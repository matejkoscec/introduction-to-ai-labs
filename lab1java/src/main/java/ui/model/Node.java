package ui.model;

import java.util.*;
import java.util.stream.Collectors;

public class Node {

    private final String id;

    private Map<Node, Double> connectedNodes = new TreeMap<>(Comparator.comparing(Node::getId));

    private Node parent;

    private double heuristicValue;

    private double g;

    private double f;

    public Node(String id) {
        this.id = id;
    }

    public double getConnectedNodeCostById(String id) {
        return connectedNodes.entrySet().stream()
            .filter(entry -> entry.getKey().getId().equals(id))
            .findFirst()
            .orElseThrow()
            .getValue();
    }

    public List<Node> tracePath() {
        final var path = new ArrayList<Node>();
        var node = this;
        path.add(node);
        while (node.getParent() != null) {
            path.add(node.getParent());
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    public String getId() {
        return id;
    }

    public Map<Node, Double> getConnectedNodes() {
        return connectedNodes;
    }

    public void setConnectedNodes(Map<Node, Double> connectedNodes) {
        this.connectedNodes = connectedNodes;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public double getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    @Override
    public String toString() {
        return "Node{" +
               "id='" + id + '\'' +
               ", connectedNodes=" +
               connectedNodes.entrySet()
                   .stream()
                   .map(entry -> "[" + entry.getKey().getId() + "," + entry.getValue() + "]")
                   .collect(Collectors.toList()) +
               ", parent=" + (parent != null ? parent.getId() : "null") +
               ", heuristicValue=" + heuristicValue +
               '}';
    }
}
