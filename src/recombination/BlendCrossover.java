package recombination;

import algorithm.player59;

import java.util.ArrayList;

/**
 * Created by rick on 12.09.18.
 */
public class BlendCrossover extends Recombination
{
    private double alpha;

    public BlendCrossover(double alpha){
        this.alpha = alpha;

    }
    @Override
    public void applyRecombination(ArrayList<double[]> parent_genes, double[][] child_genes, int number_param_genes) {


        int number_parents = parent_genes.size();
        for (int index_child = 0; index_child < number_parents; index_child++) { //going over each child
//            System.out.println(index_child);
            for (int index_gene = 0; index_gene < number_param_genes; index_gene++) {//going over all genes
                double sum_parent_gene = 0;
                double gamma = (1+2*alpha)* player59.rnd_.nextDouble()-alpha;
                for (int index_parent = 0; index_parent < number_parents; index_parent++) {//going over all parents
                    if (index_child == index_parent){
                        sum_parent_gene =+ gamma * parent_genes.get(index_parent)[index_gene]; //the ith child gets the alpha
                    }
                    else {
                        sum_parent_gene =+ (1 - gamma) * parent_genes.get(index_parent)[index_gene] / (number_parents - 1); //average of all gene of each parent
                    }
                }
//                System.out.println(sum_parent_gene);
                child_genes[index_child][index_gene] = sum_parent_gene;
            }
        }
    }


    @Override
    public String getRecombinationDescription(){
        String s = "";
        s += "Recombination process: Blend Crossover";
        return s;
    }
}
