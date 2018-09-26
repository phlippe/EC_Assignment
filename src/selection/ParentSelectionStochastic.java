package selection;

/**
 * Created by phlippe on 12.09.18.
 */
public abstract class ParentSelectionStochastic
{

	public abstract void randomlySelectElements(int[][] parent_indices, double[] ranges, double sum);

	protected static int searchIndexOfRange(double[] vals, double rand_val){
		return ParentSelectionStochastic.searchIndexOfRange(vals, rand_val, 0, vals.length - 1);
	}

	protected static int searchIndexOfRange(double[] vals, double rand_val, int lower_bound, int upper_bound){
		if(upper_bound == lower_bound)
			return upper_bound;

		int index = (lower_bound + upper_bound) / 2;
		if(rand_val > vals[index])
			return ParentSelectionStochastic.searchIndexOfRange(vals, rand_val, index + 1, upper_bound);
		else
			return ParentSelectionStochastic.searchIndexOfRange(vals, rand_val, lower_bound, index);

	}

	public String getStochasticDescription(){
		String s = "";
		s += "\t Stochastic class: " + this.getClass().getName() + "\n";
		s += "\t Stochastic method: " + getStochasticMethodDescription() + "\n";
		return s;
	}

	public abstract String getStochasticMethodDescription();

}
