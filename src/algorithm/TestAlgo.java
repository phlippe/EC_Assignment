package algorithm;

import configuration.ConfigParams;
import configuration.Configuration;
import configuration.ExampleConfig;
import configuration.StandardConfig;
import individuals.FitnessSharingType;
import island.DistributedEvolutionaryCycle;
import island.IslandParams;
import org.vu.contest.ContestEvaluation;

import evaluation.*;
import recombination.RecombinationType;
import selection.SurvivorSelectionType;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        configParams.setParentTournamentSize(2);
        configParams.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
        configParams.setSurvivorTournamentSize(2);
        configParams.setMutationMultiSigmaInit(0.01);
        configParams.setMutationMultiSigmaFactor(0.8);
        configParams.setUseFitnessSharing(false);
        configParams.setUseFitnessSharingMultiSigma(false);
        StandardConfig config = new StandardConfig(configParams);

		ConfigParams configParams2 = new ConfigParams(400, 80, 2);
		configParams2.setParentTournamentSize(2);
		configParams2.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
		configParams2.setSurvivorTournamentSize(2);
		configParams2.setMutationMultiSigmaInit(0.01);
		configParams2.setMutationMultiSigmaFactor(0.8);
		configParams2.setUseFitnessSharing(true);
		configParams2.setUseFitnessSharingMultiSigma(false);
		configParams2.setFitnessSharingSigma(0.03);
		configParams2.setFitnessSharingBeta(1);
		configParams2.setFitnessSharingAlpha(1);
		double remaining_iterations = 12500;
		// configParams2.setFitnessSharingBetaOffsetSteps(0.2 * remaining_iterations);
		// configParams2.setFitnessSharingBetaStep(Math.exp(Math.log(10) / (0.8 * remaining_iterations)));
		// configParams2.setFitnessSharingBetaExponential(true);
		double offstep_prop = 0.4;
		configParams2.setFitnessSharingBetaOffsetSteps(offstep_prop * remaining_iterations);
		configParams2.setFitnessSharingBetaMaxSteps((1 - 0.5 * offstep_prop) * remaining_iterations);
		configParams2.setFitnessSharingBetaStep(8.0 / ((1 - 2 * offstep_prop) * remaining_iterations));
//		double offstep_prop = 0.2;
//		configParams2.setFitnessSharingBetaOffsetSteps(offstep_prop * remaining_iterations);
//		configParams2.setFitnessSharingBetaMaxSteps((1 - 0.2 - offstep_prop) * remaining_iterations);
//		configParams2.setFitnessSharingBetaStep(4.0 / ((0.2) * remaining_iterations));
		configParams2.setFitnessSharingBetaExponential(false);
		configParams2.setFitnessSharingType(FitnessSharingType.PUSH_TO_LINE_SYMMETRIC);
		configParams2.setPushToLineEndCycle(3000);
		configParams2.setPushToLinePower(6);
		configParams2.setPushToLineStartVal(4);
		configParams2.setPushToLineFitnessSharing(false);
		//configParams2.setRecombinationType(RecombinationType.BLEND_RANDOM_CROSSOVER);
		//configParams2.setRecombinationBlendRandomSigma(0.1);
		StandardConfig config2 = new StandardConfig(configParams2);

		ConfigParams configParams3 = new ConfigParams(100, 10, 2);
		configParams3.setParentTournamentSize(2);
		configParams3.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
		configParams3.setSurvivorTournamentSize(2);
		configParams3.setMutationMultiSigmaInit(0.01);
		configParams3.setMutationMultiSigmaFactor(0.8);
		configParams3.setUseFitnessSharing(true);
		configParams3.setUseFitnessSharingMultiSigma(false);
		configParams3.setFitnessSharingSigma(0.0001);
		StandardConfig config3 = new StandardConfig(configParams3);

		StandardConfig[] allConfigs = {config, config2, config3, config, config2, config3};

        IslandParams islandParams = new IslandParams(1000, 5);
        islandParams.setTopologyType(IslandParams.TopologyType.COMPLETE);
        islandParams.setExchangeType(IslandParams.ExchangeType.MULTI_CULTI);
        // DistributedEvolutionaryCycle eval_ea = new DistributedEvolutionaryCycle(allConfigs, islandParams);
        EvolutionaryCycle eval_ea = new EvolutionaryCycle(config2);
		Tracer tracer = new Tracer(false, "fitness_sharing_push_to_line_symmetric_best");
        eval_ea.addTracer(tracer);
        ContestEvaluation eval = createEval(EvalType.KATSUURA);
        swipeSeeds(eval_ea, eval, 1000, 0);
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
		swipeSeeds(eval_ea, eval, number_of_runs, 0);
	}

	private static void swipeSeeds(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval, int number_of_runs, int start_seed){
	    double best_score = 0.0;
	    double mean_score = 0.0;
	    double worst_score = Double.MAX_VALUE;
	    double loc_score;
	    long startTime = System.currentTimeMillis();
	    long lastPrintTime = -1;
	    long currentTime;

        String summary = "";
	    for(int i=0;i<number_of_runs;i++){
	        loc_score = executeExperiment(eval_ea, eval, i + start_seed);
	        if(i == 0)
				System.out.println(eval_ea.getLogString());
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
