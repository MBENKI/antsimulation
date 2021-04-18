package ch.epfl.moocprog;

public interface AntEnvironmentView extends AnimalEnvironmentView {
	public abstract void addPheromone(Pheromone pheromone);
	public abstract double[] getPheromoneQuantitiesPerIntervalForAnt(ToricPosition position, double directionAngleRad, double[] angles);
}
