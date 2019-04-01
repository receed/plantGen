import org.jblas.DoubleFunction;
import org.jblas.DoubleMatrix;

import java.util.Arrays;

public class Model {
    int layers;
    int neuronCount[];
    DoubleMatrix values[];
    DoubleMatrix weights[];
    DoubleFunction activate = Model::leakyReLu;
    static double leakyReLu(double x) {
        return x > 0 ? x : x * 0.01;
    }
    static double logistic(double x) {
        return 1 / (1 + Math.exp(-x));
    }


    Model(int... neuronCount) {
        this.neuronCount = neuronCount;
        layers = neuronCount.length - 1;
        values = new DoubleMatrix[layers + 1];
        values[0] = new DoubleMatrix(neuronCount[0]);
        weights = new DoubleMatrix[layers];
        for (int i = 0; i < layers; i++)
            weights[i] = new DoubleMatrix(neuronCount[i + 1], neuronCount[i]);

    }

    double[] apply(double ...input) {
        values[0] = new DoubleMatrix(input);
        for (int i = 0; i < layers; i++) {
            values[i + 1] = weights[i].mmul(values[i]);
            if (i < neuronCount.length - 2)
                for (int j = 0; j < neuronCount[i + 1]; j++)
                    values[i + 1].put(j, 0, activate.compute(values[i + 1].get(j, 0)));
        }
        return values[layers].toArray();
    }

    Model recombinate(Model other) {
        assert(Arrays.equals(neuronCount, other.neuronCount));
        Model result = new Model(neuronCount);
        for (int i = 0; i < layers; i++)
            for (int j = 0; j < neuronCount[i]; j++)
                for (int k = 0; k < neuronCount[i + 1]; k++) {
                    double v1 = weights[i].get(j, k), v2 = other.weights[i].get(j, k);
                    double c1 = logistic(Main.random.nextDouble() * 8 - 4);
                    double v3 = v1 * c1 + v2 * (1 - c1);
                    result.weights[i].put(j, k, v3 * (1 + Main.random.nextGaussian() * 0.1));
                }
        return result;
    }

    void randomInit() {
        for (int i = 0; i < layers; i++)
            for (int j = 0; j < neuronCount[i]; j++)
                for (int k = 0; k < neuronCount[i + 1]; k++)
                    weights[i].put(j, k, Main.random.nextDouble() * 2 - 1);
    }

    void mutate() {
        for (int i = 0; i < layers; i++)
            for (int j = 0; j < neuronCount[i]; j++)
                for (int k = 0; k < neuronCount[i + 1]; k++)
                    weights[i].put(j, k, weights[i].get(j, k) * (1 + Main.random.nextGaussian() * 0.1));

    }

}
