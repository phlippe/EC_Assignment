package ea_on_ea;

import algorithm.*;
import configuration.ConfigParams;
import configuration.Configuration;
import configuration.StandardConfig;
import evaluation.EvalType;
import individuals.GeneTypes;
import individuals.GenoRepresentation;
import individuals.Individual;
import individuals.Population;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;
import mutation.Mutation;
import org.vu.contest.ContestEvaluation;
import recombination.Recombination;
import selection.ParentSelection;
import selection.SurvivorSelection;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EAOnEA implements EvolutionaryAlgorithm {



    private Population population;
    private GenoRepresentation genoRepresentation;
    private SurvivorSelection survivorSelection;
    private ParentSelection parentSelection;
    private Recombination recombination;
    private ArrayList<Mutation> mutations;
    private Configuration myConfig;
    private int number_cycle;

    public EAOnEA(){
        this.myConfig = new EAOnEAConfig();
        population = new Population(myConfig.getPopulationSize());
        genoRepresentation = myConfig.getRepresentation();
        survivorSelection = myConfig.getSurvivorSelection();
        parentSelection = myConfig.getParentSelection();
        recombination = myConfig.getRecombination();
        mutations = myConfig.getMutationOperators();
    }

    @Override
    public void initialize(){
        number_cycle = 1;
        population.initialize(genoRepresentation, myConfig.getGenoInitializer(), myConfig.getAddParamsInitializer());
        ArrayList<Individual> individuals = new ArrayList<>();
        for(int i=0;i<population.size();i++)
            individuals.add(population.get(i));
        evaluate(individuals);
        population.reevaluateMaxFitness();
        population.increaseAge();
        population.setConfigParams(myConfig.getParameters());
    }

    @Override
    public void addTracer(Tracer tracer) {

    }

    @Override
    public void writeTraceFiles() {

    }

    @Override
    public String getExtraDescription() {
        return null;
    }

    @Override
    public void run_single_cycle(){
        population.prepareCycle();
        ArrayList<Individual> reeval_ind = new ArrayList<>();
        for(int i=0;i<population.size();i++){
            if(number_cycle > 4 && population.get(i).getAge() > number_cycle * 3 / 4){
                reeval_ind.add(population.get(i));
            }
        }
        evaluate(reeval_ind);
        // 1. Parent selection
        int[][] selected_parents = parentSelection.selectParent(population, myConfig.getNumberOfRecombinations(), myConfig.getParentArity());
        // 2. recombination.Recombination / crossover
        ArrayList<Individual> children = recombination.recombineAll(population, selected_parents);
        // 3. mutation.Mutation
        for(Mutation mut: mutations){
            for(Individual new_ind: children){
                mut.mutate(new_ind);
            }
        }
        // 4. Fitness evaluation
        evaluate(children);
        population.interactWithNewChildren(children);
        // 5. Survivor selection
        ArrayList<Individual> removed_individuals = survivorSelection.selectSurvivors(population, children);
        population.increaseAge();
        population.endCycle();
        number_cycle++;
    }

    private void evaluate(ArrayList<Individual> children){
//        ArrayList<Thread> threads = new ArrayList<>();
        for(Individual i: children){
            RunExperiment runExperiment = new RunExperiment(i, number_cycle + 1);
            runExperiment.run();
//            Thread myThread = new Thread(runExperiment);
//            myThread.start();
//            threads.add(myThread);
        }
//        for(Thread t: threads){
//            try{
//                t.join();
//            }
//            catch(InterruptedException e){
//                System.out.println("Could not join: "+e.getMessage());
//            }
//        }
    }

    @Override
    public int getEvalsPerCycle() {
        return 0;
    }

    @Override
    public double[] getBestSolution() {
        return new double[0];
    }

    @Override
    public double getBestFitness() {
        return 0;
    }

    @Override
    public Population getPopulation() {
        return population;
    }

    @Override
    public void logResults() {

    }

    @Override
    public String getLogString() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    public static void main(String args[]){
        TheOptimizers opt = new TheOptimizers();
        opt.setSeed(0);
        EAOnEA myEA = new EAOnEA();
        myEA.initialize();
        for(int i=0;i<1000;i++) {
            Individual best_Individual = myEA.getPopulation().getMaxIndividual();
            System.out.println("("+i+") Best fitness: " + best_Individual.getFitness() +
                    " (alpha = "+best_Individual.getAdditionalParams(GeneTypes.ALPHA)[0] +
                    ", beta = " + best_Individual.getAdditionalParams(GeneTypes.BETA)[0] +
                    ", gamma = " + best_Individual.getAdditionalParams(GeneTypes.GAMMA)[0] + ")");
//            for(int j=0;j<myEA.getPopulation().size();j++){
//                System.out.println("Individiual " + j + ": " + myEA.getPopulation().get(j).getFitness() + "/" + myEA.getPopulation().get(j).getPureFitness() +
//                        " (alpha = "+myEA.getPopulation().get(j).getAdditionalParams(GeneTypes.ALPHA)[0] +
//                        ", beta = " + myEA.getPopulation().get(j).getAdditionalParams(GeneTypes.BETA)[0] +
//                        ", gamma = " + myEA.getPopulation().get(j).getAdditionalParams(GeneTypes.GAMMA)[0] + ")");
//            }
            myEA.run_single_cycle();
        }
    }


    static ContestEvaluation eval = TestAlgo.createEval(EvalType.KATSUURA);

    public class RunExperiment implements Runnable{


        private Individual individual;
        private int number_runs;

        public RunExperiment(Individual i, int number_runs){
            individual = i;
            this.number_runs = number_runs;
        }

        @Override
        public void run() {
            ConfigParams configParams = TestAlgo.getDefaultConfig();
            configParams.setPushToLineStartVal(individual.getAdditionalParams(GeneTypes.BETA)[0]);
            configParams.setPushToLineEndCycle(individual.getAdditionalParams(GeneTypes.ALPHA)[0]);
            configParams.setPushToLineGradientFactor(Math.pow(10, individual.getAdditionalParams(GeneTypes.GAMMA)[0]));

            StandardConfig config = new StandardConfig(configParams);
            EvolutionaryCycle eval_ea = new EvolutionaryCycle(config);
            double mean_score = TestAlgo.swipeSeeds(eval_ea, eval, this.number_runs,1, false);
            individual.setFitness(mean_score);
        }
    }
}
