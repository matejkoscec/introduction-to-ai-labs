package ui;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class Solution {

	private static final DataLoader DATA_LOADER = new DataLoader("files/");

	private static final Map<String, BiConsumer<String, Config>> ARG_CONSUMERS = Map.of(
		"--train", (s, config) -> config.setTrainFileName(s),
		"--test", (s, config) -> config.setTestFileName(s),
		"--nn", (s, config) -> config.setNnArchitecture(s),
		"--popsize", (s, config) -> config.setPopSize(Integer.parseInt(s)),
		"--elitism", (s, config) -> config.setElitism(Integer.parseInt(s)),
		"--p", (s, config) -> config.setChromosomeChangeProbability(Double.parseDouble(s)),
		"--K", (s, config) -> config.setGaussStdDev(Double.parseDouble(s)),
		"--iter", (s, config) -> config.setIterations(Integer.parseInt(s)),
		"--multiThreaded", (s, config) -> config.setThreadPoolSize(Integer.parseInt(s))
	);

	private static final List<String> TESTS = List.of(
		"--train sine_train.txt --test sine_test.txt --nn 5s --popsize 10 --elitism 1 --p 0.1 --K 0.1 --iter 2000 --multiThreaded 10",
		"--train sine_train.txt --test sine_test.txt --nn 20s --popsize 10 --elitism 1 --p 0.7 --K 0.1 --iter 2000 --multiThreaded 10",
		"--train sine_train.txt --test sine_test.txt --nn 5s5s --popsize 10 --elitism 1 --p 0.7 --K 0.1 --iter 2000 --multiThreaded 10",
		"--train rastrigin_train.txt --test rastrigin_test.txt --nn 5s --popsize 10 --elitism 1 --p 0.3 --K 0.5 --iter 2000 --multiThreaded 10",
		"--train rosenbrock_train.txt --test rosenbrock_test.txt --nn 5s --popsize 10 --elitism 1 --p 0.5 --K 4. --iter 2000 --multiThreaded 10"
	);

	public static void main(String[] args) {
		final var start = System.currentTimeMillis();
		for (var test : TESTS) {
			run(test.split(" "));
		}
		System.out.println("Delta: " + (System.currentTimeMillis() - start) + "ms");
	}

    private static void run(String[] args) {
		final var config = new Config();
        for (var i = 0; i < args.length; i++) {
            var consumer = ARG_CONSUMERS.get(args[i]);
            if (consumer != null) {
                consumer.accept(args[i + 1], config);
            }
        }

        final var dataset = DATA_LOADER.readDataset(config.getTrainFileName());
        final var testDataset = DATA_LOADER.readDataset(config.getTestFileName());

        final var geneticAlgorithm = new GeneticAlgorithm(config);
        geneticAlgorithm.train(dataset);
        geneticAlgorithm.test(testDataset);
    }
}
