package selection;

import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;

/**
 * Created by phlippe on 12.09.18.
 */
public class SurvivorElitismSelection extends SurvivorSelection
{
	@Override
	void prepareSelection(Population population, ArrayList<Individual> children)
	{

	}

	@Override
	double rateIndividual(Individual individual, boolean isNewChild)
	{
		return 0;
	}

	@Override
	public String getCriteriaDescription()
	{
		return " elitism - if no child has better fitness than an individual is selected to die, it is kept alive and a child is discarded\n combined with stochastic fitness selection";
	}
}
