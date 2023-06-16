package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Solution {

    public static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final DataLoader DATA_LOADER = new DataLoader("files/");

    public static void main(String[] args) {
        final var fileName = args[0];
        final var testFileName = args[1];
        final var maxDepth = args.length == 3 ? Integer.parseInt(args[2]) : null;

        final var dataset = DATA_LOADER.readDataset(fileName);
        final var testDataset = DATA_LOADER.readDataset(testFileName);

        final var model = new ID3();
        model.setMaxDepth(maxDepth);
        model.fit(dataset);
        model.predict(testDataset);
    }
}
