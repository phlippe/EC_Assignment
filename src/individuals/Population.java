package individuals;

import initialization.GenoInitializer;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public class Population
{

	private Individual[] myIndividuals;
	private double maxFitness;
	private Individual maxIndividual;

	public Population(int size){
		myIndividuals = new Individual[size];
		maxFitness = -1;
		maxIndividual = null;
	}

	public Individual get(int index){
		if(index < 0){
			System.out.println("ERROR: Index smaller than 0 ("+index+").");
			return null;
		}
		if(index >= myIndividuals.length){
			System.out.println("ERROR: Index greater than array size of "+myIndividuals.length+" ("+index+").");
			return null;
		}
		return myIndividuals[index];
	}

	public void set(int index, Individual individual){
		myIndividuals[index] = individual;
		if(individual.getFitness() > maxFitness){
			maxFitness = individual.getFitness();
			maxIndividual = individual;
			//System.out.println("Found new max fitness: "+individual.getFitness());
		}
	}

	public int size(){
		return myIndividuals.length;
	}

	public void initialize(GenoRepresentation repr, GenoInitializer gene_init){
		initialize(repr, gene_init, null);
	}

	public void initialize(GenoRepresentation repr, GenoInitializer gene_init, ArrayList<GenoInitializer> params_init){
		for(int i=0;i<myIndividuals.length;i++){
			myIndividuals[i] = new Individual(repr);
			if(params_init != null)
				myIndividuals[i].initialize(gene_init, params_init);
			else
				myIndividuals[i].initialize(gene_init);
		}
	}

	public void reevaluateMaxFitness(){
		System.out.println("Reevaluating max fitness (before: "+maxFitness+")");
		for(int i=0;i<myIndividuals.length;i++){
			if(myIndividuals[i].getFitness() > maxFitness){
				maxFitness = myIndividuals[i].getFitness();
				maxIndividual = myIndividuals[i];
			}
		}
		System.out.println("New max fitness: "+maxFitness);
	}

	public Individual getMaxIndividual(){
		return maxIndividual;
	}

	public void increaseAge(){
		for(Individual i: myIndividuals){
			i.increaseAge();
		}
	}

	public void printStatistic(){
		System.out.println("--------------------------------");
		System.out.println("Population statistic");
		System.out.println("--------------------------------");

		System.out.print("Mean position: [");
		double[] mean_pos = getMeanPosition();
		for(int i=0;i<mean_pos.length;i++){
			if(i > 0) System.out.print(", ");
			System.out.print(mean_pos[i]);
		}
		System.out.println("]");
		System.out.println("Mean distance: " + getMeanDistance(mean_pos));
		System.out.println("--------------------------------");

	}

	private double[] getMeanPosition(){
		double[] mean_pos = new double[myIndividuals[0].getGenotype().length];
		double[] genes;
		for(Individual i: myIndividuals){
			genes = i.getGenotype();
			for(int k=0;k<genes.length;k++)
				mean_pos[k] += genes[k] / myIndividuals.length;
		}
		return mean_pos;
	}

	private double getMeanDistance(double[] mean_pos){
		double mean_dist = 0.0;
		double loc_dist;
		double[] genes;
		for(Individual i: myIndividuals){
			genes = i.getGenotype();
			loc_dist = 0.0;
			for(int k=0;k<genes.length;k++)
				loc_dist += Math.pow(mean_pos[k] - genes[k], 2);
			mean_dist += Math.sqrt(loc_dist) / myIndividuals.length;
		}
		return mean_dist;
	}

}
