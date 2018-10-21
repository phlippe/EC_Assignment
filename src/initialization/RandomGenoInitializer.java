package initialization;

import algorithm.player59;

/**
 * Created by phlippe on 06.09.18.
 */
public class RandomGenoInitializer extends GenoInitializer
{

	private double upper_limit;
	private double lower_limit;

	public RandomGenoInitializer(){
		upper_limit = 5;
		lower_limit = -5;
	}

	public RandomGenoInitializer(double upper_limit, double lower_limit){
		this.upper_limit = upper_limit;
		this.lower_limit = lower_limit;
	}

	@Override
	public void initializeArray(double[] array)
	{
		for(int i=0;i<array.length;i++){
			array[i] = player59.rnd_.nextDouble() * (upper_limit - lower_limit) + lower_limit;
		}
	}
}
