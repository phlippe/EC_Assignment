package recombination;

import algorithm.TheOptimizers;

import java.util.ArrayList;

/**
 * Created by rick on 12.09.18.
 */
public class SimulatedBinaryCrossover
		extends Recombination
{
    private double eta;

    public SimulatedBinaryCrossover(double eta){
        this.eta = eta;

    }
    @Override
    public void applyRecombination(ArrayList<double[]> parent_genes, double[][] child_genes, int number_param_genes) {

        int number_parents = parent_genes.size();
        for (int index_gene = 0; index_gene < number_param_genes; index_gene++) {
            double rand = TheOptimizers.rnd_.nextDouble();
            double beta;
            if(rand <= 0.5){
                beta = 2.0 * rand;
            }
            else{
                beta = 1.0 / (2.0 * (1.0 - rand));
            }
            beta = Math.pow(beta, 1.0 / (1.0 + eta));
            for (int index_child = 0; index_child < number_parents; index_child++) {
                child_genes[index_child][index_gene] = 0.0;
                for(int index_parent = 0; index_parent < number_parents; index_parent++) {
                    if(index_parent == index_child){
                        double diff = 0.5 * ((1 + beta) * parent_genes.get(index_parent)[index_gene]);
                        child_genes[index_child][index_gene] += diff;
                    }
                    else{
                        double diff = 0.5 * ((1 - beta) * parent_genes.get(index_parent)[index_gene]) / (number_parents - 1);
                        child_genes[index_child][index_gene] += diff;
                    }
                }
                //System.out.println("Child genes: " + child_genes[index_child][index_gene]);
            }
        }
    }


    @Override
    public String getRecombinationDescription(){
        String s = "";
        s += "Recombination process: Simulated binary crossover, eta = " + eta;
        return s;
    }

    public static void main(String args[]){
        ArrayList<double[]> parent_genes = new ArrayList<>();
        double[] parent_1 = {1.0, -1.0, 0.5, 0.0};
        double[] parent_2 = {2.0, 1.0, 0.5, 0.2};
        parent_genes.add(parent_1);
        parent_genes.add(parent_2);
        double[][] children = new double[2][parent_1.length];
        int number_param_genes = parent_1.length;
        TheOptimizers opt = new TheOptimizers();
        opt.setSeed(1);
        SimulatedBinaryCrossover a = new SimulatedBinaryCrossover(1.0);
        a.applyRecombination(parent_genes, children, number_param_genes);
        for(int i=0;i<children.length;i++){
            TheOptimizers.print("Child " + i + ": ");
            for(int j=0;j<children[i].length;j++){
                TheOptimizers.print(children[i][j] + ", ");
            }
            TheOptimizers.println("");
        }
    }
}
