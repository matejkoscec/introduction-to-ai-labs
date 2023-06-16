package ui;

import java.util.*;
import java.util.stream.Collectors;

public class ID3 {

    private Node root;

    private final Map<String, Set<String>> featureValues = new HashMap<>();

    private int maxDepth;

    public void fit(Dataset dataset) {
        final var yName = dataset.header().get(dataset.header().size() - 1);
        final var yValues = dataset.data().stream().map(f -> f.get(yName)).collect(Collectors.toSet());
        final var header = new ArrayList<>(dataset.header());
        header.remove(yName);

        for (var label : header) {
            featureValues.put(label, dataset.data().stream()
                .map(f -> f.get(label))
                .collect(Collectors.toSet())
            );
        }

        root = id3(dataset.data(), dataset.data(), header, yValues, yName, 1);
        Solution.out.println("[BRANCHES]:");
        System.out.println(root.trace());
    }

    public void predict(Dataset testDataset) {
        final var yName = testDataset.header().get(testDataset.header().size() - 1);

        final var guesses = new ArrayList<String>();
        for (var testCase : testDataset.data()) {
            var node = root;
            var val = testCase.get(node.name);
            while (val != null) {
                var newNode = node.connectedNodes.get(val);
                if (node.isLeaf() || newNode == null) {
                    break;
                }
                node = newNode;
                val = testCase.get(node.name);
            }

            guesses.add(node.max);
        }
        Solution.out.printf("[PREDICTIONS]: %s%n", String.join(" ", guesses));

        final var actual = testDataset.data().stream().map(f -> f.get(yName)).toList();
        calculateAccuracy(guesses, actual);
        calculateConfusionMatrix(testDataset, yName, guesses, actual);
    }

    private Node id3(
        List<Map<String, String>> D,
        List<Map<String, String>> DParent,
        List<String> X,
        Set<String> y,
        String yName,
        int depth
    ) {
        if (D.isEmpty()) {
            var v = argmax_v(DParent, y, yName);
            return new Leaf(v);
        }
        var vMax = argmax_v(D, y, yName);
        if (maxDepth != -1 && depth > maxDepth) {
            return new Leaf(vMax);
        }
        if (X.isEmpty() || filterBy(D, yName, vMax).equals(D)) {
            return new Leaf(vMax);
        }
        var x = argmax_xeX(IG(D, X, y, yName));
        var subtrees = new HashMap<String, Node>();
        for (var v : V(x)) {
            var t = id3(filterBy(D, x, v), D, remove(X, x), y, yName, depth + 1);
            subtrees.put(v, t);
        }
        return new Node(x, subtrees, vMax);
    }

    private String argmax_v(List<Map<String, String>> D, Set<String> y, String yName) {
        var yOccurrences = y.stream().collect(Collectors.toMap(k -> k, v -> 0.0));

        for (var row : D) {
            yOccurrences.merge(row.get(yName), 1.0, Double::sum);
        }

        return argmax_xeX(yOccurrences);
    }

    private String argmax_xeX(Map<String, Double> igs) {
        return igs.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElseThrow();
    }

    private Map<String, Double> IG(List<Map<String, String>> D, List<String> X, Set<String> y, String yName) {
        var igs = new HashMap<String, Double>();
        var yOccurrences = y.stream().collect(Collectors.toMap(k -> k, v -> 0));

        for (var row : D) {
            yOccurrences.merge(row.get(yName), 1, Integer::sum);
        }

        var yEntropy = 0.0;
        for (var s : y) {
            var f = yOccurrences.get(s);
            var x = f / (double) D.size();
            yEntropy += -x * log2(x);
        }

        for (var feature : X) {
            var distinct = D.stream().map(f -> f.get(feature)).collect(Collectors.toSet());

            var ig = yEntropy;
            for (var d : distinct) {
                var occurrences = y.stream().collect(Collectors.toMap(k -> k, v -> 0));
                D.stream()
                    .filter(f -> f.get(feature).equals(d))
                    .forEach(row -> occurrences.merge(row.get(yName), 1, Integer::sum));

                var total = occurrences.values().stream().reduce(0, Integer::sum);
                var entropy = 0.0;
                for (var y_ : y) {
                    var f = occurrences.get(y_);
                    var x = (f / (double) total);
                    entropy += -x * log2(x);
                }

                var t = occurrences.values().stream().reduce(0, Integer::sum);
                ig -= ((double) t / D.size()) * entropy;
            }

            igs.put(feature, ig);
            Solution.out.printf("IG(%s)=%.4f ", feature, ig);
        }

        Solution.out.println();

        return igs;
    }

    private double log2(double x) {
        return x == 0 ? 0 : Math.log(x) / Math.log(2);
    }

    private Set<String> V(String x) {
        return featureValues.get(x);
    }

    private List<Map<String, String>> filterBy(List<Map<String, String>> D, String label, String v) {
        return D.stream().filter(f -> f.get(label).equals(v)).toList();
    }

    private List<String> remove(List<String> X, String x) {
        final var newX = new ArrayList<>(X);
        newX.remove(x);

        return newX;
    }

    private void calculateAccuracy(List<String> guesses, List<String> actual) {
        var correct = 0;
        for (var i = 0; i < guesses.size(); i++) {
            if (guesses.get(i).equals(actual.get(i))) {
                correct++;
            }
        }

        Solution.out.printf(Locale.US, "[ACCURACY]: %.5f%n", (double) correct / guesses.size());
    }

    private void calculateConfusionMatrix(
        Dataset testDataset,
        String yName,
        List<String> guesses,
        List<String> actual
    ) {
        final var yValues = testDataset.data().stream().map(f -> f.get(yName)).collect(Collectors.toSet());

        final var confusionMatrix = new HashMap<String, Map<String, Integer>>();
        for (var y1 : yValues) {
            var map = new HashMap<String, Integer>();
            for (var y2 : yValues) {
                map.put(y2, 0);
            }
            confusionMatrix.put(y1, map);
        }
        for (var i = 0; i < guesses.size(); i++) {
            var guess = guesses.get(i);
            var test = actual.get(i);
            if (guess.equals(test)) {
                confusionMatrix.get(guess).merge(test, 1, Integer::sum);
            } else {
                confusionMatrix.get(test).merge(guess, 1, Integer::sum);
            }
        }

        var sorted = yValues.stream().sorted().toList();
        Solution.out.println("[CONFUSION_MATRIX]:");
        for (var y1 : sorted) {
            for (var y2 : sorted) {
                Solution.out.print(confusionMatrix.get(y1).get(y2) + " ");
            }
            Solution.out.println();
        }
    }

    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth == null ? -1 : maxDepth;
    }
}
