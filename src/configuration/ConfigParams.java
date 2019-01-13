package configuration;

import individuals.NichingTechnique;
import recombination.RecombinationType;
import selection.ParentSelectionStochasticType;
import selection.ParentSelectionType;
import selection.RankingSelection;
import selection.SurvivorSelectionType;

public class ConfigParams {

    private int populationSize;
    private int numberRecombinations;
    private int parentArity;

    private NichingTechnique nichingTechnique = NichingTechnique.FITNESS_SHARING;
    private boolean useNichingTechnique = false;
    private double fitnessSharingSigma = 0.1;
    private boolean useFitnessSharingMultiSigma = false;
    private double fitnessSharingAlpha = 1;
    private double fitnessSharingBeta = 1;
    private double fitnessSharingAdaptiveStepSize = 1;
    private double fitnessSharingOffsetSteps = 0;
    private double fitnessSharingMaxSteps = -1;
    private boolean fitnessSharingStepsExponential = true;
    private boolean fitnessSharingAdaptSigma = true;

    private double pushToLinePower = 1.0;
    private boolean pushToLineFitnessSharing = false;
    private double pushToLineStartVal = 4.0;
    private double pushToLineEndCycle = 8000;
    private double pushToLineGradientFactor = 1.0;

    private ParentSelectionType parentSelectionType = ParentSelectionType.TOURNAMENT_SELECTION;
    private ParentSelectionStochasticType parentSelectionStochasticType = ParentSelectionStochasticType.UNIVERSAL;
    private int parentTournamentSize = 25;
    private int parentSigmaScalingS = 2;
    private double parentRankingS = 2;
    private RankingSelection.RankingType parentRankingType = RankingSelection.RankingType.LINEAR;

    private SurvivorSelectionType survivorSelectionType = SurvivorSelectionType.FITNESS_BASED;
    private int survivorTournamentSize = 5;

    private double mutationGaussianSigma = 0.5;
    private double mutationMultiSigmaInit = 0.5;
    private double mutationMultiSigmaFactor = 1;
    private double mutationResetProbability = 0.0;

    private double mutationResetHardValueInit = 0.5;
    private boolean mutationResetHardValue = false;
    private boolean mutationResetExponential = true;
    private double mutationResetLowFac = 1;
    private double mutationResetUppFac = 1000;

    private RecombinationType recombinationType = RecombinationType.BLEND_RANDOM_CROSSOVER;
    private double recombinationBlendRandomSigma = 0.0;
    private double recombinationBlendAlpha = 0.5;
    private double recombinationSBCEta = 1.0;
    private double recombinationWACAlpha = 0.5;

    private String name = "";


    public ConfigParams(int population_size, int number_recombinations, int parent_arity){
        this.populationSize = population_size;
        this.numberRecombinations = number_recombinations;
        this.parentArity = parent_arity;
    }


    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getNumberRecombinations() {
        return numberRecombinations;
    }

    public void setNumberRecombinations(int numberRecombinations) {
        this.numberRecombinations = numberRecombinations;
    }

    public int getParentArity() {
        return parentArity;
    }

    public void setParentArity(int parentArity) {
        this.parentArity = parentArity;
    }

    public ParentSelectionType getParentSelectionType() {
        return parentSelectionType;
    }

    public void setParentSelectionType(ParentSelectionType parentSelectionType) {
        this.parentSelectionType = parentSelectionType;
    }

    public ParentSelectionStochasticType getParentSelectionStochasticType() {
        return parentSelectionStochasticType;
    }

    public void setParentSelectionStochasticType(ParentSelectionStochasticType parentSelectionStochasticType) {
        this.parentSelectionStochasticType = parentSelectionStochasticType;
    }

    public int getParentTournamentSize() {
        return parentTournamentSize;
    }

    public void setParentTournamentSize(int parentTournamentSize) {
        this.parentTournamentSize = parentTournamentSize;
    }

    public int getParentSigmaScalingS() {
        return parentSigmaScalingS;
    }

    public void setParentSigmaScalingS(int parentSigmaScalingS) {
        this.parentSigmaScalingS = parentSigmaScalingS;
    }

    public SurvivorSelectionType getSurvivorSelectionType() {
        return survivorSelectionType;
    }

    public void setSurvivorSelectionType(SurvivorSelectionType survivorSelectionType) {
        this.survivorSelectionType = survivorSelectionType;
    }

    public int getSurvivorTournamentSize() {
        return survivorTournamentSize;
    }

    public void setSurvivorTournamentSize(int survivorTournamentSize) {
        this.survivorTournamentSize = survivorTournamentSize;
    }

    public double getMutationGaussianSigma() {
        return mutationGaussianSigma;
    }

    public void setMutationGaussianSigma(double mutationGaussianSigma) {
        this.mutationGaussianSigma = mutationGaussianSigma;
    }

    public double getMutationMultiSigmaInit() {
        return mutationMultiSigmaInit;
    }

    public void setMutationMultiSigmaInit(double mutationMultiSigmaInit) {
        this.mutationMultiSigmaInit = mutationMultiSigmaInit;
    }

    public double getMutationMultiSigmaFactor() {
        return mutationMultiSigmaFactor;
    }

    public void setMutationMultiSigmaFactor(double mutationMultiSigmaFactor) {
        this.mutationMultiSigmaFactor = mutationMultiSigmaFactor;
    }

    public double getMutationResetProbability() {
        return mutationResetProbability;
    }

    public void setMutationResetProbability(double mutationResetProbability) {
        this.mutationResetProbability = mutationResetProbability;
    }

    public double getMutationResetHardValueInit() {
        return mutationResetHardValueInit;
    }

    public void setMutationResetHardValueInit(double mutationResetHardValueInit) {
        this.mutationResetHardValueInit = mutationResetHardValueInit;
    }

    public boolean isMutationResetHardValue() {
        return mutationResetHardValue;
    }

    public void setMutationResetHardValue(boolean mutationResetHardValue) {
        this.mutationResetHardValue = mutationResetHardValue;
    }

    public boolean isMutationResetExponential() {
        return mutationResetExponential;
    }

    public void setMutationResetExponential(boolean mutationResetExponential) {
        this.mutationResetExponential = mutationResetExponential;
    }

    public double getMutationResetLowFac() {
        return mutationResetLowFac;
    }

    public void setMutationResetLowFac(double mutationResetLowFac) {
        this.mutationResetLowFac = mutationResetLowFac;
    }

    public double getMutationResetUppFac() {
        return mutationResetUppFac;
    }

    public void setMutationResetUppFac(double mutationResetUppFac) {
        this.mutationResetUppFac = mutationResetUppFac;
    }

    public RecombinationType getRecombinationType() {
        return recombinationType;
    }

    public void setRecombinationType(RecombinationType recombinationType) {
        this.recombinationType = recombinationType;
    }

    public double getRecombinationBlendRandomSigma() {
        return recombinationBlendRandomSigma;
    }

    public void setRecombinationBlendRandomSigma(double recombinationBlendRandomSigma) {
        this.recombinationBlendRandomSigma = recombinationBlendRandomSigma;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getParentRankingS() {
        return parentRankingS;
    }

    public void setParentRankingS(double parentRankingS) {
        this.parentRankingS = parentRankingS;
    }

    public RankingSelection.RankingType getParentRankingType() {
        return parentRankingType;
    }

    public void setParentRankingType(RankingSelection.RankingType parentRankingType) {
        this.parentRankingType = parentRankingType;
    }

    public double getRecombinationBlendAlpha() {
        return recombinationBlendAlpha;
    }

    public void setRecombinationBlendAlpha(double recombinationBlendAlpha) {
        this.recombinationBlendAlpha = recombinationBlendAlpha;
    }

    public double getRecombinationSBCEta() {
        return recombinationSBCEta;
    }

    public void setRecombinationSBCEta(double recombinationSBCEta) {
        this.recombinationSBCEta = recombinationSBCEta;
    }

    public double getRecombinationWACAlpha() {
        return recombinationWACAlpha;
    }

    public void setRecombinationWACAlpha(double recombinationWACAlpha) {
        this.recombinationWACAlpha = recombinationWACAlpha;
    }

    public boolean useNichingTechnique() {
        return useNichingTechnique;
    }

    public void setUseNichingTechnique(boolean useNichingTechnique) {
        this.useNichingTechnique = useNichingTechnique;
    }

    public boolean useFitnessSharingMultiSigma() {
        return useFitnessSharingMultiSigma;
    }

    public void setUseFitnessSharingMultiSigma(boolean useFitnessSharingMultiSigma) {
        this.useFitnessSharingMultiSigma = useFitnessSharingMultiSigma;
    }

    public double getFitnessSharingSigma() {
        return fitnessSharingSigma;
    }

    public void setFitnessSharingSigma(double fitnessSharingSigma) {
        this.fitnessSharingSigma = fitnessSharingSigma;
    }

    public double getFitnessSharingAlpha() {
        return fitnessSharingAlpha;
    }

    public void setFitnessSharingAlpha(double fitnessSharingAlpha) {
        this.fitnessSharingAlpha = fitnessSharingAlpha;
    }

    public double getFitnessSharingBeta() {
        return fitnessSharingBeta;
    }

    public void setFitnessSharingBeta(double fitnessSharingBeta) {
        this.fitnessSharingBeta = fitnessSharingBeta;
    }

    public double getFitnessSharingAdaptiveStepSize() {
        return fitnessSharingAdaptiveStepSize;
    }

    public void setFitnessSharingAdaptiveStepSize(double fitnessSharingAdaptiveStepSize) {
        this.fitnessSharingAdaptiveStepSize = fitnessSharingAdaptiveStepSize;
    }

    public boolean isFitnessSharingStepsExponential() {
        return fitnessSharingStepsExponential;
    }

    public void setFitnessSharingStepsExponential(boolean fitnessSharingStepsExponential) {
        this.fitnessSharingStepsExponential = fitnessSharingStepsExponential;
    }

    public double getFitnessSharingOffsetSteps() {
        return fitnessSharingOffsetSteps;
    }

    public void setFitnessSharingOffsetSteps(double fitnessSharingOffsetSteps) {
        this.fitnessSharingOffsetSteps = fitnessSharingOffsetSteps;
    }

    public double getFitnessSharingMaxSteps() {
        return fitnessSharingMaxSteps;
    }

    public void setFitnessSharingMaxSteps(double fitnessSharingMaxSteps) {
        this.fitnessSharingMaxSteps = fitnessSharingMaxSteps;
    }

    public NichingTechnique getNichingTechnique()
    {
        return nichingTechnique;
    }

    public void setNichingTechnique(NichingTechnique nichingTechnique)
    {
        this.nichingTechnique = nichingTechnique;
    }

    public double getPushToLinePower() {
        return pushToLinePower;
    }

    public void setPushToLinePower(double pushToLinePower) {
        this.pushToLinePower = pushToLinePower;
    }

    public boolean isPushToLineFitnessSharing() {
        return pushToLineFitnessSharing;
    }

    public void setPushToLineFitnessSharing(boolean pushToLineFitnessSharing) {
        this.pushToLineFitnessSharing = pushToLineFitnessSharing;
    }

    public double getPushToLineStartVal() {
        return pushToLineStartVal;
    }

    public void setPushToLineStartVal(double pushToLineStartVal) {
        this.pushToLineStartVal = pushToLineStartVal;
    }

    public double getPushToLineEndCycle() {
        return pushToLineEndCycle;
    }

    public void setPushToLineEndCycle(double pushToLineEndCycle) {
        this.pushToLineEndCycle = pushToLineEndCycle;
    }

    public boolean isFitnessSharingAdaptSigma() {
        return fitnessSharingAdaptSigma;
    }

    public void setFitnessSharingAdaptSigma(boolean fitnessSharingAdaptSigma) {
        this.fitnessSharingAdaptSigma = fitnessSharingAdaptSigma;
    }

    public double getPushToLineGradientFactor() {
        return pushToLineGradientFactor;
    }

    public void setPushToLineGradientFactor(double pushToLineGradientFactor) {
        this.pushToLineGradientFactor = pushToLineGradientFactor;
    }
}
