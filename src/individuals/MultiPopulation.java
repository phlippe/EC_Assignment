package individuals;

import initialization.GenoInitializer;

import java.util.ArrayList;

public class MultiPopulation extends Population {

    private ArrayList<Population> populations;

    public MultiPopulation(ArrayList<Population> populations) {
        super(0);
        this.populations = populations;
    }

    @Override
    public Individual get(int index){
        int start_index = 0;
        for(Population p: populations)
            if(index >= (p.size() + start_index))
                start_index += p.size();
            else{
                return p.get(index - start_index);
            }
        return null;
    }

    @Override
    public void set(int index, Individual individual){
        int start_index = 0;
        for(Population p: populations)
            if(index >= (p.size() + start_index))
                start_index += p.size();
            else{
                p.set(index - start_index, individual);
                break;
            }
    }

    @Override
    public int size(){
        int size = 0;
        for(Population p: populations)
            size += p.size();
        return size;
    }

    @Override
    public void initialize(GenoRepresentation repr, GenoInitializer gene_init){
        for(Population p: populations)
            p.initialize(repr, gene_init);
    }

    @Override
    public void initialize(GenoRepresentation repr, GenoInitializer gene_init, ArrayList<GenoInitializer> params_init){
        for(Population p: populations)
            p.initialize(repr, gene_init, params_init);
    }

    @Override
    public void reevaluateMaxFitness(){
        for(Population p: populations)
            p.reevaluateMaxFitness();
    }

    @Override
    public Individual getMaxIndividual(){
        Individual maxIndividual = populations.get(0).getMaxIndividual();
        for(Population p: populations)
            if(p.getMaxIndividual().getFitness() > maxIndividual.getFitness())
                maxIndividual = p.getMaxIndividual();
        return maxIndividual;
    }

    @Override
    public void increaseAge(){
        for(Population p: populations){
            p.increaseAge();
        }
    }

    @Override
    public Individual getMinIndividual(){
        Individual minIndividual = populations.get(0).getMinIndividual();
        for(Population p: populations)
            if(p.getMinIndividual().getFitness() < minIndividual.getFitness())
                minIndividual = p.getMinIndividual();
        return minIndividual;
    }


}
