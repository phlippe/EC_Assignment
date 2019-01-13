package recombination;

import algorithm.player59;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public class RandomRecombination extends Recombination
{

	@Override
	public void applyRecombination(ArrayList<double[]> parent_genes, double[][] child_genes, int number_param_genes){
		int rand_pos, index_parent, rem_index;
		for(int index_gene=0;index_gene<number_param_genes;index_gene++){
			for(index_parent=parent_genes.size()-1;index_parent>=0;index_parent--){
				rand_pos = player59.rnd_.nextInt(index_parent+1);
				for(rem_index=0;rem_index<parent_genes.size();rem_index++){
					if(child_genes[rem_index][index_gene] == Double.MAX_VALUE)
						rand_pos--;
					if(rand_pos < 0)
						break;
				}
				child_genes[rem_index][index_gene] = parent_genes.get(index_parent)[index_gene];
			}
		}
	}



	@Override
	public String getRecombinationDescription(){
		String s = "";
		s += "Recombination process: Uniform crossover";
		return s;
	}
}
