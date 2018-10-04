package selection;

import algorithm.TheOptimizers;
import com.sun.org.apache.bcel.internal.generic.POP;
import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;

/**
 * Created by phlippe on 12.09.18.
 */
public class SurvivorRoundRobinTournamentSelection extends SurvivorSelection
{

	private int tournament_size;
	private ArrayList<TournamentContestant> contestants;
	private ArrayList<RandomObj<TournamentContestant>>[] tournament_pos;

	public SurvivorRoundRobinTournamentSelection(int tournament_size){
		this.tournament_size = tournament_size;
		contestants = new ArrayList<>();
		tournament_pos = new ArrayList[tournament_size];
		for(int i=0;i<tournament_size;i++){
			tournament_pos[i] = new ArrayList<>();
		}
	}

	@Override
	void prepareSelection(Population population, ArrayList<Individual> children)
	{
		int size_population = population.size();
		int size_children = children.size();
		for(TournamentContestant c: contestants){
			c.resetWins();
		}
		int contestants_size = contestants.size();
		for(int i=0;i<size_population + size_children - contestants_size;i++){
			TournamentContestant c = new TournamentContestant(-1, -1);
			contestants.add(c);
			for(ArrayList a: tournament_pos){
				a.add(new RandomObj<>(c));
			}
		}
		for(int i=0;i<size_population;i++){
			contestants.get(i).setId(population.get(i).getID());
			contestants.get(i).setScore(population.get(i).getFitness());
		}
		for(int i=0;i<children.size();i++){
			contestants.get(i + size_population).setId(children.get(i).getID());
			contestants.get(i + size_population).setScore(children.get(i).getFitness());
		}
		for(ArrayList<RandomObj<TournamentContestant>> a: tournament_pos)
			for(RandomObj<TournamentContestant> obj: a)
				obj.randomize_score();
		for(ArrayList<RandomObj<TournamentContestant>> a: tournament_pos)
			a.sort(RandomObj::compareTo);
		for(int cont_pos=0;cont_pos<size_population + size_children;cont_pos++){
			for(int c_index=0;c_index<tournament_pos.length;c_index++){
				for(int c_index_2=c_index+1;c_index_2<tournament_pos.length;c_index_2++){
					if(tournament_pos[c_index].get(cont_pos).obj.id == tournament_pos[c_index_2].get(cont_pos).obj.id){
						int switch_index = findSwitchPosition(tournament_pos[c_index_2].get(cont_pos).obj.id, c_index_2);
						if(switch_index == -1){
							TheOptimizers.println("ERROR (SurvivorRoundRobinTournamentSelecion): Switch index was -1");
						}
						RandomObj<TournamentContestant> tmp = tournament_pos[c_index_2].get(switch_index);
						tournament_pos[c_index_2].set(switch_index, tournament_pos[c_index_2].get(cont_pos));
						tournament_pos[c_index_2].set(cont_pos, tmp);
					}
				}
			}
		}

		//System.out.println("Contest");
		for(int cont_pos=0;cont_pos<size_population + size_children; cont_pos++){
			//System.out.print(cont_pos + ": ");
			double best_fitness = -Double.MAX_VALUE;
			int best_index = -1;
			for(int c_index=0;c_index<tournament_pos.length;c_index++){
				//System.out.print(tournament_pos[c_index].get(cont_pos).obj.id + " [" + tournament_pos[c_index].get(cont_pos).obj.score + "]" + " vs. ");
				if(tournament_pos[c_index].get(cont_pos).obj.score > best_fitness){
					best_fitness = tournament_pos[c_index].get(cont_pos).obj.score;
					best_index = c_index;
				}
			}
			//System.out.println(" -> Winner: " + tournament_pos[best_index].get(cont_pos).obj.id);
			tournament_pos[best_index].get(cont_pos).obj.increaseWins();
		}
	}

	private int findSwitchPosition(long id, int column){
		boolean changeable;
		for(int cont_exc_pos=0;cont_exc_pos<tournament_pos[column].size();cont_exc_pos++){
			changeable = true;
			for(int c_index=0;c_index<tournament_pos.length;c_index++){
				changeable = changeable && (tournament_pos[c_index].get(cont_exc_pos).obj.id != id);
			}
			if(changeable){
				return cont_exc_pos;
			}
		}
		return -1;
	}

	@Override
	double rateIndividual(Individual individual, boolean isNewChild)
	{
		for(TournamentContestant c: contestants){
			if(individual.getID() == c.id){
				return c.wins + 1e-5 * TheOptimizers.rnd_.nextDouble();
			}
		}
		return -1;
	}

	@Override
	public String getCriteriaDescription()
	{
		return null;
	}

	private class RandomObj<T>{

		T obj;
		double rnd_score;

		public RandomObj(T obj){
			this.obj = obj;
			rnd_score = TheOptimizers.rnd_.nextDouble();
		}

		private void randomize_score(){
			rnd_score = TheOptimizers.rnd_.nextDouble();
		}

		public int compareTo(RandomObj p2){
			if(rnd_score == p2.rnd_score){
				return 0;
			}
			if(rnd_score > p2.rnd_score){
				return 1;
			}
			return -1;
		}
	}

	private class TournamentContestant{

		private long id;
		private double score;
		private int wins;

		public TournamentContestant(long id, double score){
			this.id = id;
			this.score = score;
			wins = 0;
		}

		public void resetWins(){
			wins = 0;
		}

		public void setId(long id){
			this.id = id;
		}

		public void setScore(double score){
			this.score = score;
		}

		public void increaseWins(){
			wins++;
		}

		public int compareTo(TournamentContestant p2){
			if(wins == p2.wins){
				return 0;
			}
			if(wins > p2.wins){
				return 1;
			}
			return -1;
		}
	}

	public static void main(String args[]){
		TheOptimizers opt = new TheOptimizers();
		opt.setSeed(1);
		int population_size = 10;
		Population p = new Population(population_size);
		TheOptimizers.print("Population: ");
		for(int i=0;i<population_size;i++){
			Individual ind = new Individual();
			ind.setFitness(i); // TheOptimizers.rnd_.nextDouble()*10
			p.set(i, ind);
			TheOptimizers.print(ind.getFitness() + ", ");
		}
		TheOptimizers.println();
		ArrayList<Individual> children = new ArrayList<>();
		TheOptimizers.print("Children: ");
		for(int i=0;i<2;i++){
			Individual ind = new Individual();
			ind.setFitness(TheOptimizers.rnd_.nextDouble()*10);
			children.add(ind);
			TheOptimizers.print(ind.getFitness() + ", ");
		}
		SurvivorRoundRobinTournamentSelection s = new SurvivorRoundRobinTournamentSelection(5);
		s.prepareSelection(p, children);
		TheOptimizers.println("Fitness of population: ");
		for(int i=0;i<population_size;i++){
			double rate = s.rateIndividual(p.get(i), false);
			TheOptimizers.println(rate + ", ");
		}
	}
}
