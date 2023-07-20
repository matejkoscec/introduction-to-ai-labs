package main

import (
	"fmt"
	"gonum.org/v1/gonum/mat"
	"gonum.org/v1/gonum/stat/distuv"
	"math"
)

type NeuralNetwork struct {
	Weights []*mat.Dense
	Biases  []*mat.Dense
	Mse     float64
}

func NewNeuralNetwork(layers []int) *NeuralNetwork {
	nn := NeuralNetwork{
		Weights: make([]*mat.Dense, len(layers)-1),
		Biases:  make([]*mat.Dense, len(layers)-1),
	}
	dist := distuv.Normal{
		Mu:    0,
		Sigma: 0.01,
	}

	for i := 0; i < len(layers)-1; i++ {
		nn.Weights[i] = mat.NewDense(layers[i+1], layers[i], nil)
		nn.Biases[i] = mat.NewDense(layers[i+1], 1, nil)
		for j := 0; j < layers[i+1]; j++ {
			for k := 0; k < layers[i]; k++ {
				nn.Weights[i].Set(j, k, dist.Rand())
			}
			nn.Biases[i].Set(j, 0, dist.Rand())
		}
	}

	return &nn
}

func NeuralNetworkWith(weights []*mat.Dense, biases []*mat.Dense) *NeuralNetwork {
	return &NeuralNetwork{
		Weights: weights,
		Biases:  biases,
		Mse:     0.0,
	}
}

func (nn *NeuralNetwork) Print() {
	fmt.Println("Weights:")
	for i := 0; i < len(nn.Weights); i++ {
		fmt.Printf("Layer %d\n", i)
		fmt.Println(mat.Formatted(nn.Weights[i]))
	}

	fmt.Println("Biases:")
	for i := 0; i < len(nn.Biases); i++ {
		fmt.Printf("Layer %d\n", i)
		fmt.Println(mat.Formatted(nn.Biases[i]))
	}
}

func (nn *NeuralNetwork) Fit(header []string, data [][]float64) {
	sum := 0.0

	for i := 0; i < len(data); i++ {
		row := data[i]

		startInputs := mat.NewDense(len(header)-1, 1, row[:len(header)-1])

		weight := nn.Weights[0]
		bias := nn.Biases[0]

		y := mat.NewDense(weight.RawMatrix().Rows, startInputs.RawMatrix().Cols, nil)
		y.Mul(weight, startInputs)
		y.Add(y, bias)
		y.Apply(fastSigmoid, y)

		for j := 1; j < len(nn.Weights); j++ {
			weight = nn.Weights[j]
			bias = nn.Biases[j]

			y2 := mat.NewDense(weight.RawMatrix().Rows, y.RawMatrix().Cols, nil)
			y2.Mul(weight, y)
			y2.Add(y2, bias)
			if j != len(nn.Weights)-1 {
				y2.Apply(fastSigmoid, y2)
			}

			y = y2
		}

		actualY := data[i][len(header)-1]
		yValue := y.At(0, 0)
		val := actualY - yValue
		sum += val * val
	}

	nn.Mse = (1.0 / float64(len(data))) * sum
}

func fastSigmoid(i, j int, v float64) float64 {
	return v / (1.0 + math.Abs(v))
}

func (nn *NeuralNetwork) Test(header []string, data [][]float64) {
	nn.Fit(header, data)
	fmt.Println("Test error:", nn.Mse)
}
