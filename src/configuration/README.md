#Module "configuration"

The configuration module defines the parts of the evolutionary algorithm, including:
* Recombination operator
* Mutation operators
* Parent selection
* Survivor selection
* Population size
* Number of recombinations per cycle
* Number of parents used for a single recombination (also referred to as parent arity)

Note that the number of new children per cycle is the multiplication of number of recombinations and parent arity 
(i.e. 10 recombinations per cycle and 2 parents per recombination results in 20 new children).

To add a new configuration, create a new class that extends the base class "Configuration":

```java
class NewConfig extends Configuration{

    public NewConfig(){ 
    	// If you have parameters that one might change, put them here
    }
    
    // Override the functions creating the parts of the algorithm. For example
    @Override
    protected Recombination createRecombination()
    {
        Recombination recombination = new RandomRecombination();
        return recombination;
    }
    
    // Continue with other functions like Mutation, parent selection, ...


}
```

Please do not change the class ExampleConfig.java, create a new one! This prevents clashes when pulling.