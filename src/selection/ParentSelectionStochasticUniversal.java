package selection;

import algorithm.TheOptimizers;

/**
 * Created by phlippe on 12.09.18.
 */
public class ParentSelectionStochasticUniversal
		extends ParentSelectionStochastic
{

	public void randomlySelectElements(int[][] parent_indices, double[] ranges, double sum){
		int[] shuffled_indices = randomlyShuffleIndices(ranges.length);
		double[] shuffled_ranges = shuffleRanges(ranges, shuffled_indices);
		int size_mating_pool = parent_indices.length * parent_indices[0].length;
		double rand_start_number = TheOptimizers.rnd_.nextDouble() * sum / size_mating_pool;
		int[] flat_parent_indices = new int[size_mating_pool];
		for(int i=0;i<size_mating_pool;i++){
			double rand = rand_start_number + i * 1.0 / size_mating_pool;
			int range_index = ParentSelectionStochastic.searchIndexOfRange(shuffled_ranges, rand);
			flat_parent_indices[i] = shuffled_indices[range_index];
		}
		flat_parent_indices = shuffleArray(flat_parent_indices);
		int counter = 0;
		for(int i=0;i<parent_indices.length;i++){
			for(int j=0;j<parent_indices[i].length;j++){
				parent_indices[i][j] = flat_parent_indices[counter];
				counter++;
			}
		}

	}

	// Testing shuffle methods
//	public static void main(String args[]){
//		new TheOptimizers().setSeed(1);
//		ParentSelectionStochasticUniversal a = new ParentSelectionStochasticUniversal();
//		double[] ranges = {0.1, 0.3, 0.31, 0.5, 1.0}; // 0.2, 0.3, 0.1, 0.4
//		int[][] parent_indices = new int[5][2];
//		a.randomlySelectElements(parent_indices, ranges, 1.0);
//		for(int i=0;i<parent_indices.length;i++){
//			for(int j=0;j<parent_indices[i].length;j++){
//				System.out.print(parent_indices[i][j] + ", ");
//			}
//			System.out.println();
//		}
////		int[] shuffled_indices = a.randomlyShuffleIndices(ranges.length);
////		double[] shuffled_ranges = a.shuffleRanges(ranges, shuffled_indices);
////		for(int i=0;i<shuffled_ranges.length;i++){
////			System.out.print(shuffled_ranges[i] + " (" + shuffled_indices[i] + ")" + ", ");
////		}
////		System.out.println();
//	}

	private int[] randomlyShuffleIndices(int number_indices){
		int[] indices = new int[number_indices];
		for(int i=0;i<number_indices;i++){
			indices[i] = i;
		}
		int[] shuffled_indices = shuffleArray(indices);
		return shuffled_indices;
	}

	private int[] shuffleArray(int[] array_to_shuffle){
		int[] shuffled_array = new int[array_to_shuffle.length];
		for(int i=0;i<shuffled_array.length;i++){
			shuffled_array[i] = Integer.MAX_VALUE;
		}
		int rand_pos;
		int i, j;
		for(i=array_to_shuffle.length-1;i>=0;i--){
			rand_pos = TheOptimizers.rnd_.nextInt(i+1);
			for(j=0;j<shuffled_array.length;j++){
				if(shuffled_array[j] == Integer.MAX_VALUE)
					rand_pos--;
				if(rand_pos < 0)
					break;
			}
			if(rand_pos >= 0){
				TheOptimizers.println("ERROR (ParentSelectionStochasticUniversal): randomly shuffled indices could not find enough free space...");
			}
			shuffled_array[j] = array_to_shuffle[i];
		}
		return shuffled_array;
	}

	private double[] shuffleRanges(double[] ranges, int[] indices){
		double[] shuffled_ranges = new double[indices.length];
		double sum = 0.0;
		double diff;
		for(int i=0;i<ranges.length;i++){
			int new_index = indices[i];
			if(new_index == 0){
				diff = ranges[new_index];
			}
			else{
				diff = ranges[new_index] - ranges[new_index - 1];
			}
			shuffled_ranges[i] = sum + diff;
			sum += diff;
		}
		return shuffled_ranges;
	}

	@Override
	public String getStochasticMethodDescription()
	{
		return " stochastic universal sampling algorithm for selection";
	}

}
