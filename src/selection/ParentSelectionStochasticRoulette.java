package selection;

import algorithm.player59;

/**
 * Created by phlippe on 12.09.18.
 */
public class ParentSelectionStochasticRoulette extends ParentSelectionStochastic
{

	private boolean allowParentTwice;

	public ParentSelectionStochasticRoulette(){
		allowParentTwice = false;
	}

	public ParentSelectionStochasticRoulette(boolean allowParentTwice){
		this.allowParentTwice = allowParentTwice;
	}

	public void randomlySelectElements(int[][] parent_indices, double[] ranges, double sum){
		double random_val;
		boolean parent_twice;
		int i, j, k;
		for(i=0;i<parent_indices.length;i++){
			random_val = player59.rnd_.nextDouble() * sum;
			// System.out.println("Sum: "+sum+", Random val: "+random_val);
			parent_indices[i][0] = ParentSelectionStochastic.searchIndexOfRange(ranges, random_val);
			for(j=1;j<parent_indices[i].length;j++)
			{
				do
				{
					random_val = player59.rnd_.nextDouble() * sum;
					parent_indices[i][j] = ParentSelectionStochastic.searchIndexOfRange(ranges, random_val);
					parent_twice = false;
					for(k=0;k<j;k++){
						parent_twice = parent_twice || (parent_indices[i][k] == parent_indices[i][j]);
					}
				} while (parent_twice && !allowParentTwice);
			}
		}
	}

	@Override
	public String getStochasticMethodDescription()
	{
		return " roulette wheel with no parent twice for a selection";
	}

}
