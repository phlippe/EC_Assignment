package individuals;

import algorithm.TheOptimizers;
import configuration.ConfigurableObject;

/**
 * Created by phlippe on 06.09.18.
 */
public abstract class GenoRepresentation implements ConfigurableObject
{
	public int number_genes;
	public int[] number_additional_params;
	public GeneTypes[] gene_types;

	public GenoRepresentation(int number_genes, int[] number_additional_params, GeneTypes[] gene_types){
		this.number_genes = number_genes;
		this.number_additional_params = number_additional_params;
		this.gene_types = gene_types;
		if(gene_types.length != number_additional_params.length){
			TheOptimizers.println("ERROR (class individuals.GenoRepresentation): number of gene types must be equal to number of additional params");
			System.exit(1);
		}
		for(int i=0;i<gene_types.length;i++){
			for(int j=0;j<i-1;j++){
				if(gene_types[i] == gene_types[j]){
					TheOptimizers.println("ERROR (class individuals.GenoRepresentation): gene types must be unique. Please define a new type is necessary");
					System.exit(1);
				}
			}
		}
	}

	public int getParamPosition(GeneTypes type){
		for(int i=0;i<gene_types.length;i++){
			if(gene_types[i] == type){
				return i;
			}
		}
		return -1;
	}

	public boolean hasGeneType(GeneTypes type){
		for(GeneTypes geneType: gene_types){
			if(geneType == type){
				return true;
			}
		}
		return false;
	}

	public abstract double[] convertGenoToPheno(double[] genes);

	public String getDescription(){
		String s = "Representation class: " + this.getClass().getName() + "\n";
		s += "Number of genes: " + number_genes + "\n";
		s += "Additional params: \n";
		if(number_additional_params.length == 0){
			s += "\t---\n";
		}
		else{
			for(int i=0;i<number_additional_params.length;i++){
				s += "\t (" + i + ") " + gene_types[i] + ": " + number_additional_params[i] + "\n";
			}
		}
		s += getAdditionalDescription();
		return s;
	}

	public abstract String getAdditionalDescription();

}
