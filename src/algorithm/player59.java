package algorithm;

import configuration.Configuration;
import configuration.ExampleConfig;
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

public class player59
        implements ContestSubmission {
    public static Random rnd_;
    public static long rnd_seed;
    static ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private double best_score;
    private EvolutionaryAlgorithm eval_cycle;
    private static final boolean SILENT_RUN = true;

    public player59() {
        rnd_ = new Random();
    }

    public void setSeed(long seed) {
        // Set seed of algorithms random process
        rnd_.setSeed(seed);
        rnd_seed = seed;
        player59.println("Seed: " + seed);
    }

    public void setEvaluation(ContestEvaluation evaluation) {
        player59.println("Set evaluation " + evaluation);
        // Set evaluation problem used in the run
        evaluation_ = evaluation;

        // Get evaluation properties
        Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
        // Property keys depend on specific evaluation
        // E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

        // Do sth with property values, e.g. specify relevant settings of your algorithm
        if (isMultimodal && hasStructure) {
            // Schaffers
            setConfig(new ExampleConfig(100, 10, 2, 0.5));
        } else {
            if (isMultimodal && !hasStructure) {
                // Katsuura
                setConfig(new ExampleConfig(1000, 100, 2, 0.01));
            } else {
                //BentCigar
                setConfig(new ExampleConfig(100, 10, 2, 0.01));
            }
            // Do sth else
        }
    }

    private void setConfig(Configuration config){
        setEvolutionaryAlgorithm(new EvolutionaryCycle(config));
    }

    public void setEvolutionaryAlgorithm(EvolutionaryAlgorithm ea){
        eval_cycle = ea;
    }

    public void run() {
        // Run your algorithm here

        int evals = 0;
        eval_cycle.initialize();
        // init population
        // calculate fitness
        player59.println("Run");
        while (evals < evaluations_limit_) {
            eval_cycle.run_single_cycle();
            evals += eval_cycle.getEvalsPerCycle();
            if (evals % 10000 == 0) {
                player59.println("Evaluated " + evals + " steps");
            }
        }
        player59.print("Best solution: ");
        for (double v : eval_cycle.getBestSolution()) {
            player59.print(v + ", ");
        }
        player59.println("");
        player59.println("Best fitness: " + eval_cycle.getBestFitness());
        if (!SILENT_RUN)
            eval_cycle.logResults();
        best_score = eval_cycle.getBestFitness();
        eval_cycle.writeTraceFiles();
    }

    public double getBestScore() {
        return best_score;
    }

    public static void print(Object o) {
        if (!SILENT_RUN) {
            System.out.print(o);
        }
    }

    public static void println() {
        player59.print("\n");
    }

    public static void println(Object o) {
        if (!SILENT_RUN) {
            System.out.println(o);
        }
    }

}
