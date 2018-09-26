package selection;

import configuration.ConfigurableObject;
import individuals.Individual;
import individuals.Population;

/**
 * Created by phlippe on 06.09.18.
 */
public abstract class ParentSelection implements ConfigurableObject
{

	private ParentSelectionStochastic stochastic;

	public ParentSelection(ParentSelectionStochastic stochastic){
		this.stochastic = stochastic;
	}

	public int[][] selectParent(Population population, int number_parent_pairs, int number_parents){
		double[] prob = new double[population.size()];
		int[][] parent_indices = new int[number_parent_pairs][number_parents];
		double sum = 0.0;
		int i;

		prepareSelection(population);

		for(i=0;i<population.size();i++){
			double selProb = getSelectionProbability(population.get(i));
			prob[i] = sum + selProb;
			sum += selProb;
		}

		stochastic.randomlySelectElements(parent_indices, prob, sum);

		return parent_indices;
	}

	abstract void prepareSelection(Population population);

	abstract double getSelectionProbability(Individual individual);

	public String getDescription(){
		String s = "";
		s += "Parent selection class: " + this.getClass().getName() + "\n";
		s += "Selection process: each individual has an assigned probability to be selected as a parent\n";
		s += "Selection criteria: " + getProbabilityDescription() + "\n";
		s += "Stochastic: \n" + stochastic.getStochasticDescription() + "\n";
		return s;
	}

	public abstract String getProbabilityDescription();

}
