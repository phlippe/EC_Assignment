package selection;

import configuration.ConfigurableObject;
import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;

/**
 * Created by phlippe on 06.09.18.
 */
public abstract class SurvivorSelection implements ConfigurableObject
{

	private ArrayList<FitnessIndexPair> pairList;

	public SurvivorSelection(){
		pairList = new ArrayList<>();
	}

	public ArrayList<Individual> selectSurvivors(Population population, ArrayList<Individual> individuals){
		ArrayList<Individual> deadIndividuals = new ArrayList<>();
		if(pairList.size() < population.size() + individuals.size()){
			for(int i=pairList.size();i<population.size() + individuals.size();i++){
				pairList.add(new FitnessIndexPair(-1, -1));
			}
		}

		prepareSelection(population, individuals);

		for(int i=0;i<population.size();i++){
			pairList.get(i).fitness = rateIndividual(population.get(i), false);
			pairList.get(i).index = i;
		}
		for(int i=population.size();i<population.size()+individuals.size();i++){
			pairList.get(i).fitness = rateIndividual(individuals.get(i - population.size()), true);
			pairList.get(i).index = i;
		}
		pairList.sort(FitnessIndexPair::compareTo);

		ArrayList<Integer> childToDelete = new ArrayList<>();
		ArrayList<Integer> freePositions = new ArrayList<>();
		for(int i=0;i<individuals.size();i++){
			if(pairList.get(i).index >= population.size()){
				childToDelete.add(pairList.get(i).index - population.size());
			}
			else{
				freePositions.add(pairList.get(i).index);
			}
		}
		childToDelete.sort(Integer::compareTo);
		for(int i=0;i<childToDelete.size();i++){
			deadIndividuals.add(individuals.get(childToDelete.get(i) - i));
			individuals.remove(childToDelete.get(i) - i);
		}
		for(int i=0;i<freePositions.size();i++){
			deadIndividuals.add(population.get(freePositions.get(i)));
			population.set(freePositions.get(i), individuals.get(i));
		}
		return deadIndividuals;
	}

	abstract void prepareSelection(Population population, ArrayList<Individual> children);

	abstract double rateIndividual(Individual individual, boolean isNewChild);

	public String getDescription(){
		String s = "";
		s += "Survivor selection class: " + this.getClass().getName() + "\n";
		s += "Selection process: individuals with lowest score are removed from population\n";
		s += "Selection criteria: " + getCriteriaDescription();
		s += "\n";
		return s;
	}

	public abstract String getCriteriaDescription();

	public class FitnessIndexPair{
		double fitness;
		int index;

		public FitnessIndexPair(double fitness, int index){
			this.fitness = fitness;
			this.index = index;
		}

		public int compareTo(FitnessIndexPair p2){
			if(fitness == p2.fitness){
				return 0;
			}
			if(fitness > p2.fitness){
				return 1;
			}
			return -1;
		}
	}
}
