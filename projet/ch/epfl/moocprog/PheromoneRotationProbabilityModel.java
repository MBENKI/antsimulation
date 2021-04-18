package ch.epfl.moocprog;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.ALPHA;
import static ch.epfl.moocprog.config.Config.BETA_D;
import static ch.epfl.moocprog.config.Config.Q_ZERO;

public class PheromoneRotationProbabilityModel implements AntRotationProbabilityModel {
	// double[] I = {-180, -135, -90, -45, 0, 45, 90, 135, 180}; // angles
	// double[] P = {0.00, 0.01, 0.09, 0.15, 0.5, 0.15, 0.09, 0.01, 0.00};  // prob par angle
	// double[] Q = {20, 0, 0, 2, 0, 0, 10, 0, 5};  // quantities per sector
	// double[] Q' = {1, 0.007 , 0.007 ,0.047 , 0.007, 0.007 , 0.993, 0.007 , 0.5};  // prob avec QZero=5 et beta=1  [1.0 / (1.0 + Math.exp(-beta*(x-QZero)))]
	// double[] P' = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.00} avec alpha=5 
	// nouveau tableau de probs de rotation P' formule: numerateur[i] = P[i] * Math.pow(Q'[i]), alpha ); denominateur S += numerateur[i];
	
	public RotationProbability computeRotationProbs(
			RotationProbability movementMatrix, 
			ToricPosition position,
			double directionAngle,
			AntEnvironmentView env) {
		double beta = getConfig().getDouble(BETA_D);
		double QZero = getConfig().getDouble(Q_ZERO);
		int alfa = getConfig().getInt(ALPHA);
		double s = 1.0;
		double[] I = movementMatrix.getAngles();
		double[] P = movementMatrix.getProbabilities();
		double[] Q = env.getPheromoneQuantitiesPerIntervalForAnt(position, directionAngle, I);
		double[] Q1 = new double[Q.length];
		for (int i = 0; i < Q1.length; ++i) {  // apply formula to obtain Q'
			Q1[i] = Math.round((1.0 / (1.0 + Math.exp(-beta*(Q[i]-QZero)))) * 1000d) / 1000d;
		}
		double[] P1 = new double[P.length];
		for (int j = 0; j < P.length; ++j) {
			double n = P[j] * Math.pow(Q1[j], alfa);
			s += n;
			P1[j] = n / s; 
		}
		return new RotationProbability(I, P1);
	}
}
