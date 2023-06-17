# lab 4

# Artificial Neural Networks, Genetic Algorithm

"Artificial neural network is a set of interconnected simple processing
elements (neurons) whose functionality is based on the biological neuron
and which are used for distributed parallel information processing."

It can be used for classification or regression problems and can learn from data.

## Perceptron

Perceptron is a single layer neural network and a multi-layer perceptron is called a Neural Network.
The perceptron used in this lab is the TLU perceptron (Threshold Logic Unit) which is a linear classifier.

The value of each input x_i is multiplied by the sensitivity of the input w_i and accumulated in the body.
Then, a bias term w_0 is added to the total sum.
This value is then transformed using the transfer function (activation function) to produce the output of the neuron:
`o = f(net)`.

The function used in this lab is the sigmoid function:
`f(net) = 1 / (1 + e^(-net))`.

To enable modeling of non-linear functions, the perceptron can be stacked in layers.
The output of one layer is the input of the next layer and the output of the last layer is the output of the network.

The neural network implemented here is represented by matrices of weights and biases. This allows for efficient
computation of values that can be easily reassigned.

## Genetic Algorithm

Genetic algorithm is a search heuristic that is inspired by Charles Darwin's theory of natural evolution.
This algorithm reflects the process of natural selection where the fittest individuals are selected for reproduction
in order to produce offspring of the next generation.

From the initial population of chromosomes (possible solutions), the algorithm creates a new population using
the following steps:
1. Selection - select the fittest individuals for reproduction
2. Crossover - create new individuals by combining 2 parents
3. Mutation - mutate some individuals to maintain diversity
4. Repeat - repeat the process until the new population is ready

The specifics of these steps are described in the assignment.

#### The Algorithm

```
P = create_initial_population(POP_SIZE)
evaluate(P)
repeat_until_done:
    new_population P' = ∅
    repeat_while size(P') < POP_SIZE
        select R1 and R2 from P
        {D1, D2} = crossover(R1, R2)
        mutate D1, mutate D2
        add D1 and D2 into P'
    end_repeat
    P = P'
    evaluate(P)
end_repeat
```
