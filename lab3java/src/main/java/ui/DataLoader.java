package ui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class DataLoader {

    private final String dataPath;

    public DataLoader(String dataPath) {
        this.dataPath = dataPath;
    }

    public Dataset readDataset(String filename) {
        final var file = readFile(filename);

        final var header = Arrays.stream(file.remove(0).split(",")).toList();

        final var data = file.stream()
            .map(line -> line.split(","))
            .map(line -> IntStream.range(0, header.size())
                .boxed()
                .collect(Collectors.toMap(header::get, v -> line[v]))
            )
            .toList();

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
