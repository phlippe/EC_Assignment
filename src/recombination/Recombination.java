package recombination;

import configuration.ConfigurableObject;
import individuals.GenoRepresentation;
import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public abstract class Recombination implements ConfigurableObject
{

	public ArrayList<Individual> recombine(ArrayList<Individual> parents)
	{
		int number_parents = parents.size();
		GenoRepresentation repr = parents.get(0).getRepresentation();
		ArrayList<Individual> children = new ArrayList<>();
		for(int i=0;i<number_parents;i++){
			Individual child = new Individual();
			child.setRepresentation(repr);
			children.add(child);
		}

		ArrayList<ArrayList<double[]>> parent_comb = new ArrayList<>();
		ArrayList<double[]> parent_genes = new ArrayList<>();
		parent_comb.add(parent_genes);
		for(int i=0;i<repr.gene_types.length;i++){
			parent_comb.add(new ArrayList<>());
		}
		for(Individual p: parents){
			parent_genes.add(p.getGenotype());
			for(int i=0;i<repr.gene_types.length;i++){
				parent_comb.get(i+1).add(p.getAdditionalParams(i));
			}
		}
		for(int param_index=0;param_index<parent_comb.size();param_index++){
			int number_param_genes = parent_comb.get(param_index).get(0).length;
			double[][] child_genes = new double[number_parents][number_param_genes];
			for(int i=0;i<child_genes.length;i++){
				for(int j=0;j<child_genes[i].length;j++){
					child_genes[i][j] = Double.MAX_VALUE;
				}
			}
			applyRecombination(parent_comb.get(param_index), child_genes, number_param_genes);
			for(int i=0;i<child_genes.length;i++){
				if(param_index == 0)
					children.get(i).setGenes(child_genes[i]);
				else
					children.get(i).addAdditionalParams(child_genes[i]);
			}
		}
		return children;
	}

	public ArrayList<Individual> recombineAll(Population population, int[][] parent_indices){
		//System.out.println("Recombine all ("+parent_indices.length+", "+parent_indices[0].length+")");
		ArrayList<Individual> all_children = new ArrayList<>();
		for(int i=0;i<parent_indices.length;i++){
			ArrayList<Individual> parents = new ArrayList<>();
			for(int j=0;j<parent_indices[i].length;j++){
				//System.out.println("Parent index: "+parent_indices[i][j]);
				parents.add(population.get(parent_indices[i][j]));
			}
			all_children.addAll(recombine(parents));
		}
		return all_children;
	}

	abstract void applyRecombination(ArrayList<double[]> parent_genes, double[][] child_genes, int number_param_genes);

	public String getDescription(){
		String s = "";
		s += "Recombination class: " + this.getClass().getName() + "\n";
		s += getRecombinationDescription() + "\n";
		return s;
	}

	protected abstract String getRecombinationDescription();

}
