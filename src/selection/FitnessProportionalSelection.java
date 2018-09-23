package selection;

import algorithm.TheOptimizers;
import evaluation.BentCigarFunction;
import individuals.BoundRepresentation;
import individuals.GeneTypes;
import individuals.Individual;
import individuals.Population;
import initialization.RandomGenoInitializer;

public class FitnessProportionalSelection extends ParentSelection{

    private Double c;
    private Double FitnessMean;
    private Double FitnessSD;
    private Double[] ProportionateFitness;
    private Long[] SavingIDsForProportionateFitness;
    private Double ProportionateFitnessSum;
    private Population a;

    public FitnessProportionalSelection(ParentSelectionStochastic stochastic,Double c) {
        super(stochastic);
        this.c=c;
    }

    @Override
    void prepareSelection(Population population) {
        this.a=population;
        /*calculate mean*/
        Double sum=0.0;
        for (int i=0;i<population.size();i++){
            sum=sum+population.get(i).getFitness();
        }
        FitnessMean=sum/population.size();
        /*calculating standard deviation*/
        sum=0.0;
        for (int i=0;i<population.size();i++){
            sum=sum+Math.pow((population.get(i).getFitness()-FitnessMean),2);
        }
        FitnessSD=Math.pow((sum/population.size()),0.5);
        /*calculating proportionateFitnesses*/
        ProportionateFitness=new Double[population.size()];
        SavingIDsForProportionateFitness=new Long[population.size()];
        for (int i=0;i<population.size();i++){
            Double value=population.get(i).getFitness()-(FitnessMean-c*FitnessSD);
            ProportionateFitness [i]= Math.max(value, 0.0);
            SavingIDsForProportionateFitness[i]=population.get(i).getID();
        }
        /*calculating the sum of proportionate fitnesses*/
        ProportionateFitnessSum=0.0;
        for (int i=0;i<population.size();i++){
            ProportionateFitnessSum=ProportionateFitnessSum+ProportionateFitness [i];
        }
        /*assigning the fitnesses*/
        for (int i=0;i<population.size();i++){
            ProportionateFitness[i]=ProportionateFitness[i]/ProportionateFitnessSum;
        }
    }

    @Override
    double getSelectionProbability(Individual individual) {
        Double Pi=-1.0;
        /*finding the individual's place in the population*/
        for (int i=0;i<a.size();i++){
            if (individual.getID()==SavingIDsForProportionateFitness[i]){
                Pi=ProportionateFitness[i];
            }
        }
        return Pi;
    }

    @Override
    public String getProbabilityDescription() {
        return "Fitness Proportional Selection";
    }

    public static void main(String[] test){
        TheOptimizers opt = new TheOptimizers();
        opt.setEvaluation(new BentCigarFunction());
        opt.setSeed(1);
        ParentSelectionStochastic b = new ParentSelectionStochasticRoulette();
        FitnessProportionalSelection a=new FitnessProportionalSelection(b,2.0);
        Population p = new Population(4);
        p.initialize(new BoundRepresentation(10, new int[0], new GeneTypes[0], -5,5), new RandomGenoInitializer());
        for(int i=0;i<4;i++){
            p.get(i).setFitness(i);
            System.out.println(p.get(i).getFitness());
            System.out.println(p.get(i).getID());
        }
        a.prepareSelection(p);
        for(int j=0; j<p.size();j++){
            System.out.println(a.getSelectionProbability(p.get(j)));
        }
    }
}
