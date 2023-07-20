package main

type Config struct {
	TrainFileName        string
	TestFileName         string
	NNArchitecture       string
	PopSize              int
	Elitism              int
	ChromosomeChangeProb float64
	GaussStdDev          float64
	Iterations           int
}

var (
	Tests = []Config{
		{
			TrainFileName:        "sine_train.txt",
			TestFileName:         "sine_test.txt",
			NNArchitecture:       "5s",
			PopSize:              10,
			Elitism:              1,
			ChromosomeChangeProb: 0.1,
			GaussStdDev:          0.1,
			Iterations:           2000,
		},
		{
			TrainFileName:        "sine_train.txt",
			TestFileName:         "sine_test.txt",
			NNArchitecture:       "20s",
			PopSize:              10,
			Elitism:              1,
			ChromosomeChangeProb: 0.7,
			GaussStdDev:          0.1,
			Iterations:           2000,
		},
		{
			TrainFileName:        "sine_train.txt",
			TestFileName:         "sine_test.txt",
			NNArchitecture:       "5s5s",
			PopSize:              10,
			Elitism:              1,
			ChromosomeChangeProb: 0.7,
			GaussStdDev:          0.1,
			Iterations:           2000,
		},
		{
			TrainFileName:        "rastrigin_train.txt",
			TestFileName:         "rastrigin_test.txt",
			NNArchitecture:       "5s",
			PopSize:              10,
			Elitism:              1,
			ChromosomeChangeProb: 0.3,
			GaussStdDev:          0.5,
			Iterations:           2000,
		},
		{
			TrainFileName:        "rosenbrock_train.txt",
			TestFileName:         "rosenbrock_test.txt",
			NNArchitecture:       "5s",
			PopSize:              10,
			Elitism:              1,
			ChromosomeChangeProb: 0.5,
			GaussStdDev:          4,
			Iterations:           2000,
		},
	}
)
