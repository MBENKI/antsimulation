package ch.epfl.moocprog;

import java.util.List;
import ch.epfl.moocprog.utils.Time;

public interface AnimalEnvironmentView {
	public abstract void selectSpecificBehaviorDispatch(AntWorker antWorker, Time dt);
	public abstract void selectSpecificBehaviorDispatch(AntSoldier antSoldier, Time dt);
	public abstract void selectSpecificBehaviorDispatch(Termite termite, Time dt);
	public abstract RotationProbability selectComputeRotationProbsDispatch(Ant ant);
	public abstract void selectAfterMoveDispatch(Ant ant, Time dt);
	public abstract void selectAfterMoveDispatch(Termite termite, Time dt);
	public abstract RotationProbability selectComputeRotationProbsDispatch(Termite termite);
	public abstract List<Animal> getVisibleEnemiesForAnimal(Animal from);
	public abstract boolean isVisibleFromEnemies(Animal from);
}
