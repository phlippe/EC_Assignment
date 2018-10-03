package individuals;

import algorithm.TheOptimizers;
import configuration.ConfigParams;
import configuration.ConfigurableObject;
import initialization.GenoInitializer;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public class Population implements ConfigurableObject
{

	private Individual[] myIndividuals;
	private double maxFitness;
	private Individual maxIndividual;
	private int population_age;

	private boolean useFitnessSharing = false;
	private double sigma_sharing = -1;
	private double fitnessSharingAlpha, fitnessSharingBeta, fitnessSharingBetaInit;
	private double fitnessSharingBetaStep, fitnessSharingBetaOffsetSteps, fitnessSharingBetaMaxSteps;
	private boolean fitnessSharingBetaExponential;
	private boolean useFitnessSharingMultiSigma = false;
	private double[][] distance_matrix;
	private long[] individual_ids;

	public Population(int size){
		myIndividuals = new Individual[size];
		maxFitness = -1;
		maxIndividual = null;
		population_age = 0;
		distance_matrix = new double[size][size];
		individual_ids = new long[size];
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
		fitnessSharingBetaMaxSteps = params.getFitnessSharingBetaMaxSteps();
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
		for(int i=0;i<individual_ids.length;i++)
			individual_ids[i] = -1;
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
		if(population_age > fitnessSharingBetaOffsetSteps &&
				(fitnessSharingBetaMaxSteps == -1 || population_age <= fitnessSharingBetaMaxSteps)) {
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
		return mean_dist / myIndividuals.length;
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
		Individual individual;
		for(int i=0;i<myIndividuals.length;i++){
			individual = myIndividuals[i];
			distance_sum = getDistanceSumForIndividual(individual, i);
			fitness_factor = calcFitnessSharing(individual, distance_sum);
			individual.setFitnessFactor(fitness_factor, distance_sum);
		}
	}

	private double getDistanceSumForIndividual(Individual individual, int index){
		double sum, dist;
		sum = 0.0;
		Individual neighbor;
		boolean isOldID = index >= 0 && (individual_ids[index] == individual.getID());
		for(int neighbor_index=0;neighbor_index<myIndividuals.length;neighbor_index++){
			neighbor = myIndividuals[neighbor_index];
			if(isOldID && individual_ids[neighbor_index] == neighbor.getID()){
				dist = distance_matrix[index][neighbor_index];
			}
			else {
				if (useFitnessSharingMultiSigma) {
					dist = individual.getDistance(neighbor, individual.getAdditionalParams(GeneTypes.MULTI_SIGMA));
				} else {
					dist = individual.getDistance(neighbor);
				}
			}
			if (useFitnessSharingMultiSigma) {
				sum += calcFitnessDistance(dist, 1);
			} else {
				sum += calcFitnessDistance(dist, sigma_sharing);
			}
			if(index >= 0){
				distance_matrix[index][neighbor_index] = dist;
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
			for(int i=0;i<individual_ids.length;i++)
				individual_ids[i] = myIndividuals[i].getID();
		}
	}

	public void endCycle(){
		resetFitnessFactors();
	}

	public void interactWithNewChildren(ArrayList<Individual> children) {
		if(useFitnessSharing) {
			double sum_distances, fitness_factor;
			for (Individual child : children) {
				sum_distances = getDistanceSumForIndividual(children, child) + getDistanceSumForIndividual(child, -1);
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

	public static void main(String args[]){
		GenoRepresentation repr = new BoundRepresentation(2, new int[0], new GeneTypes[0],-5, 5);
		Individual i = new Individual(repr);
		i.setFitness(2.0);
		double[] genes_i = {0.0, 1.0};
		i.setGenes(genes_i);
		Individual i2 = new Individual(repr);
		i2.setFitness(2.0);
		double[] genes_i2 = {0.0, 1.0};
		i2.setGenes(genes_i2);
		Population population = new Population(2);
		population.set(0, i);
		population.set(1, i2);

		ConfigParams params = new ConfigParams(1, 1, 1);
		params.setUseFitnessSharing(true);
		params.setFitnessSharingBeta(1);
		params.setFitnessSharingAlpha(1);
		params.setFitnessSharingSigma(4.0);
		population.setConfigParams(params);

		population.prepareCycle();
		System.out.println("Individual 1: " + i.getFitnessFactor() + ", " + i.getFitness() + ", " + i.getDistanceSum());
		System.out.println("Individual 2: " + i2.getFitnessFactor() + ", " + i2.getFitness() + ", " + i2.getDistanceSum());
	}

	public double getMeanAge() {
		double mean_age = 0.0;
		for(Individual i: myIndividuals)
			mean_age += i.getAge();
		mean_age /= myIndividuals.length;
		return mean_age;
	}

	@Override
	public String getDescription() {
		String s = "";
		s += "Population size: " + myIndividuals.length + "\n";
		s += "Use Fitness Sharing: " + useFitnessSharing + "\n";
		if(useFitnessSharing){
			s += "Use self-adapted multi sigmas for fitness sharing: " + useFitnessSharingMultiSigma + "\n";
			if(!useFitnessSharingMultiSigma)
				s += "Shared sigma: " + sigma_sharing + "\n";
			s += "Alpha: " + fitnessSharingAlpha + "\n";
			s += "Beta initial: " + fitnessSharingBeta + "\n";
			s += "Beta offset steps: " + fitnessSharingBetaOffsetSteps + "\n";
			s += "Beta step size: " + fitnessSharingBetaOffsetSteps + " " + (fitnessSharingBetaExponential ? "(exponential)":"(linear)") + "\n";
			s += "Beta max steps: " + fitnessSharingBetaMaxSteps + "\n";
		}
		return s;
	}
}
