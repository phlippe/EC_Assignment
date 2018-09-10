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
			System.out.println("Found new max fitness: "+individual.getFitness());
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

}
