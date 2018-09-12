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
		ExampleConfig config = new ExampleConfig(100, 10, 2, 0.2);
		ContestEvaluation eval = createEval(EvalType.SCHAFFERS);
		executeExperiment(config, eval);

//		SwipeFunction func = (double swipe_val) -> new ExampleConfig((int)Math.round(swipe_val),
//						((int)Math.round(swipe_val)) / 10,
//						2);
//		SwipeFunction func = (double swipe_val) -> (new ExampleConfig(100,
//				10,
//				2, swipe_val, "swipe_" + swipe_val));
//		swipeExperiment(eval, func, 0.1, 0.1, 10, false);
	}

	private static void executeExperiment(Configuration config, ContestEvaluation eval){
		TheOptimizers a = new TheOptimizers();
		a.setSeed(1);
		a.setEvaluation(eval);
		a.setConfig(config);
		a.run();
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
			Thread t = new Thread(()->executeExperiment(new_config, eval));
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
