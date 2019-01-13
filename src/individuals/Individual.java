package individuals;

import algorithm.player59;
import initialization.GenoInitializer;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public class Individual
{

	private static long ID_COUNTER = 0;

	private double[] genes;
	private ArrayList<double[]> add_params;
	private GenoRepresentation myRepr;
	private double myFitness;
	private double age;
	private long id;
	private double fitnessFactor = 1.0;
	private double distanceSum = 0.0;

	public Individual(){
		myFitness = -1;
		myRepr = null;
		genes = null;
		add_params = new ArrayList<>();
		age = 0;
		id = ID_COUNTER;
		ID_COUNTER++;
	}

	public Individual(GenoRepresentation repr){
		this();
		myRepr = repr;
		genes = new double[myRepr.number_genes];
		add_params = new ArrayList<>();
		for(int i=0;i<myRepr.number_additional_params.length;i++){
			add_params.add(new double[myRepr.number_additional_params[i]]);
		}
	}

	public void setFitnessFactor(double factor){
		fitnessFactor = factor;
	}

	public void setFitnessFactor(double factor, double sum_distances){
		setFitnessFactor(factor);
		distanceSum = sum_distances;
	}

	public double getFitnessFactor(){
		return fitnessFactor;
	}

	public double getDistanceSum(){
		return distanceSum;
	}

	public void initialize(GenoInitializer gene_init){
		gene_init.initializeArray(genes);
	}

	public void initialize(GenoInitializer gene_init, ArrayList<GenoInitializer> params_init){
		gene_init.initializeArray(genes);
		for(int i=0;i<add_params.size();i++){
			params_init.get(i).initializeArray(add_params.get(i));
		}
	}


	public double[] getGenotype(){
		return genes;
	}

	public double[] getPhenotype(){
		return myRepr.convertGenoToPheno(genes);
	}

	public double[] getAdditionalParams(int index){
		if(index >= add_params.size() || index < 0){
			player59.println("ERROR (class Individual): Index is out of boundaries for individual. Index: "+index+", Size: "+add_params);
			//System.exit(1);
		}
		return add_params.get(index);
	}

	public double[] getAdditionalParams(GeneTypes type){
		int index = myRepr.getParamPosition(type);
		if(index < 0){
			player59.println("ERROR (class Individual): Unknown gene type requested: "+type.name());
			player59.print("Implemented in current initialization: ");
			if(myRepr.gene_types.length == 0){
				player59.print("---");
			}
			else{
				for (GeneTypes implType : myRepr.gene_types)
				{
					player59.print(implType.name() + ", ");
				}
			}
			player59.println("");
		}
		return getAdditionalParams(index);
	}

	public void setFitness(double fitness){
		myFitness = fitness;
	}

	public double getFitness(){
		return myFitness * fitnessFactor;
	}

	public double getPureFitness(){
		return myFitness;
	}

	public GenoRepresentation getRepresentation(){
		return myRepr;
	}

	public void setRepresentation(GenoRepresentation repr){
		myRepr = repr;
	}

	public void setGenes(double[] g){
		genes = g;
	}

	public void addAdditionalParams(double[] p){
		add_params.add(p);
	}

	public void setAddParams(ArrayList<double[]> params){
		add_params = params;
	}

	public double getAge(){
		return age;
	}

	public void increaseAge(){
		age = age + 1;
	}

	public long getID(){
		return id;
	}

	public double getDistance(Individual individual){
		double[] others_genes = individual.getGenotype();
		return getDistance(others_genes);
	}

	public double getDistance(double[] others_genes){
		double dist = 0.0;
		for(int k=0;k<genes.length;k++)
			dist += Math.pow(others_genes[k] - genes[k], 2);
		dist = Math.sqrt(dist);
		return dist;
	}

	public double getDistance(Individual individual, double[] weights){
		double[] others_genes = individual.getGenotype();
		double dist = 0.0;
		for(int k=0;k<genes.length;k++)
			dist += Math.pow((others_genes[k] - genes[k]) / weights[k], 2);
		dist = Math.sqrt(dist);
		return dist;
	}

	public double getDistance(Individual individual, double max_dist){
		double[] others_genes = individual.getGenotype();
		double dist = 0.0;
		double gene_dist = 0.0;
		for(int k=0;k<genes.length;k++) {
			gene_dist = (others_genes[k] - genes[k]);
			if (gene_dist > max_dist)
				return gene_dist;
			dist += gene_dist * gene_dist;
		}
		dist = Math.sqrt(dist);
		return dist;
	}

}
