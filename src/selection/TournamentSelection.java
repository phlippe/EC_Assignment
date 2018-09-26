package selection;

import algorithm.TheOptimizers;
import evaluation.BentCigarFunction;
import individuals.BoundRepresentation;
import individuals.GeneTypes;
import individuals.Individual;
import individuals.Population;
import initialization.RandomGenoInitializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class TournamentSelection extends ParentSelection {

    private int k;

    public TournamentSelection(ParentSelectionStochastic stochastic, int k) {
        super(stochastic);
        this.k=k;
    }

    @Override
    public int[][] selectParent(Population population, int number_parent_pairs, int number_parents){
        ArrayList<Individual> FinalBatch=new ArrayList<>(number_parents);
        int[][] parent_indices=new int[number_parent_pairs][number_parents];

        for (int j=0;j<number_parents*number_parent_pairs;j++) {
            ArrayList<Individual> TournamentBatch = new ArrayList<>(k);
            for (int i = 0; i < k; i++) {
                int selectedNum = TheOptimizers.rnd_.nextInt(population.size());
                TournamentBatch.add(i, population.get(selectedNum));
            }
            TournamentBatch.sort(new Comparator<Individual>() {
                @Override
                public int compare(Individual o1, Individual o2) {
                    return -Double.compare((o1).getFitness(), (o2).getFitness());
                }
            });
            FinalBatch.add(TournamentBatch.get(0));
        }
        int counter=0;
        for (int i=0;i<number_parent_pairs;i++){
            for(int j=0;j<number_parents;j++) {
                parent_indices[i][j] = (int)FinalBatch.get(counter).getID();
                counter++;
            }
        }
        /*Searching for their place in the population*/
        for (int a=0;a<number_parent_pairs;a++){
            for (int b=0;b<number_parents;b++){
                for (int i=0;i<population.size();i++){
                    if (population.get(i).getID()==parent_indices[a][b]){
                        parent_indices[a][b]=i;
                    }
                }
            }
        }

        return parent_indices;
    }

    @Override
    void prepareSelection(Population population) {

    }

    @Override
    double getSelectionProbability(Individual individual) {
        return 0;
    }

    @Override
    public String getProbabilityDescription() {
        return null;
    }

}
