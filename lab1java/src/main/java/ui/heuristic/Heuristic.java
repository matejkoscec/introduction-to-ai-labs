package ui.heuristic;

import ui.Config;
import ui.Solution;
import ui.model.Graph;
import ui.model.Node;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Heuristic {

    private Heuristic() {
    }

    public static void isOptimistic(Graph graph, Config config) {
        final var sorted = graph.nodes.values().stream()
            .sorted(Comparator.comparing(Node::getId))
            .collect(Collectors.toList());

        Solution.out.println("# HEURISTIC-OPTIMISTIC " + config.getHeuristicsFilePath());
        var isOptimistic = true;
        for (var start : sorted) {
            final var searchResult = Solution.ALGORITHMS.get("ucs").find(
                start,
                node -> node.getConnectedNodes().keySet()
                    .stream()
                    .sorted(Comparator.comparing(Node::getId))
                    .collect(Collectors.toList()),
                node -> graph.getEndStates().stream().anyMatch(n -> n.getId().equals(node.getId()))
            );
            var isOpt = start.getHeuristicValue() <= searchResult.getTotalCost();
            if (!isOpt) {
                isOptimistic = false;
            }

            Solution.out.printf(
                Locale.US,
                "[CONDITION]: [%s] h(%s) <= h*: %.1f <= %.1f%n",
                isOpt ? "OK" : "ERR",
                start.getId(),
                start.getHeuristicValue(),
                searchResult.getTotalCost()
            );
        }

        Solution.out.println("[CONCLUSION]: Heuristic " + (isOptimistic ? "is" : "is not") + " optimistic.");
    }

    public static void isConsistent(Graph graph, Config config) {
        final List<Node> sorted = graph.nodes.values().stream()
            .sorted(Comparator.comparing(Node::getId))
            .collect(Collectors.toList());

        Solution.out.println("# HEURISTIC-CONSISTENT " + config.getHeuristicsFilePath());
        boolean isConsistent = true;
        for (final Node node : sorted) {
            for (final var entry : node.getConnectedNodes().entrySet()) {
                final Node n = entry.getKey();
                final double cost = entry.getValue();

                final boolean isCon = node.getHeuristicValue() <= n.getHeuristicValue() + cost;
                if (!isCon) {
                    isConsistent = false;
                }

                Solution.out.printf(
                    Locale.US,
                    "[CONDITION]: [%s] h(%s) <= h(%s) + c: %.1f <= %.1f + %.1f%n",
                    isCon ? "OK" : "ERR",
                    node.getId(),
                    n.getId(),
                    node.getHeuristicValue(),
                    n.getHeuristicValue(),
                    cost
                );
            }
        }

        Solution.out.println("[CONCLUSION]: Heuristic " + (isConsistent ? "is" : "is not") + " consistent.");
    }
}
