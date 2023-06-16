package ui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public final class DataLoader {

    private final String dataPath;

    public DataLoader(String dataPath) {
        this.dataPath = dataPath;
    }

    public List<Set<String>> loadClauses(String fileName) {
        final var clauses = new ArrayList<Set<String>>(new HashSet<>());

        for (var l : readFile(fileName)) {
            var line = l.toLowerCase();

            var split = line.split(" v ");
            var clause = new HashSet<>(Arrays.asList(split));

            clauses.add(clause);
        }

        return clauses;
    }

    public List<UserInput> loadUserInputs(String fileName) {
        final var userInputs = new ArrayList<UserInput>();

        for (var l : readFile(fileName)) {
            var line = l.toLowerCase();

            var command = line.charAt(line.length() - 1);

            var split = line.replace(" " + command, "").split(" v ");
            var clause = new HashSet<>(Arrays.asList(split));

            userInputs.add(new UserInput(clause, command));
        }

        return userInputs;
    }

    private List<String> readFile(String fileName) {
        final var path = Paths.get(dataPath + fileName);

        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return List.of();
    }
}
