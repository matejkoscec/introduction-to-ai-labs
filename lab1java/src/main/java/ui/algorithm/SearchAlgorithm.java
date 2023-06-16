package ui.algorithm;

import ui.model.Node;
import ui.result.SearchResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SearchAlgorithm {

    SearchResult find(Node s0, Function<Node, List<Node>> succ, Predicate<Node> goal);

    List<Node> expand(Node n, Function<Node, List<Node>> succ);

    SearchResult success(Node n, Map<String, Node> visited);

    SearchResult fail(Node n, Map<String, Node> visited);
}
