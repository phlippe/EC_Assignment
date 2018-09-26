package recombination;

import algorithm.TheOptimizers;

import java.util.ArrayList;

public class BlendRandomRecombination extends Recombination
{
    private double sigma;

    public BlendRandomRecombination(double sigma){
        this.sigma = sigma;
    }

    @Override
    public void applyRecombination(ArrayList<double[]> parent_genes, double[][] child_genes, int number_param_genes) {
        int rand_pos, index_parent, rem_index, index_child, index_other_children;
        double dist_sum;
        for(int index_gene=0;index_gene<number_param_genes;index_gene++){
            // RANDOM RECOMBINATION
            for(index_parent=parent_genes.size()-1;index_parent>=0;index_parent--){
                rand_pos = TheOptimizers.rnd_.nextInt(index_parent+1);
                for(rem_index=0;rem_index<parent_genes.size();rem_index++){
                    if(child_genes[rem_index][index_gene] == Double.MAX_VALUE)
                        rand_pos--;
                    if(rand_pos < 0)
                        break;
                }
                child_genes[rem_index][index_gene] = parent_genes.get(index_parent)[index_gene];
            }

            // BLEND WITH GAUSSIAN
            for(index_child=0;index_child<parent_genes.size();index_child++){
                dist_sum = 0.0;
                for(index_other_children=0;index_other_children<parent_genes.size();index_other_children++){
                    if(index_other_children != index_child)
                        dist_sum += child_genes[index_other_children][index_gene];
                }
                dist_sum /= parent_genes.size() - 1;
                child_genes[index_child][index_gene] += (child_genes[index_child][index_gene] - dist_sum) * TheOptimizers.rnd_.nextGaussian() * sigma;
            }
        }
    }


    @Override
    public String getRecombinationDescription(){
        String s = "";
        s += "Recombination process: Blend Random Crossover\n";
        s += "\tsigma = " + this.sigma + "\n";
        return s;
    }

    public static void main(String args[]){
        TheOptimizers opt = new TheOptimizers();
        opt.setSeed(1);
        double[] p1 = {1.0, 0.6, 0.0, 1.8};
        double[] p2 = {-1.0, 0.8, 0.0, 2.0};
        ArrayList<double[]> par_genes = new ArrayList<>();
        par_genes.add(p1);
        par_genes.add(p2);
        double[][] child_genes = new double[par_genes.size()][p1.length];
        for(int i=0;i<child_genes.length;i++)
            for(int j=0;j<child_genes[i].length;j++)
                child_genes[i][j] = Double.MAX_VALUE;

        BlendRandomRecombination rec = new BlendRandomRecombination(0.1);
        rec.applyRecombination(par_genes, child_genes, p1.length);

        System.out.println("Child genes: ");
        for(int i=0;i<child_genes.length;i++){
            System.out.print("[");
            for(int j=0;j<child_genes[i].length;j++){
                if(j > 0) System.out.print(", ");
                System.out.print(child_genes[i][j]);
            }
            System.out.println("]");
        }
    }
}