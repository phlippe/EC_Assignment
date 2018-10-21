package mutation;

import algorithm.player59;
import individuals.GeneTypes;
import individuals.Individual;

public class EvovleMutation extends Mutation {

    private double standtau;
    private double tau;


    public EvovleMutation(){
        super();
        setTau(100,1);
    }

    public EvovleMutation(GeneTypes[] geneTypes){
        this(geneTypes, 100, 1);
    }

    public EvovleMutation(GeneTypes[] geneTypes, int population_size, double scale_fac){
        super(geneTypes);
        setTau(population_size, scale_fac);
    }

    private void setTau(int population_size, double scale_fac){
        this.standtau = scale_fac / Math.sqrt(2. * population_size);
        this.tau = scale_fac / Math.sqrt(2. * Math.sqrt(population_size));
    }

    @Override
    void applyMutation(double[] genes, Individual individual)
    {
        double[] addparam = individual.getAdditionalParams(GeneTypes.MULTI_SIGMA);

        double standardsig = player59.rnd_.nextGaussian() * standtau;

        for (int i=0; i < addparam.length; i++)
        {
            double sig = player59.rnd_.nextGaussian() * tau;
            addparam[i] = addparam[i] * Math.exp(sig + standardsig);

        }

    }

    @Override
    public String getMutationDescription()
    {
        String s = "";
        s += "Mutation algorithm: self-adaptation mutation (multi-sigma)\n";
        s += "\ttau = " + this.tau + "\n\tstandtau = " + this.standtau + "\n";
        return s;
    }

}
