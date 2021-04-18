package ch.epfl.moocprog;

public interface AntWorkerEnvironmentView extends AntEnvironmentView {
	public abstract Food getClosestFoodForAnt(AntWorker antWorker);
	public abstract boolean dropFood(AntWorker antWorker);
}
