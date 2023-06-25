package ui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataLoader {

    private final String dataPath;

    public DataLoader(String dataPath) {
        this.dataPath = dataPath;
    }

    public Dataset readDataset(String fileName) {
        final var file = readFile(fileName);

        final var header = file.remove(0).split(",");

        final var data = file.stream()
            .map(line -> Arrays.stream(line.split(","))
                .mapToDouble(Double::parseDouble)
                .toArray()
            )
            .toArray(double[][]::new);

        return new Dataset(header, data);
    }

    public List<String> readFile(String fileName) {
        final var path = Paths.get(dataPath + fileName);

        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
