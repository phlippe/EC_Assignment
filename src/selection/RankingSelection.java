package selection;

import individuals.Individual;
import individuals.Population;

import java.util.ArrayList;
import java.util.Comparator;

public class RankingSelection extends ParentSelection{

    public enum RankingType{
        LINEAR,
        EXPONENTIAL
    }

    private ArrayList<Individual> ranking;
    private Double s;
    private RankingType RankingSelectionType;

    public RankingSelection(ParentSelectionStochastic stochastic,double s, RankingType RankingSelectionType) {
        super(stochastic);
        this.s = s;
        this.RankingSelectionType = RankingSelectionType;
    }

    @Override
    void prepareSelection(Population population) {
        ranking=new ArrayList<>(population.size());
        for (int i=0; i<population.size(); i++) {
            ranking.add(i,population.get(i));
        }
        ranking.sort(new Comparator<Individual>() {
            @Override
            public int compare(Individual o1, Individual o2) {
                return -Double.compare((o1).getFitness(), (o2).getFitness());
            }
        });
    }

    @Override
    double getSelectionProbability(Individual individual) {
        int IndividualRank=-1;
        Double Pi=-1.0;
        for(int i=0; i<ranking.size(); i++){
            if(individual.getID()==(ranking.get(i)).getID()){
                IndividualRank=i;
            }
        }
        if (RankingSelectionType == RankingType.LINEAR){
            Pi=(2-s)/ranking.size()+(2*IndividualRank*(s-1))/(ranking.size()*(ranking.size()-1));
        }
        if (RankingSelectionType == RankingType.EXPONENTIAL){
            Double sum=0.0;
            for (int i=0 ;i<ranking.size();i++){
                sum=sum+(1-java.lang.Math.exp(-1.00*i));
            }
            Double c=sum;
            Pi=(1-java.lang.Math.exp(-1.00*IndividualRank))/c;
        }
        return Pi;
    }

    @Override
    public String getProbabilityDescription() {
        return "Ranking Selection";
    }


}