package algorithm;

import configuration.ConfigParams;
import configuration.Configuration;
import configuration.ExampleConfig;
import configuration.StandardConfig;
import island.DistributedEvolutionaryCycle;
import island.IslandParams;
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

//		ConfigParams configParams = getBestKatsuuraConfig();
//		StandardConfig config = new StandardConfig(configParams);
//		EvolutionaryCycle eval_ea = new EvolutionaryCycle(config);
//		ContestEvaluation eval = createEval(EvalType.KATSUURA);
//      swipeSeeds(eval_ea, eval, 1000);

        ConfigParams configParams = new ConfigParams(100, 10, 2);
        configParams.setParentTournamentSize(4);
        configParams.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
        configParams.setSurvivorTournamentSize(6);
        configParams.setMutationMultiSigmaInit(0.01);
        configParams.setMutationMultiSigmaFactor(0.8);
        StandardConfig config = new StandardConfig(configParams);
        IslandParams islandParams = new IslandParams(500, 2);
        islandParams.setTopologyType(IslandParams.TopologyType.COMPLETE);
        DistributedEvolutionaryCycle eval_ea = new DistributedEvolutionaryCycle(config, 5, islandParams);
        ContestEvaluation eval = createEval(EvalType.KATSUURA);
        swipeSeeds(eval_ea, eval, 1);
	}

	private static ConfigParams getBestKatsuuraConfig(){
        ConfigParams configParams = new ConfigParams(400, 40, 2);
        configParams.setParentTournamentSize(2);
        configParams.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
        configParams.setSurvivorTournamentSize(6);
        configParams.setMutationMultiSigmaInit(0.01);
        configParams.setMutationMultiSigmaFactor(0.8);
        return configParams;
    }

	private static double executeExperiment(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval){
		return executeExperiment(eval_ea, eval, 1);
	}

	private static double executeExperiment(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval, long seed){
		TheOptimizers a = new TheOptimizers();
		a.setSeed(seed);
		a.setEvaluation(eval);
		if(eval_ea != null)
			a.setEvolutionaryAlgorithm(eval_ea);
		a.run();
		return a.getBestScore();
	}

	private static void swipeSeeds(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval, int number_of_runs){
	    double best_score = 0.0;
	    double mean_score = 0.0;
	    double worst_score = Double.MAX_VALUE;
	    double loc_score;
	    long startTime = System.currentTimeMillis();
	    long lastPrintTime = -1;
	    long currentTime;

        String summary = "";
	    for(int i=0;i<number_of_runs;i++){
	        loc_score = executeExperiment(eval_ea, eval, i);
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
        summary += "\n\n" + eval_ea.getLogString() + "\n\n";

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        new File("logs/").mkdirs();
        String filename = "logs/swipe_" + (eval_ea.getName().length() == 0 ? "" : eval_ea.getName() + "_") + dateFormat.format(date) + ".txt";
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(summary);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
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
