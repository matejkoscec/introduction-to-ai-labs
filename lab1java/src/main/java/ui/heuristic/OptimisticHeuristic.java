package ui.heuristic;

import ui.Config;
import ui.Solution;
import ui.model.Graph;
import ui.model.Node;

import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

public class OptimisticHeuristic {

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
}
