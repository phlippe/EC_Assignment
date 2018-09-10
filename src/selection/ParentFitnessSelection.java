package selection;

import individuals.Individual;
import individuals.Population;

/**
 * Created by phlippe on 08.09.18.
 */
public class ParentFitnessSelection extends ParentSelection
{

	@Override
	void prepareSelection(Population population)
	{
		// Nothing to do here. Might be necessary for other variations
	}

	@Override
	double getSelectionProbability(Individual individual)
	{
		return individual.getFitness();
	}

	public String getProbabilityDescription(){
		return "probability proportional to the fitness of an individual";
	}

}
