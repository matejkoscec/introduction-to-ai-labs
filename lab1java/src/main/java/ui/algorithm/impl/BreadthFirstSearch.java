package ui.algorithm.impl;

import ui.algorithm.SearchAlgorithm;
import ui.model.Node;
import ui.result.SearchResult;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BreadthFirstSearch implements SearchAlgorithm {

    /**
     * BFS algorithm
     *
     * <pre>
     *function aStarSearch(s0, succ, goal)
     *  open ← [initial(s0)]
     *  closed ← ∅
     *  while open =/= [] do
     *      n ← removeHead(open)
     *      if goal(state(n)) then return n
     *      closed ← closed ∪ { n }
     *      for m ∈ expand(n) do
     *          insertBack(m, open)
     *  return fail
     * </pre>
     */
    @Override
    public SearchResult find(Node s0, Function<Node, Collection<Node>> succ, Predicate<Node> goal) {
        final Queue<Node> open = new LinkedList<>();
        open.add(s0);
        final var visited = new HashSet<String>();

        while (!open.isEmpty()) {
            var n = open.remove();
            if (goal.test(n)) {
                return success(n, visited.size());
            }
            visited.add(n.getId());
            for (var m : expand(n, succ)) {
                if (!visited.contains(m.getId())) {
                    open.add(m);
                }
            }
        }

        return fail(null, visited.size());
    }

    @Override
    public SearchResult success(Node n, int statesVisited) {
        return new SearchResult("BFS", true, statesVisited + 1, n);
    }

    @Override
    public SearchResult fail(Node n, int statesVisited) {
        return new SearchResult("BFS", false, statesVisited, n);
    }

    @Override
    public List<Node> expand(Node n, Function<Node, Collection<Node>> succ) {
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
