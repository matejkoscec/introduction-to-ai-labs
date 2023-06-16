package ui;

import ui.algorithm.SearchAlgorithm;
import ui.algorithm.impl.AStar;
import ui.algorithm.impl.BreadthFirstSearch;
import ui.algorithm.impl.UniformCostSearch;
import ui.heuristic.Heuristic;
import ui.model.Node;
import ui.result.SearchResult;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Solution {

    public static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final DataLoader DATA_LOADER = new DataLoader("files/");

    private static final Config CONFIG = new Config();

    private static final Map<String, Consumer<String>> ARG_CONSUMERS = Map.of(
        "--alg", CONFIG::setAlgorithm,
        "--ss", CONFIG::setStatesFilePath,
        "--h", CONFIG::setHeuristicsFilePath,
        "--check-optimistic", arg -> CONFIG.setCheckOptimistic(true),
        "--check-consistent", arg -> CONFIG.setCheckConsistent(true)
    );

    public static final Map<String, SearchAlgorithm> ALGORITHMS = Map.of(
        "bfs", new BreadthFirstSearch(),
        "ucs", new UniformCostSearch(),
        "astar", new AStar()
    );

    public static void main(String[] args) {
        for (var i = 0; i < args.length; i += 2) {
            var consumer = ARG_CONSUMERS.get(args[i]);
            if (consumer != null) {
                if (i + 1 >= args.length) {
                    consumer.accept(null);
                } else if (args[i + 1].startsWith("--")) {
                    consumer.accept(null);
                } else {
                    consumer.accept(args[i + 1]);
                }
            }
        }

        final var graph = DATA_LOADER.loadStates(CONFIG.getStatesFilePath());
        if (CONFIG.getHeuristicsFilePath() != null) {
            DATA_LOADER.loadHeuristicValues(CONFIG.getHeuristicsFilePath(), graph);
        }

        if (CONFIG.getAlgorithm() != null) {
            final var searchAlgorithm = ALGORITHMS.get(CONFIG.getAlgorithm());

            final Function<Node, List<Node>> succ = node -> new ArrayList<>(node.getConnectedNodes().keySet());
            final Predicate<Node> goal =
                node -> graph.getEndStates().stream().anyMatch(n -> n.getId().equals(node.getId()));

            final var searchResult = searchAlgorithm == null
                ? new SearchResult()
                : searchAlgorithm.find(graph.getStartState(), succ, goal);

            out.println(searchResult);
        }
        if (CONFIG.isCheckOptimistic()) {
            Heuristic.isOptimistic(graph, CONFIG);
        }
        if (CONFIG.isCheckConsistent()) {
            Heuristic.isConsistent(graph, CONFIG);
        }
    }
}
