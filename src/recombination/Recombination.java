package recombination;

import configuration.ConfigurableObject;
import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public abstract class Recombination implements ConfigurableObject
{

	public abstract ArrayList<Individual> recombine(ArrayList<Individual> parents);

	public abstract ArrayList<Individual> recombineAll(Population population, int[][] parent_indices);

	public String getDescription(){
		String s = "";
		s += "Recombination class: " + this.getClass().getName() + "\n";
		s += getRecombinationDescription() + "\n";
		return s;
	}

	protected abstract String getRecombinationDescription();

}
