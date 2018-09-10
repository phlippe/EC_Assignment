package selection;

import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;

/**
 * Created by phlippe on 08.09.18.
 */
public class SurvivorFitnessSelection
		extends SurvivorSelection
{

	@Override
	void prepareSelection(Population population, ArrayList<Individual> children)
	{
		// Nothing to do here. Might be necessary for other variations
	}

	@Override
	double rateIndividual(Individual individual, boolean isNewChild)
	{
		return individual.getFitness();
	}

	@Override
	public String getCriteriaDescription()
	{
		return "fitness";
	}

}
