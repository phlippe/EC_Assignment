package selection;

import algorithm.player59;
import individuals.Individual;
import individuals.Population;

public class TournamentSelection extends ParentSelection {

    private int k;

    public TournamentSelection(ParentSelectionStochastic stochastic, int k) {
        super(stochastic);
        this.k=k;
    }

    @Override
    public int[][] selectParent(Population population, int number_parent_pairs, int number_parents){
        int[][] parent_indices=new int[number_parent_pairs][number_parents];
        int[] tournament_indices = new int[k];
        boolean found_new_number;
        int selectedNum;
        double best_fitness;
        int best_index;
        double current_fitness;

        for(int pair_index=0;pair_index<number_parent_pairs;pair_index++) {
            for (int parent_index=0;parent_index<number_parents;parent_index++) {
                best_fitness = -1;
                best_index = -1;
                for (int i = 0; i < k; i++) {
                    do {
                        found_new_number = true;
                        selectedNum = player59.rnd_.nextInt(population.size());
                        for (int l = 0; l < i; l++) {
                            found_new_number = found_new_number && (tournament_indices[l] != selectedNum);
                        }
                    } while (!found_new_number);
                    tournament_indices[i] = selectedNum;
                    current_fitness = population.get(selectedNum).getFitness();
                    if(current_fitness > best_fitness){
                        best_fitness = current_fitness;
                        best_index = selectedNum;
                    }
                }
                parent_indices[pair_index][parent_index] = best_index;
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
