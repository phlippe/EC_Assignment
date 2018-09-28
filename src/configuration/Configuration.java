package configuration;


import individuals.GenoRepresentation;
import initialization.GenoInitializer;
import mutation.Mutation;
import selection.ParentSelection;
import recombination.Recombination;
import selection.SurvivorSelection;

import java.util.ArrayList;

/**
 * Created by phlippe on 08.09.18.
 */
public abstract class Configuration
{

	private static final int LINE_SIZE = 100;

	private GenoRepresentation myRepresentation;
	private ArrayList<Mutation> myMutations;
	private Recombination myRecombination;
	private ParentSelection myParentSelection;
	private SurvivorSelection mySurvivorSelection;
	private GenoInitializer genoInitializer;
	private ArrayList<GenoInitializer> addParamsInitializer;
	private String name;

	public Configuration(){
		name = "";
	}

	protected void init(){
		myRepresentation = createRepresentation();
		myMutations = createMutationOperators();
		myRecombination = createRecombination();
		myParentSelection = createParentSelection();
		mySurvivorSelection = createSurvivorSelection();
		genoInitializer = createGenoInitializer();
		addParamsInitializer = createAddParamsInitializer();
	}

	protected abstract GenoRepresentation createRepresentation();

	protected abstract ArrayList<Mutation> createMutationOperators();

	protected abstract Recombination createRecombination();

	protected abstract ParentSelection createParentSelection();

	protected abstract SurvivorSelection createSurvivorSelection();

	protected abstract GenoInitializer createGenoInitializer();

	protected abstract ArrayList<GenoInitializer> createAddParamsInitializer();

	public abstract int getPopulationSize();

	public abstract int getNumberOfRecombinations();

	public abstract int getParentArity();

	public GenoRepresentation getRepresentation(){
		return myRepresentation;
	}

	public ArrayList<Mutation> getMutationOperators(){
		return myMutations;
	}

	public ParentSelection getParentSelection(){
		return myParentSelection;
	}

	public Recombination getRecombination(){
		return myRecombination;
	}

	public SurvivorSelection getSurvivorSelection(){
		return mySurvivorSelection;
	}

	public GenoInitializer getGenoInitializer(){
		return genoInitializer;
	}

	public ArrayList<GenoInitializer> getAddParamsInitializer(){
		return addParamsInitializer;
	}

	public String toString(){
		String s = createHeading("Configuration", '=');
		s += "Configuration class: " + this.getClass().getName()+"\n";
		s += createHeading("Population", '-');
		s += "Population size: " + getPopulationSize() + "\n";
		s += "Parent arity: " + getParentArity() + "\n";
		s += "Number of recombinations: " + getNumberOfRecombinations() + "\n";
		// s += "Number of new children per cycle: "+ getNumberOfRecombinations() * getParentArity() + "\n";
		s += createHeading("Representation", '-');
		s += myRepresentation.getDescription();
		s += createHeading("Parent Selection", '-');
		s += myParentSelection.getDescription();
		s += createHeading("Recombination", '-');
		s += myRecombination.getDescription();
		s += createHeading("Mutation", '-');
		for(Mutation mum: myMutations){
			if(mum != myMutations.get(0)){
				s += "\n" + repeatChar('#',20) + "\n\n";
			}
			s += mum.getDescription();
		}
		s += createHeading("Survivor Selection", '-');
		s += mySurvivorSelection.getDescription();
		s += "\n" + repeatChar('=', LINE_SIZE);
		return s;
	}

	public static String createHeading(String heading, char c){
		int space_front = (int)Math.ceil(heading.length() / 2.0) + 1;
		int space_back = (int)Math.floor(heading.length() / 2.0) + 1;
		return "\n" + repeatChar(c,LINE_SIZE/2-space_front) +
				" " + heading + " " +
				repeatChar(c,LINE_SIZE/2-space_back) + "\n\n";
	}

	public static String repeatChar(char c, int amount){
		char[] c_array = new char[amount];
		for(int i=0;i<amount;i++){
			c_array[i] = c;
		}
		return new String(c_array);
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public abstract ConfigParams getParameters();

}
