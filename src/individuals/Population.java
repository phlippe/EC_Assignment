package individuals;

import algorithm.player59;
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

	private NichingTechnique nichingTechnique = NichingTechnique.FITNESS_SHARING;
	private boolean useNichingTechnique = false;
	private double sigma_sharing = -1;
	private double sigma_sharing_init = -1;
	private double fitnessSharingAlpha, fitnessSharingBeta, fitnessSharingBetaInit;
	private double fitnessSharingStepSize, fitnessSharingOffsetSteps, fitnessSharingMaxSteps;
	private boolean fitnessSharingStepsExponential;
	private boolean fitnessSharingAdaptSigma;
	private boolean useFitnessSharingMultiSigma = false;
	private double[][] distance_matrix;
	private long[] individual_ids;
	private double mean_distance;
	private double[] mean_position;

	private double pushToLinePower;
	private boolean pushToLineFitnessSharing;
	private double pushToLineStartVal;
	private double pushToLineEndCycle;
	private double pushToLineGradientFactor;

	public Population(int size){
		myIndividuals = new Individual[size];
		maxFitness = -1;
		maxIndividual = null;
		population_age = 0;
		distance_matrix = new double[size][size];
		individual_ids = new long[size];
	}

	public void setConfigParams(ConfigParams params){
		nichingTechnique = params.getNichingTechnique();
		useNichingTechnique = params.useNichingTechnique();
		sigma_sharing = params.getFitnessSharingSigma();
		sigma_sharing_init = params.getFitnessSharingSigma();
		useFitnessSharingMultiSigma = params.useFitnessSharingMultiSigma();
		fitnessSharingAlpha = params.getFitnessSharingAlpha();
		fitnessSharingBeta = params.getFitnessSharingBeta();
		fitnessSharingStepSize = params.getFitnessSharingAdaptiveStepSize();
		fitnessSharingStepsExponential = params.isFitnessSharingStepsExponential();
		fitnessSharingOffsetSteps = params.getFitnessSharingOffsetSteps();
		fitnessSharingMaxSteps = params.getFitnessSharingMaxSteps();
		fitnessSharingBetaInit = params.getFitnessSharingBeta();
		fitnessSharingAdaptSigma = params.isFitnessSharingAdaptSigma();
		pushToLinePower = params.getPushToLinePower();
		pushToLineFitnessSharing = params.isPushToLineFitnessSharing();
		pushToLineStartVal = params.getPushToLineStartVal();
		pushToLineEndCycle = params.getPushToLineEndCycle();
		pushToLineGradientFactor = params.getPushToLineGradientFactor();
	}

	public Individual get(int index){
		if(index < 0){
			player59.println("ERROR: Index smaller than 0 ("+index+").");
			return null;
		}
		if(index >= myIndividuals.length){
			player59.println("ERROR: Index greater than array size of "+myIndividuals.length+" ("+index+").");
			return null;
		}
		return myIndividuals[index];
	}

	public void set(int index, Individual individual){
		myIndividuals[index] = individual;
		if(individual.getPureFitness() > maxFitness){
			maxFitness = individual.getPureFitness();
			maxIndividual = individual;
			player59.println("Found new max fitness: "+individual.getPureFitness());
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
		sigma_sharing = sigma_sharing_init;
		for(int i=0;i<individual_ids.length;i++)
			individual_ids[i] = -1;
	}

	public void reevaluateMaxFitness(){
		player59.println("Reevaluating max fitness (before: "+maxFitness+")");
		maxFitness = -1;
		maxIndividual = null;
		for(int i=0;i<myIndividuals.length;i++){
			if(myIndividuals[i].getPureFitness() > maxFitness){
				maxFitness = myIndividuals[i].getPureFitness();
				maxIndividual = myIndividuals[i];
			}
		}
		player59.println("New max fitness: "+maxFitness);
	}

	public double getMaxFitness(){
		return maxFitness;
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
		if(population_age > fitnessSharingOffsetSteps &&
				(fitnessSharingMaxSteps == -1 || population_age <= fitnessSharingMaxSteps)) {
			if (fitnessSharingStepsExponential) {
				if(fitnessSharingAdaptSigma) {
					sigma_sharing *= fitnessSharingStepSize;
				}
				else{
					fitnessSharingBeta *= fitnessSharingStepSize;
				}
			} else {
				if(fitnessSharingAdaptSigma) {
					sigma_sharing += fitnessSharingStepSize;
				}
				else{
					fitnessSharingBeta += fitnessSharingStepSize;
				}
			}
		}
		else{
			if(population_age > fitnessSharingMaxSteps) {
			    if(fitnessSharingAdaptSigma)
                    sigma_sharing = 0.0;
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

	public double getMeanSharedFitness(){
		double mean_fitness = 0.0;
		for(Individual i: myIndividuals)
			mean_fitness += i.getFitness();
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
			else{
				if(neighbor_index < index){
					dist = distance_matrix[neighbor_index][index];
				}
				else {
					if (useFitnessSharingMultiSigma) {
						dist = individual.getDistance(neighbor, individual.getAdditionalParams(GeneTypes.MULTI_SIGMA));
					} else {
						dist = individual.getDistance(neighbor);
					}
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
        if(nichingTechnique == NichingTechnique.EXPLICIT_DIVERSITY_CONTROL)
            return distance;
	    if(distance == 0){
			return 1;
		}
		else {
			if (sigma == 0 || distance > sigma) {
				return 0.0;
			} else {
				if (fitnessSharingAlpha > 10)
					return 1;
				else {
					if (fitnessSharingAlpha == 1)
						return 1 - distance / sigma;
					else
						return 1 - Math.pow((distance / sigma), fitnessSharingAlpha);
				}
			}
		}
	}

	private double calcFitnessSharing(Individual individual, double sum_dist){
		double fitness_factor = 1.0;
		switch(nichingTechnique){
			case FITNESS_SHARING:
				if(fitnessSharingBeta == 1 || individual.getPureFitness() < 0){
					fitness_factor = 1.0 / sum_dist;
				}
				else{
					fitness_factor = (1.0 / sum_dist) * Math.pow(Math.max(0, individual.getPureFitness()), fitnessSharingBeta - 1);
				}
				break;
			case RELATIVE:
				fitness_factor = (1.0 - Math.pow(sum_dist / myIndividuals.length, fitnessSharingBeta));
				break;
			case LOG_SCALE:
				fitness_factor = (1.0 - Math.pow(sum_dist / myIndividuals.length, fitnessSharingBeta)) / individual.getPureFitness() *
						Math.exp(Math.log10(individual.getPureFitness()+1e-5));
				break;
			case SQRT:
				fitness_factor = Math.pow((1.0 - sum_dist / myIndividuals.length), 1.0/fitnessSharingBeta) / Math.pow(individual.getPureFitness(), 0.75);
				break;
			case PUSH_TO_LINE:
				double my_dist = - sigma_sharing * (sum_dist - myIndividuals.length) / myIndividuals.length;
				// double desired_mean_distance = 1.0 / (0.0001 * population_age + 0.2) - 1;
				double desired_mean_distance = getDesiredMeanDistance();
				if(mean_distance < desired_mean_distance){
					fitness_factor = (Math.pow(desired_mean_distance / mean_distance, pushToLinePower) * my_dist / individual.getPureFitness());
					if(pushToLineFitnessSharing){
						fitness_factor += Math.pow((1.0 - sum_dist / myIndividuals.length), 1.0/fitnessSharingBeta);
					}
					else{
						fitness_factor += 1;
					}
				}
				break;
            case EXPLICIT_DIVERSITY_CONTROL:
                double my_dist2 = sum_dist / myIndividuals.length;
				//double my_dist2 = individual.getDistance(mean_position);
				//System.out.println(my_dist2);
				// double my_dist2 = - sigma_sharing * (sum_dist - myIndividuals.length) / myIndividuals.length;
			    double desired_mean_distance2 = getDesiredMeanDistance();
				if(mean_distance < desired_mean_distance2){
					double fit_p = Math.pow(mean_distance / desired_mean_distance2,1);
					fitness_factor = fit_p +
							(1 - fit_p) * my_dist2 / individual.getPureFitness();
				}

		}
		return fitness_factor;
	}

	public double getDesiredMeanDistance(){
		double beta = 1.0 / (pushToLineStartVal / pushToLineGradientFactor + 1);
		double alpha = (1 - beta) / pushToLineEndCycle;
		PushLineType pushLineType = PushLineType.INVERS;
		double desired_mean_distance = 0;
		switch(pushLineType){
			case INVERS:
				desired_mean_distance = (1.0 / (alpha * population_age + beta) - 1) * pushToLineGradientFactor;
				break;
			case LINEAR:
				desired_mean_distance = - beta/alpha * population_age + beta;
				break;
			case SQUARED:
				desired_mean_distance = - beta * 1.0 / (alpha * alpha) * population_age * population_age + beta;
				break;
		}
		return (desired_mean_distance > 0 ? desired_mean_distance : 0);
	}

	public double getDesiredMeanFactor() {
		double desired_mean_distance = getDesiredMeanDistance();
		if (desired_mean_distance > mean_distance) {
			switch (nichingTechnique){
				case PUSH_TO_LINE:
					return Math.pow(getDesiredMeanDistance() / mean_distance, pushToLinePower);
				case EXPLICIT_DIVERSITY_CONTROL:
					return (1 - mean_distance / desired_mean_distance);
				default:
					return 1;
			}
		}
		else
			return 1;
	}

	private void resetFitnessFactors(){
		for(Individual i: myIndividuals)
			i.setFitnessFactor(1.0);
		maxIndividual.setFitnessFactor(1.0);
	}

	public void prepareCycle(){
		if(useNichingTechnique &&
				(nichingTechnique == NichingTechnique.EXPLICIT_DIVERSITY_CONTROL || sigma_sharing > 0.0)) {
            mean_position = getMeanPosition();
            mean_distance = getMeanDistance(mean_position);
            if(nichingTechnique != NichingTechnique.EXPLICIT_DIVERSITY_CONTROL || mean_distance < getDesiredMeanDistance()) {
                setFitnessFactorSharing();
                for (int i = 0; i < individual_ids.length; i++)
                    individual_ids[i] = myIndividuals[i].getID();
            }
        }
	}

	public void endCycle(){
		resetFitnessFactors();
	}

	public void interactWithNewChildren(ArrayList<Individual> children) {
		if(useNichingTechnique &&
				(nichingTechnique == NichingTechnique.EXPLICIT_DIVERSITY_CONTROL || sigma_sharing > 0.0)) {
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
		params.setUseNichingTechnique(true);
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
		s += "Use Fitness Sharing: " + useNichingTechnique + "\n";
		if(useNichingTechnique){
			s += "Type of fitness sharing: " + nichingTechnique + "\n";
			s += "Use self-adapted multi sigmas for fitness sharing: " + useFitnessSharingMultiSigma + "\n";
			if(!useFitnessSharingMultiSigma)
				s += "Shared sigma: " + sigma_sharing + "\n";
			s += "Alpha: " + fitnessSharingAlpha + "\n";
			s += "Beta initial: " + fitnessSharingBetaInit + "\n";
			s += "Beta offset steps: " + fitnessSharingOffsetSteps + "\n";
			s += "Beta step size: " + fitnessSharingStepSize + " " + (fitnessSharingStepsExponential ? "(exponential)":"(linear)") + "\n";
			s += "Beta max steps: " + fitnessSharingMaxSteps + "\n";
			s += "-- Push to line --\n";
			s += "Start value: " + pushToLineStartVal + "\n";
			s += "End cycle: " + pushToLineEndCycle + "\n";
			s += "Gradient factor: " + pushToLineGradientFactor + "\n";
			s += "Power: " + pushToLinePower + "\n";
			s += "Use Fitness sharing: " + pushToLineFitnessSharing + "\n";
		}
		return s;
	}
}
