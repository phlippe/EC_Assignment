package selection;

import individuals.Individual;
import individuals.Population;

/**
 * Created by phlippe on 12.09.18.
 */
public class ParentSigmaScalingSelection extends ParentSelection
{

	private double c;
	private double mean_fitness;
	private double standard_deviation;


	public ParentSigmaScalingSelection(ParentSelectionStochastic stochastic, double c)
	{
		super(stochastic);
		this.c = c;
	}

	@Override
	void prepareSelection(Population population)
	{
		mean_fitness = 0.0;
		for(int i=0;i<population.size();i++){
			mean_fitness += population.get(i).getFitness();
		}
		mean_fitness /= population.size();

		standard_deviation = 0.0;
		for(int i=0;i<population.size();i++){
			standard_deviation += Math.pow(population.get(i).getFitness() - mean_fitness, 2);
		}
		standard_deviation /= population.size();
		standard_deviation = Math.sqrt(standard_deviation);
	}

	@Override
	double getSelectionProbability(Individual individual)
	{
		return Math.max(individual.getFitness() - (mean_fitness - c * standard_deviation), 0);
	}

	@Override
	public String getProbabilityDescription()
	{
		return " Sigma scaling with c = " + c;
	}
}
