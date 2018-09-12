package mutation;

import configuration.ConfigurableObject;
import individuals.GeneTypes;
import individuals.Individual;

/**
 * Created by phlippe on 07.09.18.
 */
public abstract class Mutation implements ConfigurableObject
{

	GeneTypes[] geneTypes;

	public Mutation(){
		this.geneTypes = GeneTypes.values();
	}

	public Mutation(GeneTypes[] geneTypes){
		this.geneTypes = geneTypes;
	}

	public void mutate(Individual individual){
		for(GeneTypes type: geneTypes)
		{
			double[] genes;
			if (type == GeneTypes.OPT_GENES)
				genes = individual.getGenotype();
			else{
				if(individual.getRepresentation().hasGeneType(type)){
					genes = individual.getAdditionalParams(type);
				}
				else{
					continue;
				}
			}
			applyMutation(genes, individual);
		}
	}

	abstract void applyMutation(double[] genes, Individual individual);

	public String getDescription(){
		String s = "";
		s += "Mutation class: " + this.getClass().getName() + "\n";
		s += getMutationDescription();
		return s;
	}

	public abstract String getMutationDescription();

}
