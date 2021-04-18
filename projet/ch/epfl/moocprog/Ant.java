package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Vec2d;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.ANT_PHEROMONE_DENSITY;
import static ch.epfl.moocprog.config.Config.ANT_PHEROMONE_ENERGY;

public abstract class Ant extends Animal {
	private Uid antHillId;
	private ToricPosition lastPos;
	private ToricPosition currentPos;
	private AntRotationProbabilityModel probModel;
	
	public Ant(ToricPosition tp) {
		super(tp);
	}

	public Ant(ToricPosition tp, int hits, Time life, Uid hillId) {
		super(tp, hits, life);
		this.antHillId = hillId;
		this.currentPos = this.getPosition();
		this.lastPos = this.getPosition();
		this.probModel = new PheromoneRotationProbabilityModel();
	}
	
	public Ant(ToricPosition tp, int hits, Time life, Uid hillId, AntRotationProbabilityModel antRotMod) {
		super(tp, hits, life);
		this.antHillId = hillId;
		this.currentPos = this.getPosition();
		this.lastPos = this.getPosition();
		this.probModel = antRotMod;
	}
	
	public final Uid getAnthillId() { return this.antHillId; }
	
	protected final void spreadPheromones(AntEnvironmentView env) {
		double densite = getConfig().getDouble(ANT_PHEROMONE_DENSITY);
		this.currentPos = this.getPosition();
		double d = this.lastPos.toricDistance(this.getPosition());
		int numPheros = (int) (d * densite);
		double step = d / numPheros;
		Vec2d direction = this.lastPos.toricVector(this.currentPos).normalized(); // direction vector lastPos --> currentPos ????
		for (int i = 0; i < numPheros; ++i) {
			this.lastPos = this.lastPos.add(direction.scalarProduct(step)); // update lastPos
			env.addPheromone(new Pheromone(this.lastPos, getConfig().getDouble(ANT_PHEROMONE_ENERGY))); 
		}
	}
	
	protected final RotationProbability computeRotationProbs(AntEnvironmentView env) {
		return this.probModel.computeRotationProbs(super.computeDefaultRotationProbs(), this.getPosition(), this.getDirection(), env);
	}
	
	protected final RotationProbability computeRotationProbsDispatch(AnimalEnvironmentView env) {
		return env.selectComputeRotationProbsDispatch(this);
	}
	
	protected final void afterMoveDispatch(AnimalEnvironmentView env, Time dt) { env.selectAfterMoveDispatch(this, dt);	}
	
	protected final void afterMoveAnt(AntEnvironmentView env, Time dt) { spreadPheromones(env); }
	
	protected final boolean isEnemy(Animal other) {
		return !this.isDead() && !other.isDead() && other.isEnemyDispatch(this);
	}
	
	protected final boolean isEnemyDispatch(Ant ant) { return !this.getAnthillId().equals(ant.getAnthillId()); }
	
	protected final boolean isEnemyDispatch(Termite termite) { return true; }
}
