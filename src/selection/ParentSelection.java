package selection;

import com.sun.org.apache.bcel.internal.generic.POP;
import configuration.ConfigurableObject;
import individuals.Individual;
import individuals.Population;
import algorithm.TheOptimizers;

/**
 * Created by phlippe on 06.09.18.
 */
public abstract class ParentSelection implements ConfigurableObject
{

	public ParentSelection(){

	}

	public int[][] selectParent(Population population, int number_parent_pairs, int number_parents){
		double[] prob = new double[population.size()];
		int[][] parent_indices = new int[number_parent_pairs][number_parents];
		double sum = 0.0;
		int i, j, k;
		boolean parent_twice;

		prepareSelection(population);

		for(i=0;i<population.size();i++){
			double selProb = getSelectionProbability(population.get(i));
			prob[i] = sum + selProb;
			sum += selProb;
		}
		double random_val;
		for(i=0;i<number_parent_pairs;i++){
			random_val = TheOptimizers.rnd_.nextDouble() * sum;
			// System.out.println("Sum: "+sum+", Random val: "+random_val);
			parent_indices[i][0] = ParentSelection.searchIndexOfRange(prob, random_val);
			for(j=1;j<number_parents;j++)
			{
				do
				{
					random_val = TheOptimizers.rnd_.nextDouble() * sum;
					parent_indices[i][j] = ParentSelection.searchIndexOfRange(prob, random_val);
					parent_twice = false;
					for(k=0;k<j;k++){
						parent_twice = parent_twice || (parent_indices[i][k] == parent_indices[i][j]);
					}
				} while (parent_twice);
			}
		}
		return parent_indices;
	}

	abstract void prepareSelection(Population population);

	abstract double getSelectionProbability(Individual individual);

	public String getDescription(){
		String s = "";
		s += "Parent selection class: " + this.getClass().getName() + "\n";
		s += "Selection process: each individual has an assigned probability to be selected as a parent\n";
		s += "Selection criteria: " + getProbabilityDescription() + "\n";
		return s;
	}

	public abstract String getProbabilityDescription();

	public static int searchIndexOfRange(double[] vals, double rand_val){
		return ParentSelection.searchIndexOfRange(vals, rand_val, 0, vals.length - 1);
	}

	public static int searchIndexOfRange(double[] vals, double rand_val, int lower_bound, int upper_bound){
		if(upper_bound == lower_bound)
			return upper_bound;

		int index = (lower_bound + upper_bound) / 2;
		if(rand_val > vals[index])
			return ParentSelection.searchIndexOfRange(vals, rand_val, index + 1, upper_bound);
		else
			return ParentSelection.searchIndexOfRange(vals, rand_val, lower_bound, index);

	}
}
