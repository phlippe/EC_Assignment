package initialization;

import configuration.ConfigurableObject;

/**
 * Created by phlippe on 06.09.18.
 */
public abstract class GenoInitializer implements ConfigurableObject
{

	public abstract void initializeArray(double[] array);

	@Override
	public String getDescription()
	{
		String s = "";
		s += "Initializer class: " + this.getClass().getName();
		return s;
	}

}
