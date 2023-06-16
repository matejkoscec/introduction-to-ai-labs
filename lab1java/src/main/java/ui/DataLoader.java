package ui;

import ui.model.Graph;
import ui.model.Node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataLoader {

    private final String dataPath;

    public DataLoader(String dataPath) {
        this.dataPath = dataPath;
    }

    public Graph loadStates(String fileName) {
        final var lines = readFile(fileName);

        final var graph = new Graph();
        int i = 0;

        for (final var line : lines) {
            if (line.charAt(0) == '#') {
                continue;
            }

            var split = line.split(":");
            var nodeId = split[0];

            if (i == 0) {
                i++;
                var node = new Node(nodeId);
                graph.setStartState(node);
                graph.nodes.put(nodeId, node);

                continue;
            }
            if (i == 1) {
                i++;
                final var nodes = Arrays.stream(nodeId.split(" "))
                    .map(Node::new)
                    .collect(Collectors.toList());
                graph.setEndStates(nodes);
                for (final var node : nodes) {
                    graph.nodes.put(node.getId(), node);
                }

                continue;
            }

            var node = graph.getNodeById(nodeId).orElseGet(() -> {
                final var n = new Node(nodeId);
                graph.nodes.put(nodeId, n);
                return n;
            });

            if (split.length == 1) {
                continue;
            }
            final var connectedNodes = split[1].trim();

            for (final var n : connectedNodes.split(" ")) {
                split = n.split(",");
                final var id = split[0].trim();
                final var cost = Double.parseDouble(split[1]);

                graph.getNodeById(id).ifPresentOrElse(
                    existing -> node.getConnectedNodes().put(existing, cost),
                    () -> {
                        final Node newNode = new Node(id);
                        graph.nodes.put(id, newNode);
                        node.getConnectedNodes().put(newNode, cost);
                    }
                );
            }
        }

        return graph;
    }

    public void loadHeuristicValues(String fileName, Graph graph) {
        final var lines = readFile(fileName);

        for (final var line : lines) {
            final var split = line.split(": ");
            final var nodeId = split[0];
            final var heuristicValue = Double.parseDouble(split[1]);
            graph.getNodeById(nodeId).ifPresent(node -> node.setHeuristicValue(heuristicValue));
        }
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
