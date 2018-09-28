package algorithm;

import configuration.ConfigParams;
import configuration.Configuration;
import configuration.ExampleConfig;
import configuration.StandardConfig;
import org.vu.contest.ContestEvaluation;

import evaluation.*;
import selection.SurvivorSelectionType;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by phlippe on 07.09.18.
 */
public class TestAlgo
{

	public static void main(String args[]){
		int config_index = Integer.parseInt(args[0]);
		double reset_prob = 0;
		// ExampleConfig config = new ExampleConfig(100 * (int)Math.pow(config_index + 1, 2), 10 * (int)Math.pow(config_index + 1, 2), 2, 0.01, reset_prob, "");
		ConfigParams configParams = new ConfigParams(400, 40, 2);
		configParams.setParentTournamentSize(2);
		configParams.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
		configParams.setSurvivorTournamentSize(6);
		//configParams.setMutationMultiSigmaInit(0.005 + (0.001 * (config_index + 1)));
		configParams.setMutationMultiSigmaInit(0.01);
		configParams.setMutationMultiSigmaFactor(0.8);
		configParams.setName("mstau_" + Math.round(configParams.getMutationMultiSigmaFactor() * 1000.0) / 1000.0);
		//configParams.setName("tau_" + (config_index + 1) * 2);
		StandardConfig config = new StandardConfig(configParams);
		ContestEvaluation eval = createEval(EvalType.KATSUURA);
		//executeExperiment(null, eval);
        swipeSeeds(config, eval, 1000);

//		SwipeFunction func = (double swipe_val) -> new ExampleConfig((int)Math.round(swipe_val),
//						((int)Math.round(swipe_val)) / 10,
//						2);
//		SwipeFunction func = (double swipe_val) -> (new ExampleConfig(100,
//				10,
//				2, swipe_val, "swipe_" + swipe_val));
//		swipeExperiment(eval, func, 0.1, 0.1, 10, false);
	}

	private static double executeExperiment(Configuration config, ContestEvaluation eval){
		return executeExperiment(config, eval, 1);
	}

	private static double executeExperiment(Configuration config, ContestEvaluation eval, long seed){
		TheOptimizers a = new TheOptimizers();
		a.setSeed(seed);
		a.setEvaluation(eval);
		if(config != null)
			a.setConfig(config);
		a.run();
		return a.getBestScore();
	}

	private static void swipeSeeds(Configuration config, ContestEvaluation eval, int number_of_runs){
	    double best_score = 0.0;
	    double mean_score = 0.0;
	    double worst_score = Double.MAX_VALUE;
	    double loc_score;
	    long startTime = System.currentTimeMillis();
	    long lastPrintTime = -1;
	    long currentTime;
	    String summary = config.toString() + "\n\n";
	    for(int i=0;i<number_of_runs;i++){
	        loc_score = executeExperiment(config, eval, i);
	        if(loc_score > best_score)
	            best_score = loc_score;
	        if(loc_score < worst_score)
	            worst_score = loc_score;
	        mean_score += loc_score;
	        currentTime = System.currentTimeMillis();
	        if(i == 0 || i == number_of_runs - 1 || (currentTime - lastPrintTime) > 5 * 60000){
	            System.out.println("==================================");
	            System.out.println("Finished " + (i + 1) + " runs");
	            System.out.println("Best score: " + best_score);
                System.out.println("Worst score: " + worst_score);
	            System.out.println("Mean score: " + (mean_score / (i + 1)));
	            double expected_runtime = (currentTime - startTime) / (i + 1.0) * (number_of_runs - i - 1) / 1000.0;
	            System.out.print("Expected runtime remaining: ");
	            int minutes = 0;
	            while(expected_runtime > 60){
	                minutes++;
	                expected_runtime-=60;
                }
                System.out.println(minutes + "min " + (int)Math.round(expected_runtime) + "s");
	            lastPrintTime = currentTime;
            }
            summary += "Seed " + i + ": " + loc_score + "\n";
        }
        mean_score /= number_of_runs;
	    summary += "==============================\nBest score: " + best_score + "\nWorst score: " + worst_score + "\nMean score: " + mean_score;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        new File("logs/").mkdirs();
        String filename = "logs/swipe_" + (config.getName().length() == 0 ? "" : config.getName() + "_") + dateFormat.format(date) + ".txt";
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(summary);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
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
