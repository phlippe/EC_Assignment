# Diversity preservation in multimodal functions using explicit diversity control and adaptive distance based algorithm
Practical assignment of the course Evolutionary Computing at the VU Amsterdam.

__Group members__: Shantanu Chandra, Phillip Lippe, Mahsa Mojtahedi, Rick Halm

__Report__: _will be added soon_

## Setup
This project was developed in IntelliJ. To install this project on your local machine, please install IntelliJ and Java 1.8 or newer. 

In IntelliJ, you can clone this repository by: _File -> New -> Project from Version Control -> Git_. If the project does not open automatically, you can do that by _File -> 

Once cloned, make sure that you import all necessary libraries of the contest. This can be done under _File -> Project Structure -> Libraries_. Here, you can add the files by clicking on the "+" button in the upper left corner. To guarantee an unproblematic installation, add these three folder/files by using the "+" button:
* assignmentfiles_2017
* contest.jar
* javabbob

Finally, create a run configuration. Therefore, select _Run -> Edit configurations..._ and add by the "+" button in the upper left corner a new _Application_. Choose as main class _TestAlgo_ (package _algorithm_). No further program arguments are required. After giving the configuration a name, click _Apply_ to save the configuration. To execute the program, select _Run -> Run..._ and then your previously created configuration.

## Executing experiments from the report
To execute the experiments from the report, please open the file _algorithm/TestAlgo.java_. The main function should start similar to:
```java
public static void main(String args[]){
	// Define algorithm you want to run. Based on this decision, the parameters will be selected
	ReportVariants algoVariant = ReportVariants.EXPLICIT_DIVERSITY_CONTROL;
	...
}
  ```
In this line, you define the EA variant that should be executed. There are five possible settings:
* _EXPLICIT_DISTANCE_CONTROL_: Our proposed bi-objective function combining mean distance and fitness
* _FITNESS_SHARING_: Standard fitness sharing algorithm
* _FITNESS_SHARING_SIGMA_: Fitness sharing with adaptive sigma share
* _FITNESS_SHARING_BETA_: Fitness sharing with adaptive beta
* _STANDARD_: Standard EA without any niching technique

Change the value of ``algoVariant`` to the algorithm you want to test. Further experimental settings will be created correspondingly.

The next lines define the evaluation function:
```java
	// Define contest function on which the algorithm should be tested
	EvalType evalType = EvalType.KATSUURA;
	ContestEvaluation eval = createEval(evalType);
```
The value of ``evalType`` determines on which optimization function the EA should be tested. Depending on this value, a corresponding ``ContestEvaluation`` object is created. Note, that the evaluation classes were decompiled to integrate them in this project for easier testing. Also, the evaluation limit was removed, but checked while executing the algorithm (see function ``run()`` in _algorithm/player59.java_).

Setting these two values is sufficient to recreated the results of the report. The next section explains the rest of the main function.

### Further experimental settings

The following lines specify our setting for the EA:
```java
	// Create default parameter setting that is shared among all variants
	ConfigParams configParams = new ConfigParams(400, 80, 2);
	configParams.setParentTournamentSize(2);
	configParams.setSurvivorSelectionType(SurvivorSelectionType.ROUND_ROBIN_TOURNAMENT);
	configParams.setSurvivorTournamentSize(2);
	configParams.setMutationMultiSigmaInit(0.01);
	configParams.setMutationMultiSigmaFactor(0.8);
```

If ``algoVariant`` is not ``ReportVariants.STANDARD``, then additional parameters for the niching technique must be added:

```java
	// Create algorithm specific parameters
	if(algoVariant == ReportVariants.STANDARD){
		// Standard algorithm does not apply any fitness sharing or other niching technique
		configParams.setUseNichingTechnique(false);
	}
	else{
		// For all other algorithms than standard, use niching technique
		createNichingSpecificParameters(configParams, evalType, algoVariant);
	}
```
For details, have a look at the function ``createNichingSpecificParameters(...)``.

After defining the parameters, the EA instance can be created:

```java
	// Create EA instance
	StandardConfig config = new StandardConfig(configParams);
	EvolutionaryCycle eval_ea = new EvolutionaryCycle(config);
```
This instance will be used to execute the experiments. Additionally to the cycle, a tracer can be added:

```java
	// Tracer creates log files with information about the population, max fitness, ... (optional by the parameter "active")
	Tracer tracer = new Tracer(false, "fitness_sharing");
	eval_ea.addTracer(tracer);
```
This object records states during the run (like max fitness, mean distance, ...) for later analysis. If the first parameter of the constructor is set to true (``new Tracer(true, ...)``), then the recorded information will be saved in a folder with the name of the second parameter. Otherwise, the informtation will be deleted after the run.

Finally, we define how many experiments we want to execute. By default, 1000 experiments are executed using a random seed from 0 to 999:

```java
	// Execute "number_of_runs" tests with seeds from "start_seed" to "start_seed" + "number_of_runs" and print mean score
	swipeSeeds(eval_ea, eval, 1000, 0, true);
}
```

If you want to execute less experiments, change ``1000`` to your desired number. Note, that this will reduce the runtime but also reduce the accuracy/significance of your experiments.

## Questions
For questions, please contact phillip.lippe@googlemail.com
