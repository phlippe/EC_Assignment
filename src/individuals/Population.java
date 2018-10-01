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
	private int population_age;

	private boolean useFitnessSharing = false;
	private double sigma_sharing = -1;
	private double fitnessSharingAlpha, fitnessSharingBeta, fitnessSharingBetaInit;
	private double fitnessSharingBetaStep, fitnessSharingBetaOffsetSteps;
	private boolean fitnessSharingBetaExponential;
	private boolean useFitnessSharingMultiSigma = false;

	public Population(int size){
		myIndividuals = new Individual[size];
		maxFitness = -1;
		maxIndividual = null;
		population_age = 0;
	}

	public void setConfigParams(ConfigParams params){
		useFitnessSharing = params.useFitnessSharing();
		sigma_sharing = params.getFitnessSharingSigma();
		useFitnessSharingMultiSigma = params.useFitnessSharingMultiSigma();
		fitnessSharingAlpha = params.getFitnessSharingAlpha();
		fitnessSharingBeta = params.getFitnessSharingBeta();
		fitnessSharingBetaStep = params.getFitnessSharingBetaStep();
		fitnessSharingBetaExponential = params.isFitnessSharingBetaExponential();
		fitnessSharingBetaOffsetSteps = params.getFitnessSharingBetaOffsetSteps();
		fitnessSharingBetaInit = params.getFitnessSharingBeta();
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
		if(individual.getPureFitness() > maxFitness){
			maxFitness = individual.getPureFitness();
			maxIndividual = individual;
			TheOptimizers.println("Found new max fitness: "+individual.getPureFitness());
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
		population_age = 0;
		fitnessSharingBeta = fitnessSharingBetaInit;
	}

	public void reevaluateMaxFitness(){
		TheOptimizers.println("Reevaluating max fitness (before: "+maxFitness+")");
		maxFitness = -1;
		maxIndividual = null;
		for(int i=0;i<myIndividuals.length;i++){
			if(myIndividuals[i].getPureFitness() > maxFitness){
				maxFitness = myIndividuals[i].getPureFitness();
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
		population_age++;
		updateParams();
	}

	private void updateParams(){
		updateBeta();
	}

	private void updateBeta(){
		if(population_age > fitnessSharingBetaOffsetSteps) {
			if (fitnessSharingBetaExponential) {
				fitnessSharingBeta *= fitnessSharingBetaStep;
			} else {
				fitnessSharingBeta += fitnessSharingBetaStep;
			}
		}
	}

	public Individual getMinIndividual(){
		Individual minInd = myIndividuals[0];
		for(Individual i: myIndividuals)
			if(i.getPureFitness() < minInd.getPureFitness())
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
		for(Individual i: myIndividuals){
			mean_dist += i.getDistance(mean_pos);
		}
		return mean_dist;
	}

	public double getFitnessSharingMeanDistanceSum(){
		double mean_sum = 0.0;
		for(Individual i: myIndividuals)
			mean_sum += i.getDistanceSum();
		mean_sum /= myIndividuals.length;
		return mean_sum;
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
			mean_fitness += i.getPureFitness();
		return mean_fitness / myIndividuals.length;
	}

	private void setFitnessFactorSharing(){
		double distance_sum, fitness_factor;
		for(Individual individual: myIndividuals){
			distance_sum = getDistanceSumForIndividual(individual);
			fitness_factor = calcFitnessSharing(individual, distance_sum);
			individual.setFitnessFactor(fitness_factor, distance_sum);
		}
	}

	private double getDistanceSumForIndividual(Individual individual){
		double sum, dist;
		sum = 0.0;
		for(Individual neighbor: myIndividuals){
			if(useFitnessSharingMultiSigma){
				dist = individual.getDistance(neighbor, individual.getAdditionalParams(GeneTypes.MULTI_SIGMA));
				sum += calcFitnessDistance(dist, 1);
			}
			else {
				dist = individual.getDistance(neighbor);
				sum += calcFitnessDistance(dist, sigma_sharing);
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
				sum += calcFitnessDistance(dist, 1);
			}
			else {
				dist = individual.getDistance(neighbor);
				sum += calcFitnessDistance(dist, sigma_sharing);
			}
		}
		return sum;
	}

	private double calcFitnessDistance(double distance, double sigma){
		if(distance > sigma){
			return 0.0;
		}
		else{
			return 1 - Math.pow((distance / sigma), fitnessSharingAlpha);
		}
	}

	private double calcFitnessSharing(Individual individual, double sum_dist){
		if(fitnessSharingBeta == 1 || individual.getPureFitness() < 0){
			return 1.0 / sum_dist;
		}
		else{
			return (1.0 / sum_dist) * Math.pow(Math.max(0, individual.getPureFitness()), fitnessSharingBeta - 1);
		}
	}

	private void resetFitnessFactors(){
		for(Individual i: myIndividuals)
			i.setFitnessFactor(1.0);
		maxIndividual.setFitnessFactor(1.0);
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
			double sum_distances, fitness_factor;
			for (Individual child : children) {
				sum_distances = getDistanceSumForIndividual(children, child) + getDistanceSumForIndividual(child);
				fitness_factor = calcFitnessSharing(child, sum_distances);
				child.setFitnessFactor(fitness_factor, sum_distances);
			}
			for (Individual parent: myIndividuals) {
				sum_distances = parent.getDistanceSum() + getDistanceSumForIndividual(children, parent);
				fitness_factor = calcFitnessSharing(parent, sum_distances);
				parent.setFitnessFactor(fitness_factor, sum_distances);
			}
		}
	}

	public double getFitnessSharingBeta(){
		return fitnessSharingBeta;
	}

}
