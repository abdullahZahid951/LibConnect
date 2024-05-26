package com.example.smd_project

class NeuralNetwork(private val learningRate: Double, private val lambda: Double) {
    private val inputSize = 3
    private val hiddenSize = 4
    private val outputSize = 1
    private var hiddenWeights = Array(hiddenSize) { DoubleArray(inputSize) { Math.random() } }
    private var hiddenBiases = DoubleArray(hiddenSize) { Math.random() }
    private var outputWeights = Array(outputSize) { DoubleArray(hiddenSize) { Math.random() } }
    private var outputBiases = DoubleArray(outputSize) { Math.random() }

    private fun sigmoid(x: Double): Double = 1.0 / (1.0 + Math.exp(-x))

    private fun forward(input: DoubleArray): Pair<DoubleArray, Double> {
        val hiddenActivations = DoubleArray(hiddenSize) { i ->
            sigmoid(input.zip(hiddenWeights[i]).sumOf { (x, w) -> x * w } + hiddenBiases[i])
        }
        val output = sigmoid(hiddenActivations.zip(outputWeights[0]).sumOf { (a, w) -> a * w } + outputBiases[0])
        return Pair(hiddenActivations, output)
    }

    fun train(data: List<BookDataClass>) {
        val numEpochs = 10
        repeat(numEpochs) { epoch ->
            data.forEach { book ->
                val input = doubleArrayOf(
                    book.autherOfTheBook.hashCode().toDouble(),
                    book.selectedBookGenre.hashCode().toDouble(),
                    book.timestamp.toDouble()
                )
                val (hiddenActivations, output) = forward(input)
                val label = book.ratting.toDouble()
                val loss = 0.5 * (output - label) * (output - label)

                val gradient = output - label

                for (j in outputWeights.indices) {
                    for (k in outputWeights[j].indices) {
                        val weightGradient = gradient * hiddenActivations[k]
                        val regularizationTerm = lambda * outputWeights[j][k]
                        outputWeights[j][k] -= learningRate * (weightGradient + regularizationTerm)
                    }
                    outputBiases[j] -= learningRate * gradient
                }

                for (j in hiddenWeights.indices) {
                    for (k in hiddenWeights[j].indices) {
                        val weightGradient = gradient * outputWeights[0][j] * hiddenActivations[j] * (1 - hiddenActivations[j]) * input[k]
                        val regularizationTerm = lambda * hiddenWeights[j][k]
                        hiddenWeights[j][k] -= learningRate * (weightGradient + regularizationTerm)
                    }
                    hiddenBiases[j] -= learningRate * gradient * outputWeights[0][j] * hiddenActivations[j] * (1 - hiddenActivations[j])
                }
            }
        }
    }

    fun predictAndSort(allBooksList: List<BookDataClass>): List<Pair<BookDataClass, Double>> {
        return allBooksList.map { book ->
            val input = doubleArrayOf(
                book.autherOfTheBook.hashCode().toDouble(),
                book.selectedBookGenre.hashCode().toDouble(),
                book.timestamp.toDouble()
            )
            val (_, output) = forward(input)
            book to output
        }.sortedByDescending { it.second }
    }
}