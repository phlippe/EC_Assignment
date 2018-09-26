package mutation;

import algorithm.TheOptimizers;
import individuals.GeneTypes;
import individuals.Individual;

public class ResetMutation extends Mutation {

    private double init_val;
    private boolean hard_val;
    private boolean exponential;
    private double low_fac;
    private double upp_fac;

    public ResetMutation(GeneTypes[] geneTypes, double init_val){
        super(geneTypes);
        this.init_val = init_val;
        this.hard_val = true;
    }

    public ResetMutation(GeneTypes[] geneTypes, double low_fac, double upp_fac, boolean exponential){
        super(geneTypes);
        this.hard_val = false;
        this.exponential = exponential;
        this.low_fac = low_fac;
        this.upp_fac = upp_fac;
        if(exponential){
            this.low_fac = Math.log(this.low_fac);
            this.upp_fac = Math.log(this.upp_fac);
        }
    }

    @Override
    void applyMutation(double[] genes, Individual individual) {
        if(this.hard_val){
            for(int i=0;i<genes.length;i++) genes[i] = init_val;
        }
        else{
            double rnd_val = TheOptimizers.rnd_.nextDouble() * (upp_fac - low_fac) + low_fac;
            if(exponential)
                rnd_val = Math.exp(rnd_val);
            for(int i=0;i<genes.length;i++){
                genes[i] *= rnd_val;
            }
        }
    }

    @Override
    public String getMutationDescription() {
        String s = "Reset mutation";
        return s;
    }
}
