package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Utils;

public class RotationProbability {
	private double[] angles = {}; //= {-180, -135, -90, -45, 0, 45, 90, 135, 180};
	private double[] probabilities = {}; //= {0.00, 0.01, 0.09, 0.15, 0.50, 0.15, 0.09, 0.01, 0.00};
	
	public RotationProbability(double[] angles, double[] probas) {
		Utils.requireNonNull(angles);
		Utils.requireNonNull(probas);
		Utils.require(angles.length == probas.length);
		this.angles = angles.clone();
		this.probabilities = probas.clone();
	}
	
	public double[] getAngles() { return this.angles.clone(); }
	public double[] getProbabilities() { return this.probabilities.clone(); }
}
