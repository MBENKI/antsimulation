package ch.epfl.moocprog;

import static ch.epfl.moocprog.random.UniformDistribution.getValue;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.utils.Utils.closestFromPoint;
import static ch.epfl.moocprog.random.UniformDistribution.getValue;
import static ch.epfl.moocprog.config.Config.ANIMAL_LIFESPAN_DECREASE_FACTOR;
import static ch.epfl.moocprog.config.Config.ANIMAL_NEXT_ROTATION_DELAY;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Utils;
import ch.epfl.moocprog.utils.Vec2d;

public abstract class Animal extends Positionable {
	private double angleDirection;
	private int hitpoints;
	private Time lifespan;
	private Time rotationDelay = Time.ZERO;
	private Time attackDuration = Time.ZERO;
	private State state;
	
	public enum State {
		IDLE, ESCAPING, ATTACK;
	}
	
	public Animal(ToricPosition tp) {
		super(tp);
		this.state = State.IDLE;
	}
	
	public Animal(ToricPosition tp, int hits, Time life) {
		super(tp);
		this.hitpoints = hits;
		this.lifespan = life;
		this.angleDirection = getValue(0.0, 2*Math.PI);
		this.state = State.IDLE;
	}
	
	public final double getDirection() { return this.angleDirection; }
	public final int getHitpoints() { return this.hitpoints; }
	public final Time getLifespan() { return this.lifespan; }
	public abstract double getSpeed();
	public final Time getRotationDelay() { return this.rotationDelay; }
	public final State getState() { return this.state; }
	
	public abstract void accept(AnimalVisitor visitor, RenderingMedia s);
	
	public String toString() {
		return super.toString() + 
				String.format("\nSpeed : %.1f\nHitPoints : %d\nLifeSpan : %f\nState : " + this.getState().toString(), this.getSpeed(), this.hitpoints, this.lifespan.toSeconds());
	}
	
	protected abstract void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt);
	
	protected abstract RotationProbability computeRotationProbsDispatch(AnimalEnvironmentView env);
	
	protected abstract void afterMoveDispatch(AnimalEnvironmentView env, Time dt);
	
	public final void setDirection(double angle) { this.angleDirection = angle; }
	public final void setState(State newState) { this.state = newState; }
	public final void setHitpoints(int hits) { this.hitpoints += hits; }
	
	public final boolean isDead() { return this.hitpoints <= 0 || this.lifespan.compareTo(Time.ZERO) <= 0; }
	
	public final void update(AnimalEnvironmentView env, Time dt) {
		if (!isDead()) {
			switch (this.state) {
			case IDLE : specificBehaviorDispatch(env, dt);
			case ESCAPING : escape(env, dt);
			case ATTACK : if (canAttack()) fight(env, dt);
						  else { 
							  setState(State.ESCAPING);
							  this.attackDuration = Time.ZERO;
						  }
			}
			this.lifespan = getLifespan().minus(dt.times(getConfig().getDouble(ANIMAL_LIFESPAN_DECREASE_FACTOR)));
		}
	}
	
	protected final boolean canAttack() {
		return !this.getState().equals(State.ESCAPING) && (this.attackDuration.compareTo(getMaxAttackDuration()) <= 0);
	}
	
	protected final void escape(AnimalEnvironmentView env, Time dt) {
		this.move(env, dt);
		if (!env.isVisibleFromEnemies(this)) setState(State.IDLE);
	}
	
	protected final void fight(AnimalEnvironmentView env, Time dt) {
		Animal enemy = closestFromPoint(this, env.getVisibleEnemiesForAnimal(this));
		if (enemy != null) {
			if (!getState().equals(State.ATTACK)) setState(State.ATTACK);
			enemy.setState(State.ATTACK);
			enemy.setHitpoints((int)getValue(this.getMinAttackStrength(), this.getMaxAttackStrength()) * -1);
			this.attackDuration = this.attackDuration.plus(dt);
		} else {
			if (getState().equals(State.ATTACK)) this.attackDuration = Time.ZERO;
			setState(State.ESCAPING);
		}
	}
	
	protected final void move(AnimalEnvironmentView env, Time dt) {		
		final Time delay = getConfig().getTime(ANIMAL_NEXT_ROTATION_DELAY);
		this.rotationDelay = getRotationDelay().plus(dt);
		while (getRotationDelay().compareTo(delay) >= 0) {
			rotate(env);
			this.rotationDelay = getRotationDelay().minus(delay);
		}
		this.getPosition().toVec2d();
		setPosition(this.getPosition().add(Vec2d.fromAngle(this.angleDirection).scalarProduct(dt.toSeconds()*(getSpeed()))));
		afterMoveDispatch(env, dt);
	}
	
	protected final RotationProbability computeDefaultRotationProbs() {
		double[] a = {-180, -100, -55, -25, -10, 0, 10, 25, 55, 100, 180};
		for (int i  = 0; i < a.length; ++i) a[i] = Math.toRadians(a[i]);
		double[] p = {0.0000, 0.0000, 0.0005, 0.0010, 0.0050, 0.9870, 0.0050, 0.0010, 0.0005, 0.0000, 0.0000};
		return new RotationProbability(a, p);
	}
	
	private void rotate(AnimalEnvironmentView env) {
		RotationProbability rp = computeRotationProbsDispatch(env);
		setDirection(getDirection() + Utils.pickValue(rp.getAngles(), rp.getProbabilities()));
	}
	
	protected abstract boolean isEnemy(Animal entity) ;
	protected abstract boolean isEnemyDispatch(Termite other) ;
	protected abstract boolean isEnemyDispatch(Ant other) ;
	
	protected abstract int getMinAttackStrength();
	protected abstract int getMaxAttackStrength();
	protected abstract Time getMaxAttackDuration();
}
