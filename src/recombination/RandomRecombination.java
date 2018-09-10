package recombination;

import individuals.GenoRepresentation;
import individuals.Individual;
import individuals.Population;
import algorithm.TheOptimizers;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public class RandomRecombination extends Recombination
{

	@Override
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
		double[][] child_genes = new double[number_parents][repr.number_genes];
		for(int i=0;i<child_genes.length;i++){
			for(int j=0;j<child_genes[i].length;j++){
				child_genes[i][j] = Double.MAX_VALUE;
			}
		}
		int rand_pos, index_parent, j, rem_index;
		for(int index_gene=0;index_gene<repr.number_genes;index_gene++){
			for(index_parent=number_parents-1;index_parent>=0;index_parent--){
				rand_pos = TheOptimizers.rnd_.nextInt(index_parent+1);
				for(rem_index=0;rem_index<number_parents;rem_index++){
					if(child_genes[rem_index][index_gene] == Double.MAX_VALUE)
						rand_pos--;
						if(rand_pos < 0)
							break;
				}
				child_genes[rem_index][index_gene] = parents.get(index_parent).getGenotype()[index_gene];
			}
		}
		for(int i=0;i<child_genes.length;i++){
			children.get(i).setGenes(child_genes[i]);
		}
		return children;
	}

	@Override
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

	@Override
	public String getRecombinationDescription(){
		String s = "";
		s += "Recombination process: Uniform crossover";
		return s;
	}
}
