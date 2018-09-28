package algorithm;

import individuals.Population;

public interface EvolutionaryAlgorithm {

    void run_single_cycle();

    int getEvalsPerCycle();

    double[] getBestSolution();

    double getBestFitness();

    Population getPopulation();

    void logResults();

    String getLogString();

    String getName();

    void initialize();

    void addTracer(Tracer tracer);

    void writeTraceFiles();
}
