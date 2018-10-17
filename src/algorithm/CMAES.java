package algorithm;

import individuals.Population;

import java.util.ArrayList;
import java.util.Arrays;

public class CMAES implements EvolutionaryAlgorithm {

    private class LiteIndividual{
        double[] genes;
        double fitness;

        private void setGenes(double[] g){
            genes = g;
            for(int i=0;i<genes.length;i++){
                genes[i] = Math.max(Math.min(genes[i], 5), -5);
            }
        }

        public int compareTo(LiteIndividual p2) {
            if (fitness == p2.fitness) {
                return 0;
            }
            if (fitness > p2.fitness) {
                return -1;
            }
            return 1;
        }
    }

    private ArrayList<LiteIndividual> population;
    private LiteIndividual bestIndividual;
    private int numberOffsprings;
    private double[] xmean;
    private double sigma;
    private double[][] C;
    private double[][] B;
    private double[] D;
    private double[] p_sigma;
    private double[] p_c;

    private int mean_mu;
    private double[] mean_weights;
    private double c_sigma;
    private double c_c;
    private double c1;
    private double cmu;
    private double damps;
    private double mueff;

    private int lastEigenupdate;
    private int iteration = 0;
    private int diagonalIterations;
    private double chiN;
    private int N = 10;
    private double ccov =  -1;
    private double ccovsep = -1;

    public CMAES(){

    }

    @Override
    public void run_single_cycle() {
        ArrayList<LiteIndividual> offsprings = samplePopulation();
        offsprings.sort(LiteIndividual::compareTo);
        population = offsprings;
//        double[] new_mean = updateMean(offsprings);
//        updatePSigma();
//        updatePC();
//        updateC();
//        updateSigma();
        // calculate xmean and BDz
        double[] xold = Arrays.copyOf(xmean, xmean.length);
        double[] BDz = new double[N];
        double[] artmp = new double[N];
        double diff = 0.0;
        for (int i = 0; i < xmean.length; i++) {
            xmean[i] = 0;

            for (int j = 0; j < mean_mu; j++) {
                xmean[i] += mean_weights[j] * offsprings.get(j).genes[i];
            }
            // System.out.println("Mueff: " + mueff + ", xmean["+i+"]="+xmean[i]+", x_old["+i+"]="+xold[i]);
            diff += Math.abs((xmean[i] - xold[i]) / sigma);
            BDz[i] = Math.sqrt(mueff) * (xmean[i] - xold[i]) / sigma;
        }
        if(iteration % 10 == 0) {
            //System.out.println("Diff: " + diff);
            //for(int i=0;i<xmean.length;i++){
            //    System.out.print(xmean[i] + ", ");
            //}
            //System.out.println();
        }

        // cumulation for sigma (ps) using B*z
        // System.out.println("Diagonal iterations: " + diagonalIterations + ", iteration: " + iteration);
        if (diagonalIterations >= iteration) {
            // given B=I we have B*z = z = D^-1 BDz

            for (int i = 0; i < N; i++) {
                // System.out.println("P_sigma["+i+"]=" + p_sigma[i] + ", C_sigma = " + c_sigma + ", BDz["+i+"]="+BDz[i]+", D["+i+"]="+D[i]);
                p_sigma[i] = (1.0 - c_sigma) * p_sigma[i] + Math.sqrt(c_sigma * (2.0 - c_sigma)) * BDz[i] / D[i];
            }
        } else {
            for (int i = 0; i < N; i++) {
                double sum = 0.0;

                for (int j = 0; j < N; j++) {
                    sum += B[j][i] * BDz[j];
                }

                artmp[i] = sum / D[i];
            }

            for (int i = 0; i < N; i++) {
                double sum = 0.0;

                for (int j = 0; j < N; j++) {
                    sum += B[i][j] * artmp[j];
                }

                p_sigma[i] = (1.0 - c_sigma) * p_sigma[i] + Math.sqrt(c_sigma * (2.0 - c_sigma)) * sum;
            }
        }


        // calculate norm(ps)^2
        double psxps = 0;

        for (int i = 0; i < N; i++) {
            // System.out.println("P_sigma["+i+"]=" + p_sigma[i]);
            psxps += p_sigma[i] * p_sigma[i];
        }

        // cumulation for covariance matrix (pc) using B*D*z
        int hsig = 0;

        if (Math.sqrt(psxps) / Math.sqrt(1.0 - Math.pow(1.0 - c_sigma, 2.0 * iteration)) / chiN < 1.4 + 2.0 / (N+1)) {
            hsig = 1;
        }

        for (int i = 0; i < N; i++) {
            p_c[i] = (1.0 - c_c) * p_c[i] + hsig * Math.sqrt(c_c * (2.0 - c_c)) * BDz[i];
        }

        // update of C
        for (int i = 0; i < N; i++) {
            for (int j = (diagonalIterations >= iteration ? i : 0); j <= i; j++) {
                C[i][j] = (1.0 - (diagonalIterations >= iteration ? ccovsep : ccov)) * C[i][j] + ccov * (1.0 / mueff) * (p_c[i] * p_c[j] + (1 - hsig) * c_c * (2.0 - c_c) * C[i][j]);

                for (int k = 0; k < mean_mu; k++) {
                    C[i][j] += ccov * (1 - 1.0 / mueff) * mean_weights[k] * (offsprings.get(k).genes[i] - xold[i]) * (offsprings.get(k).genes[j] - xold[j]) / sigma / sigma;
                }
            }
        }

        // update of sigma
        // System.out.println("ChiN: " + chiN + ", PSXPS: " + psxps + ", C_sigma: " + c_sigma + ", Damps: " + damps);
        sigma *= Math.exp(((Math.sqrt(psxps) / chiN) - 1) * c_sigma / damps);
        // System.out.println("Max fitness: " + bestIndividual.fitness);
        // System.out.println("sigma: " + sigma);
        // if(iteration>100)
        //    System.exit(1);
    }

    private ArrayList<LiteIndividual> samplePopulation() {
        boolean feasible = true;
        // System.out.println("Start sampling " + sigma);
        if ((iteration - lastEigenupdate) > 1.0 / ccov / N / 5.0) {
            eigendecomposition();
        }

        if (true) {
            testAndCorrectNumerics();
        }

        ArrayList<LiteIndividual> offsprings = new ArrayList<>();

        // sample the distribution
        for (int i = 0; i < numberOffsprings; i++) {
            double[] genes = new double[N];
            if(0.0 > TheOptimizers.rnd_.nextDouble()) {
                genes = Arrays.copyOf(xmean, xmean.length);
            }
            else {

                if (diagonalIterations >= iteration) {
                    // loop until a feasible solution is generated
                    do {
                        feasible = true;

                        for (int j = 0; j < N; j++) {
                            double value = xmean[j] + sigma * D[j] * TheOptimizers.rnd_.nextGaussian();

                            if (value < -5 || value > 5) {
                                feasible = false;
                                break;
                            }

                            genes[j] = value;
                        }
                    } while (!feasible);
                } else {
                    double[] artmp = new double[N];

                    // loop until a feasible solution is generated
                    do {
                        feasible = true;

                        for (int j = 0; j < N; j++) {
                            artmp[j] = D[j] * TheOptimizers.rnd_.nextGaussian();
                        }

                        // add mutation (sigma * B * (D*z))
                        for (int j = 0; j < N; j++) {
                            double sum = 0.0;

                            for (int k = 0; k < N; k++) {
                                sum += B[j][k] * artmp[k];
                            }

                            double value = xmean[j] + sigma * sum;

                            if (value < -5 || value > 5) {
                                feasible = false;
                                break;
                            }

                            genes[j] = value;
                        }
                    } while (!feasible);
                }
            }

            LiteIndividual individual = new LiteIndividual();
            individual.setGenes(genes);
            individual.fitness = (double) TheOptimizers.evaluation_.evaluate(individual.genes);
            // System.out.println("Fitness: " + individual.fitness);
            if(individual.fitness > bestIndividual.fitness)
                bestIndividual = individual;
            offsprings.add(individual);
        }

        iteration++;
        // System.out.println("Iteration");
        return offsprings;
    }

    private  void testAndCorrectNumerics() {
        // flat fitness, test is function values are identical
        if (population.size() > 0) {

            if (population.get(0).fitness == population.get(Math.min(numberOffsprings-1, numberOffsprings/2 + 1) - 1).fitness) {
                System.err.println("flat fitness landscape, consider reformulation of fitness, step size increased");
                sigma *= Math.exp(0.2 + c_sigma/damps);
            }
        }

        // align (renormalize) scale C (and consequently sigma)
        double fac = 1.0;

        double max_D = -1;
        double min_D = Double.MAX_VALUE;
        for(int i=0;i<D.length;i++){
            if(D[i] > max_D) max_D = D[i];
            if(D[i] < min_D) min_D = D[i];
        }
        if (max_D < 1e-6) {
            fac = 1.0 / max_D;
        } else if (min_D > 1e4) {
            fac = 1.0 / min_D;
        }

        if (fac != 1.0) {
            // System.out.println("Correct sigma by " + fac + " (was " + sigma + ")");
            sigma /= fac;

            for (int i = 0; i < N; i++) {
                p_c[i] *= fac;
                D[i] *= fac;

                for (int j = 0; j <= i; j++) {
                    C[i][j] *= fac*fac;
                }
            }
        }
    }

//    private void updateSigma() {
//        // TODO: Implement this stuff
//    }
//
//    private void updateC(){
//        // TODO: Implement this stuff
//    }
//
//    private void updatePC(){
//        // TODO: Implement this stuff
//    }

//    private void updatePSigma(){
//        for(int i=0;i<p_sigma.length;i++){
//            p_sigma[i] = (1 - c_sigma) * p_sigma[i] + Math.sqrt(c_sigma * (2 - c_sigma) * mueff);
//        }
//    }

//    private double[] updateMean(ArrayList<LiteIndividual> offsprings){
//        double[] new_mean = new double[10];
//        for(int i=0;i<new_mean.length;i++){
//            new_mean[i] = 0;
//            for(int j=0;j<mean_mu;j++){
//                new_mean[i] += mean_weights[j] * offsprings.get(j).fitness;
//            }
//        }
//        return new_mean;
//    }
//
//    public double[] sampleNewOffspring(){
//        // TODO: Implement this stuff
//        return new double[10];
//    }

    @Override
    public int getEvalsPerCycle() {
        return numberOffsprings;
    }

    @Override
    public double[] getBestSolution() {
        return bestIndividual.genes;
    }

    @Override
    public double getBestFitness() {
        return bestIndividual.fitness;
    }

    @Override
    public Population getPopulation() {
        return null;
    }

    @Override
    public void logResults() {

    }

    @Override
    public String getLogString() {
        return "CMA-ES";
    }

    @Override
    public String getName() {
        return "CMA-ES";
    }

    @Override
    public void initialize() {
        bestIndividual = new LiteIndividual();
        bestIndividual.fitness = -1;

        p_c = new double[10];
        p_sigma = new double[10];
        xmean = new double[10];
        for(int i=0;i<p_c.length;i++){
            p_c[i] = 0.0;
            p_sigma[i] = 0.0;
            xmean[i] = Math.max(Math.min(TheOptimizers.rnd_.nextGaussian() * 2, 5), -5);
        }
        C = new double[10][10];
        D = new double[10];
        B = new double[10][10];
        for(int i=0;i<C.length;i++) {
            D[i] = 1;
            for (int j = 0; j < C[i].length; j++) {
                if (i == j) C[i][j] = 1;
                else C[i][j] = 0;
                if (i == j) B[i][j] = 1;
                else B[i][j] = 0;
            }
        }

//        sigma = 0.3;
//        numberOffsprings = 20;
//        mean_mu = numberOffsprings / 4;
//        mean_weights = new double[mean_mu];
//        double sum = 0;
//        for(int i=0;i<mean_weights.length;i++) {
//            mean_weights[i] = 1.0 / Math.pow(i + 1, 1);// Math.log(mean_mu + 1.0 / 2.0) - Math.log(i + 1);
//            sum += mean_weights[i];
//        }
//        double squared_sum = 0.0;
//        for(int i=0;i<mean_weights.length;i++){
//            mean_weights[i] /= sum;
//            squared_sum += mean_weights[i] * mean_weights[i];
//        }
//        mueff = sum*sum / squared_sum;
//        c_sigma = (mueff + 2) / (N + mueff + 5);
//        c_c = (4 + mueff / N) / (N + 4 + 2 * mueff / N);
//        c1 = 2 / (Math.pow((N + 1.3), 2) + mueff);
//        cmu = Math.min(1 - c1, 2 * (mueff - 2 + 1.0 / mueff) / (Math.pow(N + 2, 2) + mueff));
//        damps = (1 + 2 * Math.max(0, Math.sqrt((mueff - 1) / (N + 1)) - 1) + c_sigma) * 1;
//        chiN = Math.sqrt(N) * (1.0 - 1.0 / (4.0 * N) + 1.0 / (21.0 * N * N));
//
//        diagonalIterations = 0; // 150 * N / numberOffsprings;
//
//        ccov = 2.0 / (N + 1.41) / (N + 1.41) / mueff + (1 - (1.0 / mueff)) * Math.min(1, (2 * mueff - 1) / (mueff + (N + 2) * (N + 2)));
//        ccovsep = Math.min(1, ccov * (N + 1.5) / 3.0);

        sigma = 1.0;
        numberOffsprings = 6;
        mean_mu = 2;
        mean_weights = new double[mean_mu];
        double sum = 0;
        for(int i=0;i<mean_weights.length;i++) {
            mean_weights[i] = i == 0 ? 1 : 0.1; //Math.log(mean_mu + 1.0 / 2.0) - Math.log(i + 1);
            sum += mean_weights[i];
        }
        double squared_sum = 0.0;
        for(int i=0;i<mean_weights.length;i++){
            mean_weights[i] /= sum;
            squared_sum += mean_weights[i] * mean_weights[i];
        }
        mueff = sum*sum / squared_sum;
        c_sigma = (mueff + 2) / (N + mueff + 5);
        c_c = (4 + mueff / N) / (N + 4 + 2 * mueff / N);
        c1 = 2 / (Math.pow((N + 1.3), 2) + mueff);
        cmu = Math.min(1 - c1, 2 * (mueff - 2 + 1.0 / mueff) / (Math.pow(N + 2, 2) + mueff));
        damps = (1 + 2 * Math.max(0, Math.sqrt((mueff - 1) / (N + 1)) - 1) + c_sigma) * 1;
        chiN = Math.sqrt(N) * (1.0 - 1.0 / (4.0 * N) + 1.0 / (21.0 * N * N));

        diagonalIterations = 0; // 150 * N / numberOffsprings;

        ccov = 2.0 / (N + 1.41) / (N + 1.41) / mueff + (1 - (1.0 / mueff)) * Math.min(1, (2 * mueff - 1) / (mueff + (N + 2) * (N + 2)));
        ccovsep = Math.min(1, ccov * (N + 1.5) / 3.0);

        //System.out.println("Parameters: C_sigma: " + c_sigma + ", C_c: " + c_c + ", C1: " + c1 + ", Cmu: " + cmu + ", damps: " + damps + ", chiN: " + chiN + ", CCOV: " + ccov);
        ccov *= 2;
        damps = 2;//1.01;
        c_sigma *= 2;
        c_c *= 0.5;
        c1 *= 1;
        cmu *= 1;
        chiN *= 1.0;
        ccov *= 1;

        double[] best_solution = {-0.8920155060065972, 3.9912011293055953, 0.17116203517301654, -3.8007786382676176, -0.46873959482921596, -2.0887781161601224, 1.3839860817313057, -0.7344032305389674, 1.1456271489990422, -0.3040078707034723};
        for (int i = 0; i < N; i++) {
            double offset = sigma * D[i];
            double range = (5 - (-5) - 2*sigma*D[i]);

            if (offset > 0.4 * (5 - (-5))) {
                offset = 0.4 * (5 - (-5));
                range = 0.2 * (5 - (-5));
            }

            xmean[i] = -5 + offset + TheOptimizers.rnd_.nextDouble() * range;
            //xmean[i] = best_solution[i] + TheOptimizers.rnd_.nextGaussian() * 0.1;
            xmean[i] = 0.0;
        }

        population = new ArrayList<>();
    }

    @Override
    public void addTracer(Tracer tracer) {

    }

    @Override
    public void writeTraceFiles() {

    }

    @Override
    public String getExtraDescription() {
        return "";
    }

    /**
     * Performs eigenvalue decomposition to update B and diagD.
     */
    private  void eigendecomposition() {
        int N = 10;

        lastEigenupdate = iteration;

        if (diagonalIterations >= iteration) {
            for (int i = 0; i < N; i++) {
                D[i] = Math.sqrt(C[i][i]);
            }
        } else {
            // set B <- C
            for (int i = 0; i < N; i++) {
                for (int j = 0; j <= i; j++) {
                    B[i][j] = B[j][i] = C[i][j];
                }
            }

            // eigenvalue decomposition
            double[] offdiag = new double[N];
            tred2(N, B, D, offdiag);
            tql2(N, D, offdiag, B);

            if (true) {
                checkEigenSystem(N, C, D, B);
            }

            // assign diagD to eigenvalue square roots
            for (int i = 0; i < N; i++) {
                if (D[i] < 0) { // numerical problem?
                    System.err.println("an eigenvalue has become negative");
                    D[i] = 0;
                }

                D[i] = Math.sqrt(D[i]);
            }
        }
    }

    public static  void tred2(int n, double[][] V, double[] d, double[] e) {
        for (int j = 0; j < n; j++) {
            d[j] = V[n-1][j];
        }

        // Householder reduction to tridiagonal form.
        for (int i = n-1; i > 0; i--) {

            // Scale to avoid under/overflow.
            double scale = 0.0;
            double h = 0.0;
            for (int k = 0; k < i; k++) {
                scale = scale + Math.abs(d[k]);
            }
            if (scale == 0.0) {
                e[i] = d[i-1];
                for (int j = 0; j < i; j++) {
                    d[j] = V[i-1][j];
                    V[i][j] = 0.0;
                    V[j][i] = 0.0;
                }
            } else {
                // Generate Householder vector.
                for (int k = 0; k < i; k++) {
                    d[k] /= scale;
                    h += d[k] * d[k];
                }
                double f = d[i-1];
                double g = Math.sqrt(h);
                if (f > 0) {
                    g = -g;
                }
                e[i] = scale * g;
                h = h - f * g;
                d[i-1] = f - g;
                for (int j = 0; j < i; j++) {
                    e[j] = 0.0;
                }

                // Apply similarity transformation to remaining columns.
                for (int j = 0; j < i; j++) {
                    f = d[j];
                    V[j][i] = f;
                    g = e[j] + V[j][j] * f;
                    for (int k = j+1; k <= i-1; k++) {
                        g += V[k][j] * d[k];
                        e[k] += V[k][j] * f;
                    }
                    e[j] = g;
                }
                f = 0.0;
                for (int j = 0; j < i; j++) {
                    e[j] /= h;
                    f += e[j] * d[j];
                }
                double hh = f / (h + h);
                for (int j = 0; j < i; j++) {
                    e[j] -= hh * d[j];
                }
                for (int j = 0; j < i; j++) {
                    f = d[j];
                    g = e[j];
                    for (int k = j; k <= i-1; k++) {
                        V[k][j] -= (f * e[k] + g * d[k]);
                    }
                    d[j] = V[i-1][j];
                    V[i][j] = 0.0;
                }
            }
            d[i] = h;
        }

        // Accumulate transformations.
        for (int i = 0; i < n-1; i++) {
            V[n-1][i] = V[i][i];
            V[i][i] = 1.0;
            double h = d[i+1];
            if (h != 0.0) {
                for (int k = 0; k <= i; k++) {
                    d[k] = V[k][i+1] / h;
                }
                for (int j = 0; j <= i; j++) {
                    double g = 0.0;
                    for (int k = 0; k <= i; k++) {
                        g += V[k][i+1] * V[k][j];
                    }
                    for (int k = 0; k <= i; k++) {
                        V[k][j] -= g * d[k];
                    }
                }
            }
            for (int k = 0; k <= i; k++) {
                V[k][i+1] = 0.0;
            }
        }
        for (int j = 0; j < n; j++) {
            d[j] = V[n-1][j];
            V[n-1][j] = 0.0;
        }
        V[n-1][n-1] = 1.0;
        e[0] = 0.0;
    }

    public static  void tql2(int n, double[] d, double[] e, double[][] V) {
        for (int i = 1; i < n; i++) {
            e[i-1] = e[i];
        }
        e[n-1] = 0.0;

        double f = 0.0;
        double tst1 = 0.0;
        double eps = Math.pow(2.0,-52.0);
        for (int l = 0; l < n; l++) {
            // Find small subdiagonal element
            tst1 = Math.max(tst1,Math.abs(d[l]) + Math.abs(e[l]));
            int m = l;
            while (m < n) {
                if (Math.abs(e[m]) <= eps*tst1) {
                    break;
                }
                m++;
            }

            // If m == l, d[l] is an eigenvalue,
            // otherwise, iterate.
            if (m > l) {
                int iter = 0;
                do {
                    iter = iter + 1;  // (Could check iteration count here.)

                    // Compute implicit shift
                    double g = d[l];
                    double p = (d[l+1] - g) / (2.0 * e[l]);
                    double r = hypot(p,1.0);
                    if (p < 0) {
                        r = -r;
                    }
                    d[l] = e[l] / (p + r);
                    d[l+1] = e[l] * (p + r);
                    double dl1 = d[l+1];
                    double h = g - d[l];
                    for (int i = l+2; i < n; i++) {
                        d[i] -= h;
                    }
                    f = f + h;

                    // Implicit QL transformation.
                    p = d[m];
                    double c = 1.0;
                    double c2 = c;
                    double c3 = c;
                    double el1 = e[l+1];
                    double s = 0.0;
                    double s2 = 0.0;
                    for (int i = m-1; i >= l; i--) {
                        c3 = c2;
                        c2 = c;
                        s2 = s;
                        g = c * e[i];
                        h = c * p;
                        r = hypot(p,e[i]);
                        e[i+1] = s * r;
                        s = e[i] / r;
                        c = p / r;
                        p = c * d[i] - s * g;
                        d[i+1] = h + s * (c * g + s * d[i]);

                        // Accumulate transformation.
                        for (int k = 0; k < n; k++) {
                            h = V[k][i+1];
                            V[k][i+1] = s * V[k][i] + c * h;
                            V[k][i] = c * V[k][i] - s * h;
                        }
                    }
                    p = -s * s2 * c3 * el1 * e[l] / dl1;
                    e[l] = s * p;
                    d[l] = c * p;

                    // Check for convergence.
                } while (Math.abs(e[l]) > eps*tst1);
            }
            d[l] = d[l] + f;
            e[l] = 0.0;
        }

        // Sort eigenvalues and corresponding vectors.
        for (int i = 0; i < n-1; i++) {
            int k = i;
            double p = d[i];
            for (int j = i+1; j < n; j++) {
                if (d[j] < p) { // NH find smallest k>i
                    k = j;
                    p = d[j];
                }
            }
            if (k != i) {
                d[k] = d[i]; // swap k and i
                d[i] = p;
                for (int j = 0; j < n; j++) {
                    p = V[j][i];
                    V[j][i] = V[j][k];
                    V[j][k] = p;
                }
            }
        }
    }

    private static  int checkEigenSystem(int N, double[][] C, double[] diag, double[][] Q) {
        /* compute Q diag Q^T and Q Q^T to check */
        int i;
        int j;
        int k;
        int res = 0;
        double cc;
        double dd;

        for (i=0; i < N; ++i) {
            for (j=0; j < N; ++j) {
                for (cc=0.,dd=0., k=0; k < N; ++k) {
                    cc += diag[k] * Q[i][k] * Q[j][k];
                    dd += Q[i][k] * Q[j][k];
                }
                /* check here, is the normalization the right one? */
                if (Math.abs(cc - C[i>j?i:j][i>j?j:i])/Math.sqrt(C[i][i]*C[j][j]) > 1e-10
                        && Math.abs(cc - C[i>j?i:j][i>j?j:i]) > 1e-9) { /* quite large */
                    System.err.println("imprecise result detected " + i + " " + j + " " + cc + " " + C[i>j?i:j][i>j?j:i] + " " + (cc-C[i>j?i:j][i>j?j:i]));
                    ++res;
                }
                if (Math.abs(dd - (i==j?1:0)) > 1e-10) {
                    System.err.println("imprecise result detected (Q not orthog.) " + i + " " + j + " " + dd);
                    ++res;
                }
            }
        }
        return res;
    }

    /**
     * Compute sqrt(a^2 + b^2) without under/overflow.
     */
    private static  double hypot(double a, double b) {
        double r  = 0;
        if (Math.abs(a) > Math.abs(b)) {
            r = b/a;
            r = Math.abs(a)*Math.sqrt(1+r*r);
        } else if (b != 0) {
            r = a/b;
            r = Math.abs(b)*Math.sqrt(1+r*r);
        }
        return r;
    }
}
