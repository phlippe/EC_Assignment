package mutation;

import algorithm.player59;
import individuals.Individual;
import selection.ParentSelectionStochastic;

public class CombinedMutation extends Mutation {


    private Mutation[] mutations;
    private double[] probs;

    public CombinedMutation(Mutation mutationA, Mutation mutationB, double probA){
        super(mutationA.geneTypes);
        mutations = new Mutation[2];
        mutations[0] = mutationA;
        mutations[1] = mutationB;
        probs = new double[2];
        probs[0] = probA;
        probs[1] = 1;
    }

    public CombinedMutation(Mutation[] mutations, double[] probs){
        super(mutations[0].geneTypes);
        this.mutations = mutations;
        this.probs = probs;
        double sum = 0.0;
        for(int i=0;i<probs.length;i++){
            sum += probs[i];
            probs[i] = sum;
        }
    }


    @Override
    void applyMutation(double[] genes, Individual individual) {
        int mut_index = ParentSelectionStochastic.searchIndexOfRange(probs, player59.rnd_.nextDouble());
        mutations[mut_index].applyMutation(genes, individual);
    }

    @Override
    public String getMutationDescription() {
        String s = "Combined mutation\n";
        for(int i=0;i<mutations.length;i++){
            s += "Mutation " + i + " (prob " + (i == 0? probs[i] : probs[i]-probs[i-1]) +"): \n";
            s += mutations[i].getMutationDescription();
        }
        return s;
    }
}
