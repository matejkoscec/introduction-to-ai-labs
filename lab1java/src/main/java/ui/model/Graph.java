package ui.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Graph {

    public final Map<String, Node> nodes = new HashMap<>();

    private Node startState;

    private List<Node> endStates;

    public Optional<Node> getNodeById(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    public Node getStartState() {
        return startState;
    }

    public void setStartState(Node startState) {
        this.startState = startState;
    }

    public List<Node> getEndStates() {
        return endStates;
    }

    public void setEndStates(List<Node> endStates) {
        this.endStates = endStates;
    }

    @Override
    public String toString() {
        return "Graph{" +
               "nodes=" + nodes.values().stream().map(node -> "\n\t" + node.toString()).collect(Collectors.toList()) +
               "\n, startState=" + startState +
               "\n, endState=" + endStates +
               '}';
    }
}
