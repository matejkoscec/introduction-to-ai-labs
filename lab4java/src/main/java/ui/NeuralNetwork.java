package ui;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NeuralNetwork {

    private final List<RealMatrix> weights;

    private final List<RealMatrix> biases;

    private double mse;

    public NeuralNetwork(int inputLayerSize, List<Integer> hiddenLayerSizes, int outputLayerSize) {
        weights = new ArrayList<>();
        biases = new ArrayList<>();

        final var normalDistribution = new NormalDistribution(0, 0.01);
        for (var i = 0; i < hiddenLayerSizes.size(); i++) {
            var rows = hiddenLayerSizes.get(i);
            var columns = i == 0 ? inputLayerSize : hiddenLayerSizes.get(i - 1);

            var weight = createRandomRealMatrix(rows, columns, normalDistribution);
            var bias = createRandomRealMatrix(rows, 1, normalDistribution);

            weights.add(weight);
            biases.add(bias);
        }

        var weight = createRandomRealMatrix(
                outputLayerSize,
                hiddenLayerSizes.get(hiddenLayerSizes.size() - 1),
                normalDistribution
        );
        var bias = createRandomRealMatrix(outputLayerSize, 1, normalDistribution);

        weights.add(weight);
        biases.add(bias);

        // System.out.println("weights");
        // System.out.println(format(weights));
        // System.out.println("biases");
        // System.out.println(format(biases));
    }

    public NeuralNetwork(List<RealMatrix> weights, List<RealMatrix> biases) {
        this.weights = weights;
        this.biases = biases;
    }

    public void fit(List<String> header, List<Map<String, Double>> data, String yName) {
        var sum = 0.0;

        for (var row : data) {
            var startInputs = MatrixUtils.createRealMatrix(header.size(), 1);
            for (var i = 0; i < header.size(); i++) {
                startInputs.setEntry(i, 0, row.get(header.get(i)));
            }

            var matrix = weights.get(0);
            var bias = biases.get(0);

            var y = matrix.multiply(startInputs);
            addBiasAndApplySigmoid(y, bias);

            for (int i = 1; i < weights.size(); i++) {
                matrix = weights.get(i);
                bias = biases.get(i);

                if (i < weights.size() - 1) {
                    y = matrix.multiply(y);
                    addBiasAndApplySigmoid(y, bias);
                } else {
                    y = matrix.multiply(y).add(bias);
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

        return matrix;
    }

    private void addBiasAndApplySigmoid(RealMatrix y, RealMatrix bias) {
        for (var i = 0; i < y.getRowDimension(); i++) {
            for (var j = 0; j < y.getColumnDimension(); j++) {
                y.setEntry(i, j, fastSigmoid(y.getEntry(i, j) + bias.getEntry(i, 0)));
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

    public List<RealMatrix> getBiases() {
        return biases;
    }

    public double getMse() {
        return mse;
    }

    @Override
    public String toString() {
        return "NeuralNetwork{mse=" + mse + '}';
    }
}
