package algorithm;

import configuration.Configuration;
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

public class TheOptimizers
		implements ContestSubmission
{
	public static Random rnd_;
	public static long rnd_seed;
	static ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private Configuration config;
	
	public TheOptimizers()
	{
		rnd_ = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algorithms random process
		rnd_.setSeed(seed);
		rnd_seed = seed;
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		System.out.println("Set evaluation "+evaluation);
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
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }

    public void setConfig(Configuration config){
		this.config = config;
	}
    
	public void run()
	{
		// Run your algorithm here
        
        int evals = 0;
        EvolutionaryCycle eval_cycle = new EvolutionaryCycle(config);
        // init population
        // calculate fitness
		System.out.println("Run");
        while(evals<evaluations_limit_){
            eval_cycle.run_single_cycle();
            evals++;
            if(evals % 10000 == 0){
            	System.out.println("Evaluated " + evals + " steps");
			}
        }
        System.out.print("Best solution: ");
        for(double v: eval_cycle.getBestSolution()){
        	System.out.print(v + ", ");
		}
		System.out.println("");
		System.out.println("Best fitness: "+eval_cycle.getBestFitness());
		eval_cycle.logResults();
	}

}
