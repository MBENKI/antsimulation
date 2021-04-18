package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.random.UniformDistribution.getValue;
import static ch.epfl.moocprog.config.Config.ANTHILL_WORKER_PROB_DEFAULT;
import static ch.epfl.moocprog.config.Config.ANTHILL_SPAWN_DELAY;
import ch.epfl.moocprog.utils.Time;

public final class Anthill extends Positionable {
	private double foodStock = 0;
	private Uid hillId;
	private double antSpawn;
	private Time time;
	
	public Anthill(ToricPosition tp) {
		super(tp);
		this.hillId = Uid.createUid();
		this.antSpawn = getConfig().getDouble(ANTHILL_WORKER_PROB_DEFAULT);
		this.time = Time.ZERO;
	}
	
	public Anthill(ToricPosition tp, double spawnprob) {
		super(tp);
		this.hillId = Uid.createUid();
		this.antSpawn = spawnprob;
		this.time = Time.ZERO;
	}
	
	public double getFoodQuantity() { return this.foodStock; }
	public Uid getAnthillId() { return this.hillId; }
	public double getSpawnProb() { return this.antSpawn; }
	
	public void update(AnthillEnvironmentView env, Time dt) {
		this.time = this.time.plus(dt);
		final Time AntSpawnDelay = getConfig().getTime(ANTHILL_SPAWN_DELAY);
		while (this.time.compareTo(AntSpawnDelay) >= 0) {
			this.time = this.time.minus(AntSpawnDelay);
			double valeurUniforme = getValue(0, 1);
			if (valeurUniforme <= getSpawnProb()) env.addAnt(new AntWorker(this.getPosition(), getAnthillId()));
			else env.addAnt(new AntSoldier(this.getPosition(), getAnthillId()));
		}		
	}
	
	public void dropFood(double toDrop) { 
		if (toDrop < 0) throw new IllegalArgumentException();
		this.foodStock += toDrop; 
	}
	
	public String toString() { return super.toString() + String.format("\nQuantity : %.2f\n", getFoodQuantity()); }
}
