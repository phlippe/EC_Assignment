package evaluation;

import java.util.Properties;
import javabbob.JNIfgeneric;
import javabbob.JNIfgeneric.Params;
import org.vu.contest.ContestEvaluation;

public class SchaffersEvaluation implements ContestEvaluation {
	private static final int EVALS_LIMIT_ = 100000;
	private static final int bbobid_ = 17;
	private static final double BASE_ = -13.35288D;
	private JNIfgeneric function_ = null;
	private double best_ = 0.0D;
	private double target_ = 0.0D;
	private int evaluations_ = 0;
	private String multimodal_ = "true";
	private String regular_ = "true";
	private String separable_ = "false";
	private String evals_ = Integer.toString(100000);

	public SchaffersEvaluation() {
		this.function_ = new JNIfgeneric();
		Params var1 = new Params();
		var1.algName = "";
		var1.comments = "";
		JNIfgeneric.makeBBOBdirs("tmp", true);
		this.function_.initBBOB(17, 1, 10, "tmp", var1);
		this.target_ = this.function_.getFtarget();
	}

	public Object evaluate(Object var1) {
		if(!(var1 instanceof double[])) {
			throw new IllegalArgumentException();
		} else {
			double[] var2 = (double[])((double[])var1);
			if(var2.length != 10) {
				throw new IllegalArgumentException();
			//} else if(this.evaluations_ > 100000) {
			//	return null;
			} else {
				double var3 = (this.function_.evaluate(var2) - this.target_) / (-13.35288D - this.target_);
				double var5 = 10.0D * Math.exp(-5.0D * var3);
				if(var5 > 10.0D) {
					var5 = 10.0D;
				} else if(var5 < 0.0D) {
					var5 = 0.0D;
				}

				if(var5 > this.best_) {
					this.best_ = var5;
				}

				++this.evaluations_;
				return new Double(var5);
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

