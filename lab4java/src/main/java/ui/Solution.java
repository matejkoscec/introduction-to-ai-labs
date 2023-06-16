package ui;

import java.util.Map;
import java.util.function.Consumer;

public final class Solution {

	private static final DataLoader DATA_LOADER = new DataLoader("files/");

	private static final Config CONFIG = new Config();

	private static final Map<String, Consumer<String>> ARG_CONSUMERS = Map.of(
		"--train", CONFIG::setTrainFileName,
		"--test", CONFIG::setTestFileName,
		"--nn", CONFIG::setNnArchitecture,
		"--popsize", arg -> CONFIG.setPopSize(Integer.parseInt(arg)),
		"--elitism", arg -> CONFIG.setElitism(Integer.parseInt(arg)),
		"--p", arg -> CONFIG.setChromosomeChangeProbability(Double.parseDouble(arg)),
		"--K", arg -> CONFIG.setGaussStdDev(Double.parseDouble(arg)),
		"--iter", arg -> CONFIG.setIterations(Integer.parseInt(arg))
	);

	public static void main(String[] args) {
		for (var i = 0; i < args.length; i++) {
			var consumer = ARG_CONSUMERS.get(args[i]);
			if (consumer != null) {
				consumer.accept(args[i + 1]);
			}
		}

		final var dataset = DATA_LOADER.readDataset(CONFIG.getTrainFileName());
		final var testDataset = DATA_LOADER.readDataset(CONFIG.getTestFileName());

		final var geneticAlgorithm = new GeneticAlgorithm(CONFIG);
		geneticAlgorithm.train(dataset);
		geneticAlgorithm.test(testDataset);
	}
}
