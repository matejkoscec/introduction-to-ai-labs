package ui.algorithm.impl;

import ui.algorithm.SearchAlgorithm;
import ui.model.Node;
import ui.result.SearchResult;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AStar implements SearchAlgorithm {

    @Override
    public SearchResult find(Node s0, Function<Node, List<Node>> succ, Predicate<Node> goal) {
        final var open = new PriorityQueue<>(Comparator.comparing(Node::getF));
        open.add(s0);
        final var visited = new HashMap<String, Node>();

        while (!open.isEmpty()) {
            var n = open.remove();
            if (goal.test(n)) {
                return success(n, visited);
            }
            visited.put(n.getId(), n);
            for (var m : expand(n, succ)) {
                if (visited.containsKey(m.getId()) || open.contains(m)) {
                    var m_ = getM_(m.getId(), open, visited);
                    if (m_.getG() < m.getG()) {
                        continue;
                    } else {
                        remove(m_, open, visited);
                    }
                }
                open.add(m);
            }
        }

        return fail(null, visited);
    }

    public void remove(Node m_, Queue<Node> open, Map<String, Node> visited) {
        open.removeIf(node -> node.getId().equals(m_.getId()));
        visited.remove(m_.getId());
    }

    public Node getM_(String id, Queue<Node> open, Map<String, Node> visited) {
        var m_ = visited.get(id);
        if (m_ != null) {
            return m_;
        }
        m_ = open.stream().filter(node -> node.getId().equals(id)).findFirst().orElseThrow();
        return m_;
    }

    @Override
    public SearchResult success(Node n, Map<String, Node> visited) {
        return new SearchResult("ASTAR", true, visited.size() + 1, n);
    }

    @Override
    public SearchResult fail(Node n, Map<String, Node> visited) {
        return new SearchResult("ASTAR", false, visited.size(), n);
    }

    @Override
    public List<Node> expand(Node n, Function<Node, List<Node>> succ) {
        return succ.apply(n).stream()
            .map(node -> {
                final var newNode = new Node(node.getId());
                newNode.setConnectedNodes(node.getConnectedNodes());
                newNode.setParent(n);
                newNode.setHeuristicValue(node.getHeuristicValue());
                newNode.setG(n.getConnectedNodeCostById(node.getId()) + n.getG());
                newNode.setF(newNode.getG() + newNode.getHeuristicValue());
                return newNode;
            })
            .collect(Collectors.toList());
    }
}
