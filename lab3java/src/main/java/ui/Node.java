package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node {

    public final String name;

    public final Map<String, Node> connectedNodes;

    public final String max;

    private Node parent;

    public Node(String name, Map<String, Node> connectedNodes, String max) {
        this.name = name;
        this.connectedNodes = connectedNodes;
        this.max = max;
    }

    public boolean isLeaf() {
        return this instanceof Leaf;
    }

    public String trace() {
        final var output = new ArrayList<String>();

        trace(this, output);

        return String.join(System.lineSeparator(), output);
    }

    private void trace(Node node, List<String> output) {
        if (node.isLeaf()) {
            output.add(traceRoot(node));
            return;
        }
        for (Node connectedNode : node.connectedNodes.values()) {
            connectedNode.parent = node;
            trace(connectedNode, output);
        }
    }

    private String traceRoot(Node leaf) {
        final var path = new ArrayList<Node>();
        path.add(leaf);

        var parentNode = leaf.parent;
        while (parentNode != null) {
            path.add(parentNode);
            parentNode = parentNode.parent;
        }

        var depth = 1;
        final var sb = new StringBuilder();
        for (var i = path.size() - 1; i >= 0; i--) {
            var node = path.get(i);
            if (node.isLeaf()) {
                sb.append(" ").append(node.name);
                continue;
            }
            var label = getKeyByValue(node.connectedNodes, path.get(i - 1));
            if (depth > 1) {
                sb.append(" ");
            }
            sb.append(depth++).append(":").append(node.name).append("=").append(label);
        }

        return sb.toString();
    }

    private <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Node{" +
               "name='" + name + '\'' +
               ", connectedNodes={" + connectedNodes.entrySet().stream()
                   .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue().name)).toList() +
               "}, max='" + max + '\'' +
               ", parent=" + (parent != null ? parent.name : null) +
               '}';
    }
}
