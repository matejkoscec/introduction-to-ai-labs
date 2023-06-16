package ui.algorithm.impl;

import ui.algorithm.SearchAlgorithm;
import ui.model.Node;
import ui.result.SearchResult;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BreadthFirstSearch implements SearchAlgorithm {

    @Override
    public SearchResult find(Node s0, Function<Node, List<Node>> succ, Predicate<Node> goal) {
        final Queue<Node> open = new LinkedList<>();
        open.add(s0);
        final var visited = new HashMap<String, Node>();

        while (!open.isEmpty()) {
            var n = open.remove();
            if (goal.test(n)) {
                return success(n, visited);
            }
            visited.put(n.getId(), n);
            for (var m : expand(n, succ)) {
                if (!visited.containsKey(m.getId())) {
                    open.add(m);
                }
            }
        }

        return fail(null, visited);
    }

    @Override
    public SearchResult success(Node n, Map<String, Node> visited) {
        return new SearchResult("BFS", true, visited.size() + 1, n);
    }

    @Override
    public SearchResult fail(Node n, Map<String, Node> visited) {
        return new SearchResult("BFS", false, visited.size(), n);
    }

    @Override
    public List<Node> expand(Node n, Function<Node, List<Node>> succ) {
        return succ.apply(n).stream()
            .map(node -> {
                final var newNode = new Node(node.getId());
                newNode.setConnectedNodes(node.getConnectedNodes());
                newNode.setParent(n);
                return newNode;
            })
            .collect(Collectors.toList());
    }
}
