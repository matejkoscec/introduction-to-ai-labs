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
	PreCalc []*mat.Dense
	Mse     float64
}

func NewNeuralNetwork(layers []int, header []string) *NeuralNetwork {
	nn := NeuralNetwork{
		Weights: make([]*mat.Dense, len(layers)-1),
		Biases:  make([]*mat.Dense, len(layers)-1),
		PreCalc: make([]*mat.Dense, len(layers)+1),
	}
	dist := distuv.Normal{
		Mu:    0,
		Sigma: 0.01,
	}

	nn.PreCalc[0] = mat.NewDense(len(header)-1, 1, nil)
	nn.PreCalc[1] = mat.NewDense(layers[1], 1, nil)
	for i := 0; i < len(layers)-1; i++ {
		nn.Weights[i] = mat.NewDense(layers[i+1], layers[i], nil)
		nn.Biases[i] = mat.NewDense(layers[i+1], 1, nil)
		for j := 0; j < layers[i+1]; j++ {
			for k := 0; k < layers[i]; k++ {
				nn.Weights[i].Set(j, k, dist.Rand())
			}
			nn.Biases[i].Set(j, 0, dist.Rand())
		}
		if i > 0 {
			rows, _ := nn.Weights[i].Dims()
			nn.PreCalc[i+1] = mat.NewDense(rows, 1, nil)
		}
	}
	nn.PreCalc[len(layers)] = mat.NewDense(1, 1, nil)

	return &nn
}

func NeuralNetworkWith(weights []*mat.Dense, biases []*mat.Dense, preCalc []*mat.Dense) *NeuralNetwork {
	return &NeuralNetwork{
		Weights: weights,
		Biases:  biases,
		PreCalc: preCalc,
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

	fmt.Println("PreCalc:")
	for i := 0; i < len(nn.PreCalc); i++ {
		fmt.Printf("Layer %d\n", i)
		fmt.Println(mat.Formatted(nn.PreCalc[i]))
	}
}

func (nn *NeuralNetwork) Fit(header []string, data [][]float64) {
	sum := 0.0

	for i := 0; i < len(data); i++ {
		row := data[i]

		startInputs := nn.PreCalc[0]
		for j := 0; j < len(header)-1; j++ {
			startInputs.Set(j, 0, row[j])
		}

		weight := nn.Weights[0]
		bias := nn.Biases[0]
		y := nn.PreCalc[1]

		y.Mul(weight, startInputs)
		addBiasAndApplySigmoid(y, bias)

		for j := 1; j < len(nn.Weights); j++ {
			weight = nn.Weights[j]
			bias = nn.Biases[j]

			y2 := nn.PreCalc[j+1]
			y2.Mul(weight, y)
			if j != len(nn.Weights)-1 {
				addBiasAndApplySigmoid(y2, bias)
			} else {
				y2.Add(y2, bias)
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

func addBiasAndApplySigmoid(y *mat.Dense, bias *mat.Dense) {
	rows, cols := y.Dims()
	for j := 0; j < rows; j++ {
		for k := 0; k < cols; k++ {
			y.Set(j, k, fastSigmoid(y.At(j, k)+bias.At(j, 0)))
		}
	}
}

func fastSigmoid(v float64) float64 {
	return v / (1.0 + math.Abs(v))
}

func (nn *NeuralNetwork) Test(header []string, data [][]float64) {
	nn.Fit(header, data)
	fmt.Println("Test error:", nn.Mse)
}
