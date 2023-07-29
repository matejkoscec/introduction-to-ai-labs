package ui;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class GeneticAlgorithm {

    private final Config config;

    private final Random random;

    private final NormalDistribution normalDistribution;

    private final ExecutorService executor;

    private final boolean isMultiThreaded;

    private String[] header;

    private NeuralNetwork min;

    public GeneticAlgorithm(Config config) {
        this.config = config;
        this.random = new Random();
        this.normalDistribution = new NormalDistribution(0, config.getGaussStdDev());
        this.isMultiThreaded = config.getThreadPoolSize() > 1 && config.getThreadPoolSize() <= 10;
        this.executor = isMultiThreaded ? Executors.newFixedThreadPool(config.getThreadPoolSize()) : null;
    }

    public void train(Dataset dataset) {
        header = dataset.header();

        try {
            train(dataset.data());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isMultiThreaded) {
            executor.shutdown();
        }
    }

    private void train(double[][] data) {
        var p = createStartingPopulation(config.getPopSize());
        evaluate(p, data);
        for (var i = 0; i < config.getIterations(); i++) {
            if (i % 2000 == 0 && i > 0) {
                System.out.printf(
                    Locale.US,
                    "[Train error @%d]: %.6f%n",
                    i,
                    p.stream().min(Comparator.comparing(NeuralNetwork::getMse)).orElseThrow().getMse()
                );
            }
            var pNew = p.stream()
                .sorted(Comparator.comparing(NeuralNetwork::getMse))
                .limit(config.getElitism())
                .collect(Collectors.toList());
            while (pNew.size() < config.getPopSize()) {
                var selected = select(p);
                var c = crossAndMutate(selected.p1(), selected.p2());
                pNew.add(c);
            }
            p = pNew;
            evaluate(p, data);
        }
        min = p.stream().min(Comparator.comparing(NeuralNetwork::getMse)).orElseThrow();
        System.out.printf(
            Locale.US,
            "[Train error @%d]: %.6f%n",
            config.getIterations(),
            min.getMse()
        );
    }

    public void test(Dataset testDataset) {
        min.fit(header, testDataset.data());
        System.out.printf(Locale.US, "[Test error]: %.6f%n", min.getMse());
    }

    private List<NeuralNetwork> createStartingPopulation(int popSize) {
        final var p = new ArrayList<NeuralNetwork>();

        var hiddenLayers = Arrays.stream(config.getNnArchitecture().split("s")).map(Integer::parseInt).toList();
        for (var i = 0; i < popSize; i++) {
            p.add(new NeuralNetwork(header.length - 1, hiddenLayers, 1));
        }

        return p;
    }

    private void evaluate(List<NeuralNetwork> p, double[][] data) {
        if (isMultiThreaded) {
            multiThreadedEvaluation(
                p.stream().map(nn -> (Callable<Void>) () -> {
                    nn.fit(header, data);
                    return null;
                }).toList()
            );
        } else {
            for (var nn : p) {
                nn.fit(header, data);
            }
        }
    }

    private void multiThreadedEvaluation(List<Callable<Void>> tasks) {
        try {
            for (var future : executor.invokeAll(tasks)) {
                future.get();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    record Tuple(NeuralNetwork p1, NeuralNetwork p2) {}

    private Tuple select(List<NeuralNetwork> p) {
        var sum = p.stream().mapToDouble(NeuralNetwork::getMse).reduce(0.0, (acc, num) -> acc + (1 / num));

        var randomNum = sum * random.nextDouble();
        var checkSum = 0.0;
        var i = -1;
        while (checkSum < randomNum) {
            checkSum += 1 / p.get(++i).getMse();
        }
        var p1 = p.get(i);
        randomNum = sum * random.nextDouble();
        checkSum = 0.0;
        i = -1;
        while (checkSum < randomNum) {
            checkSum += 1 / p.get(++i).getMse();
        }
        var p2 = p.get(i);

        return new Tuple(p1, p2);
    }

    private NeuralNetwork crossAndMutate(NeuralNetwork p1, NeuralNetwork p2) {
        var biases = new RealMatrix[p1.getBiases().length];
        var weights = new RealMatrix[p1.getWeights().length];

        for (var i = 0; i < p1.getWeights().length; i++) {
            biases[i] = (meanAndMutate(p1.getBiases()[i], p2.getBiases()[i]));
            weights[i] = (meanAndMutate(p1.getWeights()[i], p2.getWeights()[i]));
        }

        return new NeuralNetwork(weights, biases);
    }

    private RealMatrix meanAndMutate(RealMatrix m1, RealMatrix m2) {
        var matrix = MatrixUtils.createRealMatrix(m1.getRowDimension(), m1.getColumnDimension());

        for (var i = 0; i < m1.getRowDimension(); i++) {
            for (var j = 0; j < m1.getColumnDimension(); j++) {
                var mean = (m1.getEntry(i, j) + m2.getEntry(i, j)) / 2;
                if (random.nextDouble() < config.getChromosomeChangeProbability()) {
                    matrix.setEntry(i, j, mean + normalDistribution.sample());
                } else {
                    matrix.setEntry(i, j, mean);
                }
            }
        }

        return matrix;
    }
}
