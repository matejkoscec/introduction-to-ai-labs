package ui.algorithm;

import ui.model.Node;
import ui.result.SearchResult;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SearchAlgorithm {

    SearchResult find(Node s0, Function<Node, Collection<Node>> succ, Predicate<Node> goal);

    List<Node> expand(Node n, Function<Node, Collection<Node>> succ);

    SearchResult success(Node n, int statesVisited);

    SearchResult fail(Node n, int statesVisited);
}
