package island;

import algorithm.EvolutionaryAlgorithm;
import algorithm.EvolutionaryCycle;
import algorithm.TheOptimizers;
import configuration.Configuration;
import individuals.Individual;
import individuals.MultiPopulation;
import individuals.Population;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DistributedEvolutionaryCycle implements EvolutionaryAlgorithm{


    private IslandParams myParams;
    private ArrayList<EvolutionaryAlgorithm> islands;
    private int no_cycle;


    public DistributedEvolutionaryCycle(ArrayList cycles, IslandParams myParams){
        if(cycles.get(0) instanceof EvolutionaryAlgorithm)
            this.islands = cycles;
        else
            if(cycles.get(0) instanceof Configuration){
                this.islands = new ArrayList<>();
                for(Object config: cycles)
                    islands.add(new EvolutionaryCycle((Configuration)config));
            }

        this.myParams = myParams;
        this.no_cycle = 0;
    }

    public DistributedEvolutionaryCycle(Configuration config, int no_island, IslandParams myParams){
        this.islands = new ArrayList<>();
        for(int i=0;i<no_island;i++)
            islands.add(new EvolutionaryCycle(config));
        this.myParams = myParams;
        this.no_cycle = 0;
    }

    @Override
    public void initialize(){
        for(EvolutionaryAlgorithm ea: islands)
            ea.initialize();
    }

    @Override
    public void run_single_cycle() {
        if(no_cycle > 0 && no_cycle % myParams.getEpochSize() == 0)
            exchangeIndividuals();
        for(EvolutionaryAlgorithm ea: islands)
            ea.run_single_cycle();
        no_cycle++;
    }

    private void exchangeIndividuals(){
        TheOptimizers.println("Exchange individuals");
        int[][] individual_index = determineExchangeIndividuals();

        Individual[][] individuals = new Individual[individual_index.length][individual_index[0].length];
        for(int i=0;i<individuals.length;i++)
            for(int j=0;j<individuals[i].length;j++)
                individuals[i][j] = islands.get(i).getPopulation().get(j);

        int exchange_island;
        switch(myParams.getTopologyType()){
            case RING:
                for(int island_index=0;island_index<islands.size();island_index++){
                    exchange_island = (island_index == 0 ? islands.size() : island_index) - 1;
                    for(int ind_index=0;ind_index<individual_index[island_index].length;ind_index++){
                        islands.get(exchange_island).getPopulation().set(individual_index[exchange_island][ind_index], individuals[island_index][ind_index]);
                    }
                }
                break;
            case COMPLETE:
                for(int island_index=0;island_index<islands.size();island_index++){
                    for(int ind_index=0;ind_index<individual_index[island_index].length;ind_index++){
                        exchange_island = (island_index + ind_index) % (islands.size() - 1);
                        if(exchange_island >= island_index)
                            exchange_island++;
                        islands.get(exchange_island).getPopulation().set(individual_index[exchange_island][ind_index], individuals[island_index][ind_index]);
                    }
                }
                break;
        }
        TheOptimizers.println("Finished");
    }

    private int[][] determineExchangeIndividuals(){
        int[][] individual_index = new int[islands.size()][myParams.getNoIndividualExchange()];
        for(int i=0;i<individual_index.length;i++)
            for(int j=0;j<individual_index[i].length;j++)
                individual_index[i][j] = -1;

        for(int island_index=0;island_index<islands.size();island_index++){
            Population island_pop = islands.get(island_index).getPopulation();
            switch(myParams.getExchangeType()){
                case BEST:
                    //TODO: IMPLEMENT THIS
                    for(int no_ind=0;no_ind<myParams.getNoIndividualExchange();no_ind++){
                        individual_index[island_index][no_ind] = -1;
                    }
                    break;
                case RANDOM:
                    for(int no_ind=0;no_ind<myParams.getNoIndividualExchange();no_ind++){
                        int rnd_ind;
                        do{
                            rnd_ind = TheOptimizers.rnd_.nextInt(island_pop.size());
                        }while(isInArray(rnd_ind, individual_index[island_index]));
                        individual_index[island_index][no_ind] = rnd_ind;
                    }
                    break;
                case MULTI_CULTI:
                    // TODO: IMPLEMENT THIS
                    break;
            }
        }
        return individual_index;
    }

    private boolean isInArray(int val, int[] array){
        for(int i: array){
            if(val == i) return true;
        }
        return false;
    }

    @Override
    public double[] getBestSolution() {
        double best_fitness = -1;
        double[] best_solution = null;
        for(EvolutionaryAlgorithm ea: islands)
            if(best_fitness < ea.getBestFitness()) {
                best_fitness = ea.getBestFitness();
                best_solution = ea.getBestSolution();
            }
        return best_solution;
    }

    @Override
    public double getBestFitness() {
        double best_fitness = -1;
        for(EvolutionaryAlgorithm ea: islands)
            if(best_fitness < ea.getBestFitness())
                best_fitness = ea.getBestFitness();
        return best_fitness;
    }

    @Override
    public Population getPopulation() {
        ArrayList<Population> populations = new ArrayList<>();
        for(EvolutionaryAlgorithm ea: islands)
            populations.add(ea.getPopulation());
        return new MultiPopulation(populations);
    }

    @Override
    public int getEvalsPerCycle() {
        int evals_per_cycle = 0;
        for(EvolutionaryAlgorithm ea: islands)
            evals_per_cycle += ea.getEvalsPerCycle();
        return evals_per_cycle;
    }

    @Override
    public String getName(){
        return myParams.getName();
    }

    @Override
    public void logResults() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        new File("logs/").mkdirs();
        String filename = "logs/log_island_" + dateFormat.format(date) + ".txt";
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println("Date: " + (new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss")).format(date));
            out.println(getLogString());
        }
        catch (Exception e){
            TheOptimizers.println("Could not write results to file. Error message:");
            TheOptimizers.println(e.getMessage());
        }
    }

    @Override
    public String getLogString(){
        String s = Configuration.createHeading("General island config", '=');
        s += "Number of islands: " + islands.size() + "\n";
        s += "Epoch size: " + myParams.getEpochSize() + "\n";
        s += "Number of exchanged individuals: " + myParams.getNoIndividualExchange() + "\n";
        s += "Exchange type: " + myParams.getExchangeType() + "\n";
        s += "Topology type: " + myParams.getTopologyType() + "\n";
        s += Configuration.repeatChar('-',50) + "\n";
        s += Configuration.createHeading("Configurations for single islands",'=');
        for(EvolutionaryAlgorithm ea: islands)
            s += ea.getLogString() + "\n";
        return s;
    }
}
