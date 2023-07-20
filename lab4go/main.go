package main

import (
	"encoding/csv"
	"fmt"
	"os"
	"strconv"
	"time"
)

func main() {
	startNano := time.Now().UnixNano()

	for _, config := range Tests {
		header, data := readFile("files/" + config.TrainFileName)
		_, testData := readFile("files/" + config.TestFileName)

		nn := RunGeneticAlgorithm(header, data, &config)
		nn.Test(header, testData)
	}

	fmt.Println("Milliseconds:", (time.Now().UnixNano()-startNano)/1000000)
}

func readFile(fileName string) ([]string, [][]float64) {
	f, err := os.Open(fileName)
	if err != nil {
		panic(err)
	}
	defer f.Close()

	csvReader := csv.NewReader(f)
	data, err := csvReader.ReadAll()
	if err != nil {
		panic(err)
	}

	return data[0], convertToFloat(data[1:])
}

func convertToFloat(data [][]string) [][]float64 {
	converted := make([][]float64, len(data))

	for i, row := range data {
		converted[i] = make([]float64, len(row))
		for j, val := range row {
			converted[i][j], _ = strconv.ParseFloat(val, 64)
		}
	}

	return converted
}
