package mutation;

import algorithm.TheOptimizers;
import individuals.BoundRepresentation;
import individuals.GeneTypes;
import individuals.Individual;
import initialization.GenoInitializer;
import initialization.RandomGenoInitializer;

import java.util.ArrayList;

/**
 * Created by phlippe on 24.09.18.
 */
public class EvolveRotationMutation extends Mutation
{

	public EvolveRotationMutation(){

	}

	@Override
	void applyMutation(double[] genes, Individual individual)
	{
//		double[][] covariance_matrix = determineCovarianceMatrix(individual);
//		double[][] cholesky_matrix = Cholesky.cholesky(covariance_matrix);
//		double[][] rnd_vec = new double[genes.length][1];
//		System.out.print("Random vector: [");
//		for(int i=0;i<genes.length;i++){
//			rnd_vec[i][0] = player59.rnd_.nextGaussian();
//			System.out.print(rnd_vec[i][0]+", ");
//		}
//		System.out.println("]");
//		double[][] rnd_choices = matrix_multiplication(cholesky_matrix, rnd_vec);
//		System.out.print("New random vector: [");
//		for(int i=0;i<genes.length;i++){
//			genes[i] += rnd_choices[i][0];
//			System.out.print(rnd_choices[i][0]+", ");
//		}
//		System.out.println("]");
//		printMatrix(covariance_matrix);
//		printMatrix(cholesky_matrix);
		double[] alphas = individual.getAdditionalParams(GeneTypes.ROTATION_ALPHA);
		double[] multi_sigmas = individual.getAdditionalParams(GeneTypes.MULTI_SIGMA);
		double[][] rot_matrix = createIdentityMatrix(genes.length);
		int alpha_index = 0;
		for(int i=0;i<genes.length;i++){
			for(int j=i+1;j<genes.length;j++){
				rot_matrix = matrix_multiplication(rot_matrix, createRotationMatrix(alphas[alpha_index], i, j, genes.length));
				alpha_index++;
			}
		}
		double[][] rnd_vec = new double[genes.length][1];
		System.out.print("Random vector: [");
		for(int i=0;i<genes.length;i++){
			rnd_vec[i][0] = TheOptimizers.rnd_.nextGaussian() * multi_sigmas[i];
			System.out.print(rnd_vec[i][0]+", ");
		}
		System.out.println("]");
		double[][] rot_rnd_vec = matrix_multiplication(rot_matrix, rnd_vec);
		for(int i=0;i<genes.length;i++){
			genes[i] += rot_rnd_vec[i][0];
		}
	}

	static void printMatrix(double[][] matrix){
		System.out.println("Matrix: ");
		for(int i=0;i<matrix.length;i++)
		{
			System.out.print("[");
			for(int j=0;j<matrix[i].length;j++){
				if(j>0)
					System.out.print(", ");
				System.out.print(matrix[i][j]);
			}
			System.out.println("]");
		}
	}

	private double[][] determineCovarianceMatrix(Individual individual){
		int number_genes = individual.getGenotype().length;
		double[] multi_sigmas = individual.getAdditionalParams(GeneTypes.MULTI_SIGMA);
		double[] alphas = individual.getAdditionalParams(GeneTypes.ROTATION_ALPHA);
		double[][] covariance_matrix = new double[number_genes][number_genes];
		int[] base_loc_alpha = new int[number_genes];
		int loc_alpha;
		double sign;
		for(int i=0;i<base_loc_alpha.length;i++){
			if(i==0)
				base_loc_alpha[i] = 0;
			else
				base_loc_alpha[i] = base_loc_alpha[i-1] + (number_genes - i);
		}
		for(int i=0;i<number_genes;i++){
			for(int j=0;j<number_genes;j++){
				if(i==j)
					covariance_matrix[i][i] = multi_sigmas[i] * multi_sigmas[i];
				else{
					if(i < j){
						loc_alpha = base_loc_alpha[i] + (j - i) - 1;
						sign = 1;
					}
					else{
						loc_alpha = base_loc_alpha[j] + (i - j) - 1;
						sign = -1;
					}
					covariance_matrix[i][j] = 0.5 * (multi_sigmas[i] * multi_sigmas[i] - multi_sigmas[j] * multi_sigmas[j]) * Math.tan(2 * sign * alphas[loc_alpha]);
				}
			}
		}
		return covariance_matrix;
	}

	private double[][] matrix_multiplication(double[][] A, double[][] B){
		System.out.println("A: "+A.length+","+A[0].length+" -> B: "+B.length+","+B[0].length);
		double[][] matrix_result = new double[A.length][B[0].length];
		for(int matrix_row=0;matrix_row<matrix_result.length;matrix_row++){
			for(int matrix_col=0;matrix_col<matrix_result[matrix_row].length;matrix_col++){
				matrix_result[matrix_row][matrix_col] = 0;
				for(int element_index=0;element_index<B.length;element_index++){
					matrix_result[matrix_row][matrix_col] += A[matrix_row][element_index] * B[element_index][matrix_col];
				}
			}
		}
		return matrix_result;
	}

	private double[][] createIdentityMatrix(int elements){
		double[][] id_matrix = new double[elements][elements];
		for(int i=0;i<elements;i++){
			for(int j=0;j<elements;j++){
				if(i == j) id_matrix[i][j] = 1;
				else id_matrix[i][j] = 0;
			}
		}
		return id_matrix;
	}

	private double[][] createRotationMatrix(double angle, int index1, int index2, int no_elements){
		double[][] rot_matrix = createIdentityMatrix(no_elements);
		rot_matrix[index1][index1] = Math.cos(angle);
		rot_matrix[index1][index2] = Math.sin(angle);
		rot_matrix[index2][index1] = -Math.sin(angle);
		rot_matrix[index2][index2] = Math.cos(angle);
		return rot_matrix;
	}

	@Override
	public String getMutationDescription()
	{
		return null;
	}

	public static void main(String args[]){
		TheOptimizers player = new TheOptimizers();
		player.setSeed(1);

		EvolveRotationMutation ev = new EvolveRotationMutation();
		double[][] A = new double[2][2];
		A[0][0] = 1;
		A[0][1] = 2;
		A[1][0] = 3;
		A[1][1] = 4;
		double[][] B = new double[2][2];
		B[0][0] = 2;
		B[0][1] = 0;
		B[1][0] = 1;
		B[1][1] = 2;
		double[][] C = ev.matrix_multiplication(B, A);
		for(int i=0;i<C.length;i++){
			System.out.print("[");
			for(int j=0;j<C[i].length;j++){
				System.out.print(C[i][j]+",");
			}
			System.out.println("]");
		}

		int[] add_params = {10, 45};
		GeneTypes[] add_gene_types = {GeneTypes.MULTI_SIGMA, GeneTypes.ROTATION_ALPHA};
		BoundRepresentation repr = new BoundRepresentation(10, add_params, add_gene_types, -5, 5);
		Individual individual = new Individual(repr);
		ArrayList<GenoInitializer> add_params_init = new ArrayList<>();
		add_params_init.add(new RandomGenoInitializer(0.1, 0.1));
		add_params_init.add(new RandomGenoInitializer(0.0, 0.0));
		individual.initialize(new RandomGenoInitializer(0.0, 0.0), add_params_init);
		individual.getAdditionalParams(GeneTypes.MULTI_SIGMA)[0] = 0.2;
		individual.getAdditionalParams(GeneTypes.ROTATION_ALPHA)[0] = 1.0;

		ev.applyMutation(individual.getGenotype(), individual);
	}
}

// Source: https://introcs.cs.princeton.edu/java/95linear/Cholesky.java.html
/*class Cholesky{
	private static final double EPSILON = 1e-10;

	// is symmetric
	public static boolean isSymmetric(double[][] A) {
		int N = A.length;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < i; j++) {
				if (A[i][j] != A[j][i]) return false;
			}
		}
		return true;
	}

	// is symmetric
	public static boolean isSquare(double[][] A) {
		int N = A.length;
		for (int i = 0; i < N; i++) {
			if (A[i].length != N) return false;
		}
		return true;
	}


	// return Cholesky factor L of psd matrix A = L L^T
	public static double[][] cholesky(double[][] A) {
		if (!isSquare(A)) {
			throw new RuntimeException("Matrix is not square");
		}
		if (!isSymmetric(A)) {
			throw new RuntimeException("Matrix is not symmetric");
		}

		int N  = A.length;
		double[][] L = new double[N][N];

		for (int i = 0; i < N; i++)  {
			for (int j = 0; j <= i; j++) {
				double sum = 0.0;
				for (int k = 0; k < j; k++) {
					sum += L[i][k] * L[j][k];
					if(true || sum == Double.NaN){
						System.out.println("L["+i+"]["+k+"] = "+L[i][k]+", "+"L["+j+"]["+k+"] = "+L[j][k]+" -> sum = "+sum);
					}
				}
				if (i == j) System.out.println(A[i][i] + " - " + sum);
				if (i == j) L[i][i] = Math.sqrt(A[i][i] - sum);
				else        L[i][j] = 1.0 / L[j][j] * (A[i][j] - sum);
			}
			if (L[i][i] <= 0) {
				throw new RuntimeException("Matrix not positive definite");
			}
			EvolveRotationMutation.printMatrix(L);
			System.out.println(L[i][i]);
		}
		return L;
	}
}*/
