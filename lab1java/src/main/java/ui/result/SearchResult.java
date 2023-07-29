package ui.result;

import ui.model.Node;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private String algorithm;

    private boolean foundSolution;

    private int statesVisited;

    private int pathLength;

    private double totalCost;

    private List<Node> path = new ArrayList<>();

    public SearchResult() {
    }

    public SearchResult(String algorithm, boolean foundSolution, int statesVisited, Node end) {
        this.algorithm = algorithm;
        this.foundSolution = foundSolution;
        this.statesVisited = statesVisited;
        if (end == null) {
            return;
        }
        this.path = end.tracePath();
        this.pathLength = path.size();
        this.totalCost = sum();
    }

    private double sum() {
        var sum = 0.0;
        for (var i = 0; i < path.size(); i++) {
            if (i == path.size() - 1) {
                continue;
            }
            var node = path.get(i);
            var next = path.get(i + 1);
            sum += node.getConnectedNodeCost(next);
        }
        return sum;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return "# " + algorithm.toUpperCase() +
               "\n[FOUND_SOLUTION]: " + (foundSolution ? "yes" : "no") +
               "\n[STATES_VISITED]: " + statesVisited +
               "\n[PATH_LENGTH]: " + pathLength +
               "\n[TOTAL_COST]: " + Math.round(totalCost * 10.0) / 10.0 +
               "\n[PATH]: " +
               path.stream()
                   .map(Node::getId)
                   .reduce((acc, s) -> acc.concat(" => " + s))
                   .orElse("");
    }
}
