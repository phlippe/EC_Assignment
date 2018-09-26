package mutation;

import algorithm.TheOptimizers;
import individuals.GeneTypes;
import individuals.Individual;

public class MultiSigma extends Mutation {


    public MultiSigma(GeneTypes[] geneTypes){
        super(geneTypes);
    }


    @Override
    void applyMutation(double[] genes, Individual individual) {
        double [] addparam = individual.getAdditionalParams(GeneTypes.MULTI_SIGMA);
        for (int i = 0; i < genes.length; i++)
        {
                double change = TheOptimizers.rnd_.nextGaussian() * addparam[i];
                genes[i] += change;

        }
    }

    @Override
    public String getMutationDescription() {
        return null;
    }
}
