package individuals;

/**
 * Created by phlippe on 08.09.18.
 */
public class BoundRepresentation extends GenoRepresentation
{

	private double[] lower_boundary, upper_boundary;

	public BoundRepresentation(int number_genes, int[] number_additional_params, GeneTypes[] gene_types){
		super(number_genes, number_additional_params, gene_types);
		upper_boundary = new double[number_genes];
		lower_boundary = new double[number_genes];
		setUpperBoundary(+Double.MAX_VALUE);
		setLowerBoundary(-Double.MAX_VALUE);
	}

	public BoundRepresentation(int number_genes, int[] number_additional_params, GeneTypes[] gene_types,
							   double lower_boundary, double upper_boundary){
		this(number_genes, number_additional_params, gene_types);
		setUpperBoundary(upper_boundary);
		setLowerBoundary(lower_boundary);
	}

	public void setLowerBoundary(double limit_val){
		for(int i=0;i<lower_boundary.length;i++){
			lower_boundary[i] = limit_val;
		}
	}

	public void setUpperBoundary(double limit_val){
		for(int i=0;i<upper_boundary.length;i++){
			upper_boundary[i] = limit_val;
		}
	}

	@Override
	public double[] convertGenoToPheno(double[] genes)
	{
		double[] phenotypes = new double[genes.length];
		for(int i=0;i<genes.length;i++){
			phenotypes[i] = genes[i];
			phenotypes[i] = phenotypes[i] > upper_boundary[i] ? upper_boundary[i] : phenotypes[i];
			phenotypes[i] = phenotypes[i] < lower_boundary[i] ? lower_boundary[i] : phenotypes[i];
		}
		return phenotypes;
	}

	@Override
	public String getAdditionalDescription()
	{
		String s = "";
		s += "Conversion genotype to phenotype: limited by boundaries. The following limits are set:\n";
		for(int i=0;i<upper_boundary.length;i++){
			s += "\tGene "+i+": [" + lower_boundary[i] + "," + upper_boundary[i] + "]\n";
		}
		return s;
	}
}
