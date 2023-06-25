package ui;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import java.util.List;
import java.util.Locale;

public class NeuralNetwork {

    private final RealMatrix[] weights;

    private final RealMatrix[] biases;

    private double mse;

    public NeuralNetwork(int inputLayerSize, List<Integer> hiddenLayerSizes, int outputLayerSize) {
        weights = new RealMatrix[hiddenLayerSizes.size() + 1];
        biases = new RealMatrix[hiddenLayerSizes.size() + 1];

        final var normalDistribution = new NormalDistribution(0, 0.01);
        for (var i = 0; i < hiddenLayerSizes.size(); i++) {
            var rows = hiddenLayerSizes.get(i);
            var columns = i == 0 ? inputLayerSize : hiddenLayerSizes.get(i - 1);

            var weight = createRandomRealMatrix(rows, columns, normalDistribution);
            var bias = createRandomRealMatrix(rows, 1, normalDistribution);

            weights[i] = weight;
            biases[i] = bias;
        }

        var weight = createRandomRealMatrix(
                outputLayerSize,
                hiddenLayerSizes.get(hiddenLayerSizes.size() - 1),
                normalDistribution
        );
        var bias = createRandomRealMatrix(outputLayerSize, 1, normalDistribution);

        weights[weights.length - 1] = weight;
        biases[biases.length - 1] = bias;

        // System.out.println("weights");
        // System.out.println(format(weights));
        // System.out.println("biases");
        // System.out.println(format(biases));
    }

    public NeuralNetwork(RealMatrix[] weights, RealMatrix[] biases) {
        this.weights = weights;
        this.biases = biases;
    }

    public void fit(String[] header, double[][] data) {
        var sum = 0.0;

        // .parallelStream() for larger datasets
        for (var row : data) {
            var startInputs = MatrixUtils.createRealMatrix(header.length - 1, 1);
            for (var i = 0; i < header.length - 1; i++) {
                startInputs.setEntry(i, 0, row[i]);
            }

            var y = weights[0].multiply(startInputs);
            addBiasAndApplySigmoid(y, biases[0]);

            for (int i = 1; i < weights.length - 1; i++) {
                y = weights[i].multiply(y);
                addBiasAndApplySigmoid(y, biases[i]);
            }

            y = weights[weights.length - 1].multiply(y).add(biases[biases.length - 1]);

            var val = row[row.length - 1] - y.getEntry(0, 0);
            sum += val * val;
        }

        mse = (1.0 / data.length) * sum;
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

    public RealMatrix[] getWeights() {
        return weights;
    }

    public RealMatrix[] getBiases() {
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
