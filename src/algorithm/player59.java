package algorithm;

import configuration.Configuration;
import configuration.SelfAdaptationConfig;
import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;

public class player59
		implements ContestSubmission
{
	public static Random rnd_;
	public static long rnd_seed;
	public static final boolean SILENT_RUN = true;
	static ContestEvaluation evaluation_;
    private int evaluations_limit_;
    private Configuration config;
    private double best_score;
	
	public player59()
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
        if(isMultimodal && hasStructure){
        	// Schaffers
			setConfig(new SelfAdaptationConfig(100, 10, 2, 0.5));
        }else{
        	if(isMultimodal && !hasStructure){
        		// Katsuura. Best: seed 13, score: 9.992
				setConfig(new SelfAdaptationConfig(1000, 100, 2, 0.01));
			}
			else{
				setConfig(new SelfAdaptationConfig(100, 10, 2, 0.01));
			}
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
            evals += config.getNumberOfRecombinations() * config.getParentArity();
            if(evals % 10000 == 0){
            	//System.out.println("Evaluated " + evals + " steps");
			}
        }
        System.out.print("Best solution: ");
        for(double v: eval_cycle.getBestSolution()){
        	System.out.print(v + ", ");
		}
		System.out.println("");
		System.out.println("Best fitness: "+eval_cycle.getBestFitness());
		eval_cycle.logResults();
		best_score = eval_cycle.getBestFitness();
	}

	public static void print(String s){
		if(!SILENT_RUN){
			System.out.print(s);
		}
	}

	public static void println(String s){
    	if(!SILENT_RUN){
    		System.out.println(s);
		}
	}

	public double getBestScore(){
		return best_score;
	}

	public static void main(String args[]){

	}

}
