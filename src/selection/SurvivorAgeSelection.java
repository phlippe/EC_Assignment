package selection;

import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;

/**
 * Created by phlippe on 10.09.18.
 */
public class SurvivorAgeSelection
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
		return -individual.getAge();
	}

	@Override
	public String getCriteriaDescription()
	{
		return "age";
	}

}
