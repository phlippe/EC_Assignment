package individuals;

import algorithm.TheOptimizers;
import configuration.ConfigParams;
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
	private boolean useFitnessSharing = false;
	private double sigma_sharing = -1;
	private boolean useFitnessSharingMultiSigma = false;

	public Population(int size){
		myIndividuals = new Individual[size];
		maxFitness = -1;
		maxIndividual = null;
	}

	public void setConfigParams(ConfigParams params){
		useFitnessSharing = params.useFitnessSharing();
		sigma_sharing = params.getFitnessSharingSigma();
		useFitnessSharingMultiSigma = params.useFitnessSharingMultiSigma();
	}

	public Individual get(int index){
		if(index < 0){
			TheOptimizers.println("ERROR: Index smaller than 0 ("+index+").");
			return null;
		}
		if(index >= myIndividuals.length){
			TheOptimizers.println("ERROR: Index greater than array size of "+myIndividuals.length+" ("+index+").");
			return null;
		}
		return myIndividuals[index];
	}

	public void set(int index, Individual individual){
		myIndividuals[index] = individual;
		if(individual.getFitness() > maxFitness){
			maxFitness = individual.getFitness();
			maxIndividual = individual;
			TheOptimizers.println("Found new max fitness: "+individual.getFitness());
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
		TheOptimizers.println("Reevaluating max fitness (before: "+maxFitness+")");
		maxFitness = -1;
		maxIndividual = null;
		for(int i=0;i<myIndividuals.length;i++){
			if(myIndividuals[i].getFitness() > maxFitness){
				maxFitness = myIndividuals[i].getFitness();
				maxIndividual = myIndividuals[i];
			}
		}
		TheOptimizers.println("New max fitness: "+maxFitness);
	}

	public Individual getMaxIndividual(){
		return maxIndividual;
	}

	public void increaseAge(){
		for(Individual i: myIndividuals){
			i.increaseAge();
		}
	}

	public Individual getMinIndividual(){
		Individual minInd = myIndividuals[0];
		for(Individual i: myIndividuals)
			if(i.getFitness() < minInd.getFitness())
				minInd = i;
		return minInd;
	}

	public double[] getMeanPosition(){
		double[] mean_pos = new double[myIndividuals[0].getGenotype().length];
		double[] genes;
		for(Individual i: myIndividuals){
			genes = i.getGenotype();
			for(int k=0;k<genes.length;k++)
				mean_pos[k] += genes[k] / myIndividuals.length;
		}
		return mean_pos;
	}

	public double getMeanDistance(double[] mean_pos){
		double mean_dist = 0.0;
		double loc_dist;
		double[] genes;
		for(Individual i: myIndividuals){
			mean_dist += i.getDistance(mean_pos);
		}
		return mean_dist;
	}

	public double getOverallMeanMultiSigma(){
		double[] mean_sigma = getMeanMultiSigma();
		double mean = 0.0;
		for(double sig: mean_sigma)
			mean += sig;
		mean /= mean_sigma.length;
		return mean;
	}

	public double[] getMeanMultiSigma(){
		boolean foundMultiSigma = false;
		for(GeneTypes type: myIndividuals[0].getRepresentation().gene_types){
			foundMultiSigma = foundMultiSigma || type == GeneTypes.MULTI_SIGMA;
		}
		if(!foundMultiSigma)
			return new double[0];
		double[] mean_sigmas = new double[myIndividuals[0].getAdditionalParams(GeneTypes.MULTI_SIGMA).length];
		for(int i=0;i<mean_sigmas.length;i++)
			mean_sigmas[i] = 0;
		for(Individual i: myIndividuals){
			double[] multisigma = i.getAdditionalParams(GeneTypes.MULTI_SIGMA);
			for(int k=0;k<mean_sigmas.length;k++)
				mean_sigmas[k] += multisigma[k] / myIndividuals.length;
		}
		return mean_sigmas;
	}

	public double getMeanFitnessFactor(){
		double mean_fitness_factor = 0.0;
		for(Individual i: myIndividuals)
			mean_fitness_factor += i.getFitnessFactor();
		return mean_fitness_factor / myIndividuals.length;
	}

	public double getMeanFitness(){
		double mean_fitness = 0.0;
		for(Individual i: myIndividuals)
			mean_fitness += i.getFitness();
		return mean_fitness / myIndividuals.length;
	}

	private void setFitnessFactorSharing(){
		for(Individual individual: myIndividuals){
			individual.setFitnessFactor(1.0 / getDistanceSumForIndividual(individual));
		}
	}

	private double getDistanceSumForIndividual(Individual individual){
		double sum, dist;
		sum = 0.0;
		for(Individual neighbor: myIndividuals){
			if(useFitnessSharingMultiSigma){
				dist = individual.getDistance(neighbor, individual.getAdditionalParams(GeneTypes.MULTI_SIGMA));
				sum += dist < 1 ? (1 - dist) : 0;
			}
			else {
				dist = individual.getDistance(neighbor);
				sum += dist < sigma_sharing ? (1 - dist / sigma_sharing) : 0;
			}
		}
		return sum;
	}

	private double getDistanceSumForIndividual(ArrayList<Individual> sub_population, Individual individual){
		double sum, dist;
		sum = 0.0;
		for(Individual neighbor: sub_population){
			if(useFitnessSharingMultiSigma){
				dist = individual.getDistance(neighbor, individual.getAdditionalParams(GeneTypes.MULTI_SIGMA));
				sum += dist < 1 ? (1 - dist) : 0;
			}
			else {
				dist = individual.getDistance(neighbor);
				sum += dist < sigma_sharing ? (1 - dist / sigma_sharing) : 0;
			}
		}
		return sum;
	}

	public void resetFitnessFactors(){
		for(Individual i: myIndividuals)
			i.setFitnessFactor(1.0);
	}

	public void prepareCycle(){
		if(useFitnessSharing){
			setFitnessFactorSharing();
		}
	}

	public void endCycle(){
		resetFitnessFactors();
	}

	public void interactWithNewChildren(ArrayList<Individual> children) {
		if(useFitnessSharing) {
			for (Individual child : children) {
				child.setFitnessFactor(1.0 / (getDistanceSumForIndividual(children, child) + getDistanceSumForIndividual(child)));
			}
			for (Individual parent: myIndividuals) {
				parent.setFitnessFactor(1.0 / (1.0 / parent.getFitnessFactor() + getDistanceSumForIndividual(children, parent)));
			}
		}
	}

}
