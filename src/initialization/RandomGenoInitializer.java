package initialization;

import algorithm.TheOptimizers;

/**
 * Created by phlippe on 06.09.18.
 */
public class RandomGenoInitializer extends GenoInitializer
{

	public RandomGenoInitializer(){

	}

	@Override
	public void initializeArray(double[] array)
	{
		for(int i=0;i<array.length;i++){
			array[i] = TheOptimizers.rnd_.nextDouble() * 10 - 5;
		}
	}
}
