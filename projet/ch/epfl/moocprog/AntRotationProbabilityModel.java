package ch.epfl.moocprog;

public interface AntRotationProbabilityModel {
	public abstract RotationProbability computeRotationProbs(
			RotationProbability movementMatrix, 
			ToricPosition position,
			double directionAngle,
			AntEnvironmentView env);
}
