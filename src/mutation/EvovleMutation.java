package mutation;

import algorithm.player59;
import individuals.GeneTypes;
import individuals.Individual;

public class EvovleMutation extends Mutation {
    //private double variance;
    private double sigvariance;
    private double[] addparam;
    private double [] newsig;


    public EvovleMutation(){
        super();
       // this.variance = variance;
        //this.sigvariance = sigvariance;
    }

    public EvovleMutation(GeneTypes[] geneTypes){
        super(geneTypes);
        //this.variance = variance;
        //this.sigvariance = sigvariance;
    }

    @Override
    void applyMutation(double[] genes, Individual individual)
    {
        this.addparam = individual.getAdditionalParams(GeneTypes.MULTI_SIGMA);

        double standtau = 1 / Math.sqrt(2 * 100);
        double standardsig = player59.rnd_.nextGaussian() * standtau;

        for (int i=0; i < addparam.length; i++)
        {
            double tau = 1 / Math.sqrt(2 * Math.sqrt(100));
            double sig = player59.rnd_.nextGaussian() * tau;
            addparam[i] = addparam[i] * Math.exp(sig + standardsig);

        }

    }

    @Override
    public String getMutationDescription()
    {
        String s = "";
        s += "Mutation algorithm: Adding a small value from a gaussian distribution to every gene.\n";
        s += "Parameters: \n";
        s += "\tσ = " + sigvariance + "\n";
        return s;
    }

}
