package algorithm;

import individuals.Population;

public interface EvolutionaryAlgorithm {

    void run_single_cycle();

    double[] getBestSolution();

    double getBestFitness();

    Population getPopulation();

    void logResults();
}
