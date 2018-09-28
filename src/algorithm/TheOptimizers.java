package algorithm;

import configuration.Configuration;
import configuration.ExampleConfig;
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

public class TheOptimizers
        implements ContestSubmission {
    public static Random rnd_;
    public static long rnd_seed;
    static ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private Configuration config;
    private double best_score;
    private static final boolean SILENT_RUN = true;

    public TheOptimizers() {
        rnd_ = new Random();
    }

    public void setSeed(long seed) {
        // Set seed of algorithms random process
        rnd_.setSeed(seed);
        rnd_seed = seed;
    }

    public void setEvaluation(ContestEvaluation evaluation) {
        TheOptimizers.println("Set evaluation " + evaluation);
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

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public void run() {
        // Run your algorithm here

        int evals = 0;
        EvolutionaryCycle eval_cycle = new EvolutionaryCycle(config);
        // init population
        // calculate fitness
        TheOptimizers.println("Run");
        while (evals < evaluations_limit_) {
            eval_cycle.run_single_cycle();
            evals += config.getNumberOfRecombinations() * config.getParentArity();
            if (evals % 10000 == 0) {
                TheOptimizers.println("Evaluated " + evals + " steps");
            }
        }
        TheOptimizers.print("Best solution: ");
        for (double v : eval_cycle.getBestSolution()) {
            TheOptimizers.print(v + ", ");
        }
        TheOptimizers.println("");
        TheOptimizers.println("Best fitness: " + eval_cycle.getBestFitness());
        if (!SILENT_RUN)
            eval_cycle.logResults();
        best_score = eval_cycle.getBestFitness();
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
        TheOptimizers.print("\n");
    }

    public static void println(Object o) {
        if (!SILENT_RUN) {
            System.out.println(o);
        }
    }

}
