package island;

import algorithm.*;
import configuration.Configuration;
import configuration.StandardConfig;
import individuals.Individual;
import individuals.MultiPopulation;
import individuals.Population;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class DistributedEvolutionaryCycle implements EvolutionaryAlgorithm{


    private IslandParams myParams;
    private ArrayList<EvolutionaryAlgorithm> islands;
    private int no_cycle;
    private Tracer tracer;

    public DistributedEvolutionaryCycle(StandardConfig[] configs, IslandParams myParams){
        this(new ArrayList<>(Arrays.asList(configs)), myParams);
    }

    public DistributedEvolutionaryCycle(EvolutionaryAlgorithm[] eas, IslandParams myParams){
        this(new ArrayList<>(Arrays.asList(eas)), myParams);
    }

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
        tracer = new Tracer(false, "");
    }

    public DistributedEvolutionaryCycle(Configuration config, int no_island, IslandParams myParams){
        this.islands = new ArrayList<>();
        for(int i=0;i<no_island;i++)
            islands.add(new EvolutionaryCycle(config));
        this.myParams = myParams;
        this.no_cycle = 0;
        tracer = new Tracer(false, "");
    }

    public void addTracer(Tracer tr){
        tracer = tr;
        for(int i=0;i<islands.size();i++){
            Tracer island_tracer = new Tracer(tr.isActive(), "island_" + i);
            island_tracer.setParentTracer(tracer);
            islands.get(i).addTracer(island_tracer);
        }
    }

    @Override
    public void initialize(){
        tracer.initialize();
        tracer.addTraceFile(TraceTags.ISLAND_EXCHANGE);
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
        int[][] individual_index = determineExchangeIndividuals();

        Individual[][] individuals = new Individual[individual_index.length][individual_index[0].length];
        for(int i=0;i<individuals.length;i++)
            for(int j=0;j<individuals[i].length;j++)
                individuals[i][j] = islands.get(i).getPopulation().get(j);

        int exchange_island;
        for(int island_index=0;island_index<islands.size();island_index++){
            for(int ind_index=0;ind_index<individual_index[island_index].length;ind_index++){
                exchange_island = getExchangeIsland(island_index, ind_index);
                islands.get(exchange_island).getPopulation().set(individual_index[exchange_island][ind_index], individuals[island_index][ind_index]);
                tracer.addTraceContent(TraceTags.ISLAND_EXCHANGE, island_index + ", " + exchange_island + ", " + individuals[island_index][ind_index].getFitness());
            }
        }
    }

    private int getExchangeIsland(int island_index, int ind_index){
        int exchange_island = -1;
        switch(myParams.getTopologyType()){
            case RING:
                exchange_island = (island_index == 0 ? islands.size() : island_index) - 1;
                break;
            case COMPLETE:
                exchange_island = (island_index + ind_index) % (islands.size() - 1);
                if(exchange_island >= island_index)
                    exchange_island++;
                break;
        }
        return exchange_island;
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
                    double[] best_fitness = new double[myParams.getNoIndividualExchange()];
                    for(int i=0;i<best_fitness.length;i++)
                        best_fitness[i] = -1;

                    for(int no_ind=0;no_ind<island_pop.size();no_ind++){
                        addElementToNBestArray(best_fitness, individual_index[island_index], island_pop.get(no_ind).getFitness(), no_ind);
                    }
                    break;
                case RANDOM:
                    for(int no_ind=0;no_ind<myParams.getNoIndividualExchange();no_ind++){
                        int rnd_ind;
                        do{
                            rnd_ind = player59.rnd_.nextInt(island_pop.size());
                        }while(isInArray(rnd_ind, individual_index[island_index]));
                        individual_index[island_index][no_ind] = rnd_ind;
                    }
                    break;
                case MULTI_CULTI:
                    double[][] max_dist = new double[islands.size()][myParams.getNoIndividualExchange()];
                    int[][] max_dist_ind = new int[islands.size()][myParams.getNoIndividualExchange()];

                    for(int i=0;i<max_dist.length;i++)
                        for(int j=0;j<max_dist[i].length;j++)
                            max_dist[i][j] = -1;

                    // Run over all islands and find the N most different individuals
                    for(int other_island=0;other_island<islands.size();other_island++) {
                        if (other_island == island_index)
                            continue;
                        Population other_island_pop = islands.get(other_island).getPopulation();
                        double dist;
                        for(int ind_is=0;ind_is < island_pop.size(); ind_is++){
                            double min_dist = Double.MAX_VALUE;
                            Individual i = island_pop.get(ind_is);
                            for(int ind_other=0;ind_other < other_island_pop.size(); ind_other++){
                                dist = i.getDistance(other_island_pop.get(ind_other));
                                if(dist < min_dist)
                                    min_dist = dist;
                            }
                            addElementToNBestArray(max_dist[other_island], max_dist_ind[other_island], min_dist, ind_is);
                        }
                    }
                    //printArray(max_dist, "Max dist");

                    int exchange_island;
                    int exchange_island_count;
                    for(int no_ind=0;no_ind<myParams.getNoIndividualExchange();no_ind++){
                        exchange_island = getExchangeIsland(island_index, no_ind);
                        exchange_island_count = 0;
                        while(isInArray(max_dist_ind[exchange_island][exchange_island_count], individual_index[island_index])){
                            exchange_island_count++;
                        }
                        individual_index[island_index][no_ind] = max_dist_ind[exchange_island][exchange_island_count];
                    }
                    break;
            }
        }
        return individual_index;
    }

    private void printArray(double[][] a, String name){
        player59.println(name);
        for(int i=0;i<a.length;i++){
            player59.print("[");
            for(int j=0;j<a[i].length;j++){
                if(j > 0)
                    player59.print(", ");
                player59.print(a[i][j]);
            }
            player59.println("]");
        }
    }

    private void printArray(int[][] a, String name){
        player59.println(name);
        for(int i=0;i<a.length;i++){
            player59.print("[");
            for(int j=0;j<a[i].length;j++){
                if(j > 0)
                    player59.print(", ");
                player59.print(a[i][j]);
            }
            player59.println("]");
        }
    }

    private boolean isInArray(int val, int[] array){
        for(int i: array){
            if(val == i) return true;
        }
        return false;
    }

    private void addElementToNBestArray(double[] best_val, int[] best_val_ind, double new_val, int new_ind){
        for(int i=best_val.length-1;i>=-1;i--){
            if(i == -1 || best_val[i] > new_val){
                if(i == best_val.length - 1)
                    break;
                else{
                    if(i == -1)
                        i = 0;
                    for(int j=best_val.length-1;j>i;j--){
                        best_val[j] = best_val[j - 1];
                        best_val_ind[j] = best_val_ind[j - 1];
                    }
                    best_val[i] = new_val;
                    best_val_ind[i] = new_ind;
                    break;
                }
            }
        }
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
            player59.println("Could not write results to file. Error message:");
            player59.println(e.getMessage());
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

    @Override
    public void writeTraceFiles(){
        tracer.writeOut();
        for(EvolutionaryAlgorithm ea: islands)
            ea.writeTraceFiles();
    }

    @Override
    public String getExtraDescription() {
        return "";
    }
}
