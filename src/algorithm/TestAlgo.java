package algorithm;

import configuration.ConfigParams;
import configuration.StandardConfig;
import individuals.NichingTechnique;
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

	enum ReportVariants{
		EXPLICIT_DIVERSITY_CONTROL,
		FITNESS_SHARING,
		FITNESS_SHARING_SIGMA,
		FITNESS_SHARING_BETA,
		STANDARD
	}

	public static void main(String args[]){

		// Define algorithm you want to run. Based on this decision, the parameters will be selected
		ReportVariants algoVariant = ReportVariants.STANDARD;
		// Define contest function on which the algorithm should be tested
		EvalType evalType = EvalType.KATSUURA;
		ContestEvaluation eval = createEval(evalType);

		// Create default parameter setting that is shared among all variants
		ConfigParams configParams = new ConfigParams(400, 80, 2);
		configParams.setParentTournamentSize(2);
		configParams.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
		configParams.setSurvivorTournamentSize(2);
		configParams.setMutationMultiSigmaInit(0.01);
		configParams.setMutationMultiSigmaFactor(0.8);

		// Create algorithm specific parameters
		if(algoVariant == ReportVariants.STANDARD){
			// Standard algorithm does not apply any fitness sharing or other niching technique
			configParams.setUseNichingTechnique(false);
		}
		else{
			// For all other algorithms than standard, use niching technique
			createNichingSpecificParameters(configParams, evalType, algoVariant);
		}

		// Create EA instance
		StandardConfig config = new StandardConfig(configParams);
        EvolutionaryCycle eval_ea = new EvolutionaryCycle(config);
		// Tracer creates log files with information about the population, max fitness, ... (optional by the parameter "active")
		Tracer tracer = new Tracer(false, "fitness_sharing");
        eval_ea.addTracer(tracer);

        // Execute "number_of_runs" tests with seeds from "start_seed" to "start_seed" + "number_of_runs" and print mean score
        swipeSeeds(eval_ea, eval, 1000, 0, true);
	}

	private static void createNichingSpecificParameters(ConfigParams configParams, EvalType evalType, ReportVariants algoVariant){
		configParams.setUseNichingTechnique(true);
		configParams.setFitnessSharingSigma(0.03); // Define sigma share
		configParams.setFitnessSharingBeta(1);	// Define initial beta
		configParams.setFitnessSharingAlpha(1);	// Define alpha

		// Determine how many generations will be created before running out of evaluation cycles
		double remaining_iterations = getEvalLimit(evalType) / (configParams.getNumberRecombinations() * configParams.getParentArity());
		switch(algoVariant){
			case EXPLICIT_DIVERSITY_CONTROL:
				configParams.setNichingTechnique(NichingTechnique.EXPLICIT_DIVERSITY_CONTROL);
				configParams.setPushToLineEndCycle(3000);
				configParams.setPushToLinePower(1);
				configParams.setPushToLineStartVal(4);
				configParams.setPushToLineGradientFactor(1);
				break;
			case FITNESS_SHARING:
				configParams.setNichingTechnique(NichingTechnique.FITNESS_SHARING);
				configParams.setFitnessSharingOffsetSteps(remaining_iterations);
				configParams.setFitnessSharingMaxSteps(remaining_iterations);
				configParams.setFitnessSharingAdaptiveStepSize(0.0);
				configParams.setFitnessSharingStepsExponential(false);
				break;
			case FITNESS_SHARING_SIGMA:
				configParams.setFitnessSharingAdaptSigma(true);
				configParams.setNichingTechnique(NichingTechnique.FITNESS_SHARING);
				// Define curve for changing sigma
				configParams.setFitnessSharingOffsetSteps(0.333 * remaining_iterations);
				configParams.setFitnessSharingMaxSteps((1 - 0.334) * remaining_iterations);
				configParams.setFitnessSharingAdaptiveStepSize(-0.03 / (0.333 * remaining_iterations));
				configParams.setFitnessSharingStepsExponential(false);
				break;
			case FITNESS_SHARING_BETA:
				configParams.setFitnessSharingAdaptSigma(false); // False means we adapt beta instead
				configParams.setNichingTechnique(NichingTechnique.FITNESS_SHARING);
				// Define curve for changing beta
				configParams.setFitnessSharingOffsetSteps(0.333 * remaining_iterations);
				configParams.setFitnessSharingMaxSteps((1 - 0.334) * remaining_iterations);
				configParams.setFitnessSharingAdaptiveStepSize(9.0 / (0.333 * remaining_iterations));
				configParams.setFitnessSharingStepsExponential(false);
				break;
			case STANDARD:
				break;
		}
	}

	private static double executeExperiment(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval){
		return executeExperiment(eval_ea, eval, 1);
	}

	// Run a single experiment based on this EA, evaluation function and seed
	private static double executeExperiment(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval, long seed){
		TheOptimizers a = new TheOptimizers();
		a.setSeed(seed);
		a.setEvaluation(eval);
		if(eval_ea != null)
			a.setEvolutionaryAlgorithm(eval_ea);
		a.run();
		return a.getBestScore();
	}

	// Run multiple experiments with different seeds
	public static double swipeSeeds(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval, int number_of_runs){
		return swipeSeeds(eval_ea, eval, number_of_runs, 0, true);
	}

	public static double swipeSeeds(EvolutionaryAlgorithm eval_ea, ContestEvaluation eval, int number_of_runs, int start_seed, boolean writeLog){
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
        if(writeLog) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            Date date = new Date();
            new File("logs/").mkdirs();
            String filename = "logs/swipe_" + (eval_ea.getName().length() == 0 ? "" : eval_ea.getName() + "_") + dateFormat.format(date) + ".txt";
            try (PrintWriter out = new PrintWriter(filename)) {
                out.println(summary);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return mean_score;
    }

	public static ContestEvaluation createEval(EvalType eval_func){
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

	private static double getEvalLimit(EvalType evalType){
		switch(evalType){
			case SPHERE: return SphereEvaluation.EVALS_LIMIT_;
			case KATSUURA: return KatsuuraEvaluation.EVALS_LIMIT_;
			case SCHAFFERS: return SchaffersEvaluation.EVALS_LIMIT_;
			case BENT_CIGAR: return BentCigarFunction.EVALS_LIMIT_;
		}
		return -1;
	}

}
