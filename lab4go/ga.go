package main

import (
	"fmt"
	"gonum.org/v1/gonum/mat"
	"gonum.org/v1/gonum/stat/distuv"
	"math/rand"
	"sort"
	"strconv"
	"strings"
	"sync"
	"time"
)

func RunGeneticAlgorithm(header []string, data [][]float64, config *Config) *NeuralNetwork {
	norm := distuv.Normal{
		Mu:    0,
		Sigma: config.GaussStdDev,
	}
	random := rand.New(rand.NewSource(time.Now().UnixNano()))

	p := createStartingPopulation(header, config.PopSize, config.NNArchitecture)
	evaluate(p, header, data)
	for i := 0; i < config.Iterations; i++ {
		if i%2000 == 0 && i != 0 {
			fmt.Printf("Train error @%v: %v\n", i, bestIn(p).Mse)
		}
		pNew := applyElitism(p, config.Elitism)
		for len(pNew) < config.PopSize {
			p1, p2 := selectParents(p, random)
			c := cross(p1, p2)
			mutate(c, config.ChromosomeChangeProb, norm, random)
			pNew = append(pNew, c)
		}
		p = pNew
		evaluate(p, header, data)
	}
	nn := *bestIn(p)
	fmt.Printf("Train error @%v: %v\n", config.Iterations, bestIn(p).Mse)

	return &nn
}

func createStartingPopulation(header []string, popSize int, arch string) []*NeuralNetwork {
	population := make([]*NeuralNetwork, popSize)

	split := strings.Split(arch, "s")
	hiddenLayers := make([]int, len(split)-1)
	for i := 0; i < len(hiddenLayers); i++ {
		hiddenLayers[i], _ = strconv.Atoi(split[i])
	}
	for i := 0; i < popSize; i++ {
		layers := make([]int, len(hiddenLayers)+2)
		layers[0] = len(header) - 1
		for j := 0; j < len(hiddenLayers); j++ {
			layers[j+1] = hiddenLayers[j]
		}
		layers[len(layers)-1] = 1
		population[i] = NewNeuralNetwork(layers)
	}

	return population
}

func evaluate(p []*NeuralNetwork, header []string, data [][]float64) {
	var wg sync.WaitGroup

	for _, nn := range p {
		wg.Add(1)
		go func(nn *NeuralNetwork) {
			defer wg.Done()
			nn.Fit(header, data)
		}(nn)
	}

	wg.Wait()
}

func bestIn(p []*NeuralNetwork) *NeuralNetwork {
	min := p[0]
	for _, nn := range p {
		if nn.Mse < min.Mse {
			min = nn
		}
	}

	return min
}

func applyElitism(p []*NeuralNetwork, elitism int) []*NeuralNetwork {
	elite := make([]*NeuralNetwork, elitism)

	sort.Slice(p, func(i, j int) bool {
		return p[i].Mse < p[j].Mse
	})
	for i := 0; i < elitism; i++ {
		elite[i] = p[i]
	}

	return elite
}

func selectParents(p []*NeuralNetwork, random *rand.Rand) (*NeuralNetwork, *NeuralNetwork) {
	sum := 0.0
	for _, nn := range p {
		sum += 1 / nn.Mse
	}

	randomNumber := random.Float64() * sum
	checkSum := 0.0
	i := -1
	for checkSum < randomNumber {
		i++
		checkSum += 1 / p[i].Mse
	}
	parent1 := p[i]

	randomNumber = random.Float64() * sum
	checkSum = 0.0
	i = -1
	for checkSum < randomNumber {
		i++
		checkSum += 1 / p[i].Mse
	}
	parent2 := p[i]

	return parent1, parent2
}

func cross(p1 *NeuralNetwork, p2 *NeuralNetwork) *NeuralNetwork {
	weights := make([]*mat.Dense, len(p1.Weights))
	biases := make([]*mat.Dense, len(p1.Biases))

	for i := 0; i < len(p1.Weights); i++ {
		weights[i] = mean(p1.Weights[i], p2.Weights[i])
		biases[i] = mean(p1.Biases[i], p2.Biases[i])
	}

	return NeuralNetworkWith(weights, biases)
}

func mean(m1 *mat.Dense, m2 *mat.Dense) *mat.Dense {
	rows, cols := m1.Dims()
	matrix := mat.NewDense(rows, cols, nil)
	data1 := m1.RawMatrix().Data
	data2 := m2.RawMatrix().Data

	for i := 0; i < rows; i++ {
		for j := 0; j < cols; j++ {
			index := i*cols + j
			matrix.Set(i, j, (data1[index]+data2[index])/2)
		}
	}

	return matrix
}

func mutate(nn *NeuralNetwork, prob float64, norm distuv.Normal, random *rand.Rand) {
	for i := 0; i < len(nn.Weights); i++ {
		mutateMatrix(nn.Weights[i], prob, norm, random)
		mutateMatrix(nn.Biases[i], prob, norm, random)
	}
}

func mutateMatrix(m *mat.Dense, prob float64, norm distuv.Normal, random *rand.Rand) {
	rows, cols := m.Dims()
	data := m.RawMatrix().Data

	for i := 0; i < rows; i++ {
		for j := 0; j < cols; j++ {
			if random.Float64() < prob {
				index := i*cols + j
				data[index] += norm.Rand()
			}
		}
	}
}
