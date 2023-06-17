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

    private List<String> header;

    private String yName;

    private NeuralNetwork min;

    public GeneticAlgorithm(Config config) {
        this.config = config;
        this.random = new Random();
        this.normalDistribution = new NormalDistribution(0, config.getGaussStdDev());
        this.isMultiThreaded = config.getThreadPoolSize() >= 1 && config.getThreadPoolSize() <= 10;
        this.executor = Executors.newFixedThreadPool(isMultiThreaded ? config.getThreadPoolSize() : 1);
    }

    public void train(Dataset dataset) {
        header = new ArrayList<>(dataset.header());
        yName = header.remove(header.size() - 1);

        train(dataset.data());
        executor.shutdown();
    }

    private void train(List<Map<String, Double>> data) {
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
                var c = cross(selected.get(0), selected.get(1));
                mutate(c);
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
        min.fit(header, testDataset.data(), yName);
        System.out.printf(Locale.US, "[Test error]: %.6f%n", min.getMse());
    }

    private List<NeuralNetwork> createStartingPopulation(int popSize) {
        final var p = new ArrayList<NeuralNetwork>();

        for (var i = 0; i < popSize; i++) {
            p.add(new NeuralNetwork(
                header.size(),
                Arrays.stream(config.getNnArchitecture().split("s")).map(Integer::parseInt).toList(),
                1
            ));
        }

        return p;
    }

    private void evaluate(List<NeuralNetwork> p, List<Map<String, Double>> data) {
        if (isMultiThreaded) {
            multiThreadedEvaluation(p, data);
        } else {
            singleThreadedEvaluation(p, data);
        }
    }

    private void singleThreadedEvaluation(List<NeuralNetwork> p, List<Map<String, Double>> data) {
        for (var nn : p) {
            nn.fit(header, data, yName);
        }
    }

    private void multiThreadedEvaluation(List<NeuralNetwork> p, List<Map<String, Double>> data) {
        List<Callable<Void>> tasks = p.stream()
            .map(nn -> (Callable<Void>) () -> {
                nn.fit(header, data, yName);
                return null;
            })
            .toList();

        try {
            var results = executor.invokeAll(tasks);
            for (var result : results) {
                result.get();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private List<NeuralNetwork> select(List<NeuralNetwork> p) {
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

        return List.of(p1, p2);
    }

    private NeuralNetwork cross(NeuralNetwork p1, NeuralNetwork p2) {
        var biases = new ArrayList<RealMatrix>();
        var weights = new ArrayList<RealMatrix>();

        for (var i = 0; i < p1.getBiases().size(); i++) {
            biases.add(mean(p1.getBiases().get(i), p2.getBiases().get(i)));
            weights.add(mean(p1.getWeights().get(i), p2.getWeights().get(i)));
        }

        return new NeuralNetwork(weights, biases);
    }

    private RealMatrix mean(RealMatrix m1, RealMatrix m2) {
        var matrix = MatrixUtils.createRealMatrix(m1.getRowDimension(), m1.getColumnDimension());

        for (var i = 0; i < m1.getRowDimension(); i++) {
            for (var j = 0; j < m1.getColumnDimension(); j++) {
                matrix.setEntry(i, j, (m1.getEntry(i, j) + m2.getEntry(i, j)) / 2);
            }
        }

        return matrix;
    }

    private void mutate(NeuralNetwork c) {
        mutate(c.getBiases());
        mutate(c.getWeights());
    }

    private void mutate(List<RealMatrix> matrices) {
        for (var matrix : matrices) {
            for (var i = 0; i < matrix.getRowDimension(); i++) {
                for (var j = 0; j < matrix.getColumnDimension(); j++) {
                    if (random.nextDouble() <= config.getChromosomeChangeProbability()) {
                        matrix.setEntry(i, j, matrix.getEntry(i, j) + normalDistribution.sample());
                    }
                }
            }
        }
    }
}
