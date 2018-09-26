package algorithm;

import configuration.Configuration;
import configuration.ExampleConfig;
import org.vu.contest.ContestEvaluation;

import evaluation.*;


/**
 * Created by phlippe on 07.09.18.
 */
public class TestAlgo
{

	public static void main(String args[]){
		ExampleConfig config = new ExampleConfig(100, 10, 2);
		ContestEvaluation eval = createEval(EvalType.KATSUURA);
		double best_score = 0.0;
		int best_seed = 0;
		for(int i=0;i<100;i++){
			System.out.println("Seed = "+i);
			double s = executeExperiment(config, eval, i);
			if(best_score < s){
				best_score = s;
				best_seed = i;
			}
		}
		System.out.println("Best seed: " + best_seed + ", Best score: " + best_score);


//		SwipeFunction func = (double swipe_val) -> new ExampleConfig((int)Math.round(swipe_val),
//						((int)Math.round(swipe_val)) / 10,
//						2);
//		SwipeFunction func = (double swipe_val) -> (new ExampleConfig(100,
//				10,
//				2, swipe_val, "swipe_" + swipe_val));
//		swipeExperiment(eval, func, 0.1, 0.1, 10, false);
	}

	private static double executeExperiment(Configuration config, ContestEvaluation eval, long seed){
		player59 a = new player59();
		a.setSeed(seed);
		a.setEvaluation(eval);
		//a.setConfig(config);
		a.run();
		return a.getBestScore();
	}

	private static void swipeExperiment(ContestEvaluation eval, SwipeFunction func, double start_val, double step_size,
										int number_steps, boolean exponential){
		double val;
		for(int i=0;i<number_steps;i++){
			if(!exponential)
				val = start_val + step_size * i;
			else
				val = start_val * Math.pow(step_size, i);
			Configuration new_config = func.swipeConfig(val);
			Thread t = new Thread(()->executeExperiment(new_config, eval,1));
			t.start();
		}
	}

	private static ContestEvaluation createEval(EvalType eval_func){
		ContestEvaluation eval;
		switch(eval_func){
			case SPHERE:
				eval = new SphereEvaluation();
				break;
			case KATSUURA:
				eval = new KatsuuraEvaluation();
				break;
			case SCHAFFERS:
				eval = new SchaffersEvaluation();
				break;
			case BENT_CIGAR:
				eval = new BentCigarFunction();
				break;
			default:
				eval = null;
				break;
		}
		return eval;
	}

}
