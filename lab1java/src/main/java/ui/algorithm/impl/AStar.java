package ui.algorithm.impl;

import ui.algorithm.SearchAlgorithm;
import ui.model.Node;
import ui.result.SearchResult;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AStar implements SearchAlgorithm {

    /**
     * A* algorithm
     *
     * <pre>
     *function aStarSearch(s0, succ, goal, h)
     *  open ← [initial(s0)]
     *  closed ← ∅
     *  while open =/= [] do
     *      n ← removeHead(open)
     *      if goal(state(n)) then return n
     *      closed ← closed ∪ { n }
     *      for m ∈ expand(n) do
     *          if ∃m0 ∈ closed ∪ open such that state(m0) = state(m) then
     *              if g(m') < g(m) then continue
     *              else remove(m', closed ∪ open)
     *          insertSortedBy(f, m, open)
     *  return fail
     *where f(n) = g(n) + h(state(n))
     * </pre>
     */
    @Override
    public SearchResult find(Node s0, Function<Node, Collection<Node>> succ, Predicate<Node> goal) {
        final var open = new PriorityQueue<>(Comparator.comparing(Node::getF));
        open.add(s0);
        final var visited = new HashMap<String, Node>();

        while (!open.isEmpty()) {
            var n = open.remove();
            if (goal.test(n)) {
                return success(n, visited.size());
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

        return fail(null, visited.size());
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
    public SearchResult success(Node n, int statesVisited) {
        return new SearchResult("ASTAR", true, statesVisited + 1, n);
    }

    @Override
    public SearchResult fail(Node n, int statesVisited) {
        return new SearchResult("ASTAR", false, statesVisited, n);
    }

    @Override
    public List<Node> expand(Node n, Function<Node, Collection<Node>> succ) {
        return succ.apply(n).stream()
            .map(node -> {
                final var newNode = new Node(node.getId());
                newNode.setConnectedNodes(node.getConnectedNodes());
                newNode.setParent(n);
                newNode.setHeuristicValue(node.getHeuristicValue());
                newNode.setG(n.getConnectedNodeCost(node) + n.getG());
                newNode.setF(newNode.getG() + newNode.getHeuristicValue());
                return newNode;
            })
            .collect(Collectors.toList());
    }
}
