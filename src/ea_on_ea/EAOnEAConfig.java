package ea_on_ea;

import configuration.ConfigParams;
import configuration.Configuration;
import individuals.GeneTypes;
import individuals.GenoRepresentation;
import initialization.GenoInitializer;
import initialization.RandomGenoInitializer;
import mutation.GaussianMutation;
import mutation.Mutation;
import recombination.BlendRandomRecombination;
import recombination.Recombination;
import selection.*;

import java.util.ArrayList;

public class EAOnEAConfig extends Configuration {


    public EAOnEAConfig(){
        init();
    }

    @Override
    protected GenoRepresentation createRepresentation() {
        int[] number_add_genes = {1, 1, 1};
        GeneTypes[] geneTypes = {GeneTypes.ALPHA, GeneTypes.BETA, GeneTypes.GAMMA};
        GenoRepresentation representation = new GenoRepresentation(1, number_add_genes, geneTypes) {
            @Override
            public double[] convertGenoToPheno(double[] genes) {
                return genes;
            }

            @Override
            public String getAdditionalDescription() {
                return null;
            }
        };
        return representation;
    }

    @Override
    protected ArrayList<Mutation> createMutationOperators() {
        ArrayList<Mutation> mutations = new ArrayList<>();
        GeneTypes[] alpha = {GeneTypes.ALPHA};
        GeneTypes[] beta = {GeneTypes.BETA};
        GeneTypes[] gamma = {GeneTypes.GAMMA};
        GaussianMutation gaussianMutationAlpha = new GaussianMutation(50, alpha);
        GaussianMutation gaussianMutationBeta = new GaussianMutation(0.2, beta);
        GaussianMutation gaussianMutationGamma = new GaussianMutation(0.1, gamma);

        mutations.add(gaussianMutationAlpha);
        mutations.add(gaussianMutationBeta);
        mutations.add(gaussianMutationGamma);

        return mutations;
    }

    @Override
    protected Recombination createRecombination() {
        Recombination recombination = new BlendRandomRecombination(0.1);
        return recombination;
    }

    @Override
    protected ParentSelection createParentSelection() {
        return new TournamentSelection(new ParentSelectionStochasticRoulette(), 2);
    }

    @Override
    protected SurvivorSelection createSurvivorSelection() {
        return new SurvivorRoundRobinTournamentSelection(4);
    }

    @Override
    protected GenoInitializer createGenoInitializer() {
        return new RandomGenoInitializer();
    }

    @Override
    protected ArrayList<GenoInitializer> createAddParamsInitializer() {
        ArrayList<GenoInitializer> initializers = new ArrayList<>();
        initializers.add(new RandomGenoInitializer(6000, 0)); // Alpha
        initializers.add(new RandomGenoInitializer(20, 0)); // Beta
        initializers.add(new RandomGenoInitializer(1, -2)); // Gamma
        return initializers;
    }

    @Override
    public int getPopulationSize() {
        return 24;
    }

    @Override
    public int getNumberOfRecombinations() {
        return 6;
    }

    @Override
    public int getParentArity() {
        return 2;
    }

    @Override
    public ConfigParams getParameters() {
        return new ConfigParams(24, 6, 2);
    }
}
