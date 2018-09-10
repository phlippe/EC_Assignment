package evaluation;

import java.util.Properties;
import org.vu.contest.ContestEvaluation;

public class SphereEvaluation implements ContestEvaluation {
	private static final int EVALS_LIMIT_ = 10000;
	private static final double BASE_ = 11.5356D;
	private static final double ftarget_ = 0.0D;
	private double best_ = 0.0D;
	private int evaluations_ = 0;
	private String multimodal_ = "false";
	private String regular_ = "true";
	private String separable_ = "true";
	private String evals_ = Integer.toString(10000);

	public SphereEvaluation() {
	}

	private double function(double[] var1) {
		double var2 = 0.0D;

		for(int var4 = 0; var4 < 10; ++var4) {
			var2 += var1[var4] * var1[var4];
		}

		return var2;
	}

	public Object evaluate(Object var1) {
		if(!(var1 instanceof double[])) {
			throw new IllegalArgumentException();
		} else {
			double[] var2 = (double[])((double[])var1);
			if(var2.length != 10) {
				throw new IllegalArgumentException();
			//} else if(this.evaluations_ > 10000) {
			//	return null;
			} else {
				double var3 = 10.0D - 10.0D * ((this.function(var2) - 0.0D) / 11.5356D);
				if(var3 > this.best_) {
					this.best_ = var3;
				}

				++this.evaluations_;
				return new Double(var3);
			}
		}
	}

	public Object getData(Object var1) {
		return null;
	}

	public double getFinalResult() {
		return this.best_;
	}

	public Properties getProperties() {
		Properties var1 = new Properties();
		var1.put("Multimodal", this.multimodal_);
		var1.put("Regular", this.regular_);
		var1.put("Separable", this.separable_);
		var1.put("Evaluations", this.evals_);
		return var1;
	}
}