package ui;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SlightlyFasterNeuralNetwork {

    private final List<RealMatrix> weights;

    private double mse;

    public SlightlyFasterNeuralNetwork(int inputLayerSize, List<Integer> hiddenLayerSizes, int outputLayerSize) {
        weights = new ArrayList<>();

        final var normalDistribution = new NormalDistribution(0, 0.01);
        for (var i = 0; i < hiddenLayerSizes.size(); i++) {
            var rows = hiddenLayerSizes.get(i);
            var columns = i == 0 ? inputLayerSize : hiddenLayerSizes.get(i - 1);

            var weight = createRandomRealMatrix(rows + 1, columns + 1, normalDistribution);
            weights.add(weight);
        }

        var weight = createRandomRealMatrix(
                outputLayerSize + 1,
                hiddenLayerSizes.get(hiddenLayerSizes.size() - 1) + 1,
                normalDistribution
        );

        weights.add(weight);

        // System.out.println("weights");
        // System.out.println(format(weights));
    }

    public SlightlyFasterNeuralNetwork(List<RealMatrix> weights) {
        this.weights = weights;
    }

    public void fit(List<String> header, List<Map<String, Double>> data, String yName) {
        var sum = 0.0;

        for (var row : data) {
            var startInputs = MatrixUtils.createRealMatrix(header.size() + 1, 1);
            for (var i = 0; i < header.size(); i++) {
                startInputs.setEntry(i, 0, row.get(header.get(i)));
            }
            startInputs.setEntry(header.size(), 0, 1.0);

            var matrix = weights.get(0);

            var y = matrix.multiply(startInputs);
            sigmoid(y);

            for (int i = 1; i < weights.size(); i++) {
                matrix = weights.get(i);

                y = matrix.multiply(y);
                if (i < weights.size() - 1) {
                    sigmoid(y);
                }
            }

            var actualY = row.get(yName);
            var yVal = y.getEntry(0, 0);
            sum += FastMath.pow(actualY - yVal, 2);
        }

        mse = (1.0 / data.size()) * sum;
    }

    private RealMatrix createRandomRealMatrix(int rows, int columns, NormalDistribution normalDistribution) {
        var matrix = MatrixUtils.createRealMatrix(rows, columns);

        for (var i = 0; i < matrix.getRowDimension(); i++) {
            for (var j = 0; j < matrix.getColumnDimension(); j++) {
                matrix.setEntry(i, j, normalDistribution.sample());
            }
        }
        for (var j = 0; j < matrix.getColumnDimension(); j++) {
            matrix.setEntry(matrix.getRowDimension() - 1, j, 1.0);
        }

        return matrix;
    }

    private void sigmoid(RealMatrix y) {
        for (var i = 0; i < y.getRowDimension(); i++) {
            for (var j = 0; j < y.getColumnDimension(); j++) {
                y.setEntry(i, j, fastSigmoid(y.getEntry(i, j)));
            }
        }
    }

    private double fastSigmoid(double x) {
        return x / (1 + FastMath.abs(x));
    }

    private String format(List<RealMatrix> matrices) {
        return String.join("\n", matrices.stream().map(this::format).toList());
    }

    private String format(RealMatrix realMatrix) {
        var sb = new StringBuilder();

        sb.append("+");
        sb.append(("-".repeat(9) + "+").repeat(realMatrix.getColumnDimension()));
        sb.append("\n");
        for (var row : realMatrix.getData()) {
            for (var column : row) {
                sb.append(String.format(Locale.US, column > 0 ? "| %.5f" : "|%.5f", column)).append(" ");
            }
            sb.append("|").append("\n");
        }
        sb.append("+");
        sb.append(("-".repeat(9) + "+").repeat(realMatrix.getColumnDimension()));
        sb.append("\n");

        return sb.toString();
    }

    public List<RealMatrix> getWeights() {
        return weights;
    }

    public double getMse() {
        return mse;
    }

    @Override
    public String toString() {
        return "NeuralNetwork{mse=" + mse + '}';
    }
}
