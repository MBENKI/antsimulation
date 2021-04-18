package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.TERMITE_SPEED;
import static ch.epfl.moocprog.config.Config.TERMITE_ATTACK_DURATION;
import static ch.epfl.moocprog.config.Config.TERMITE_MAX_STRENGTH;
import static ch.epfl.moocprog.config.Config.TERMITE_MIN_STRENGTH;
import static ch.epfl.moocprog.config.Config.TERMITE_HP;
import static ch.epfl.moocprog.config.Config.TERMITE_LIFESPAN;

public final class Termite extends Animal {
	public Termite(ToricPosition tp) {
		super(tp, getConfig().getInt(TERMITE_HP), getConfig().getTime(TERMITE_LIFESPAN));
	}
	
	public void accept(AnimalVisitor v, RenderingMedia s) {
		v.visit(this, s);
	}
	
	@Override
	public double getSpeed() { return getConfig().getDouble(TERMITE_SPEED); }
	
	public void seekForEnemies(AnimalEnvironmentView env, Time dt) { 
		super.move(env, dt);
		super.fight(env, dt);
	}
	
	@Override
	public void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) { env.selectSpecificBehaviorDispatch(this, dt);}

	@Override
	protected RotationProbability computeRotationProbsDispatch(AnimalEnvironmentView env) {
		return env.selectComputeRotationProbsDispatch(this);
	}
	
	protected RotationProbability computeRotationProbs(TermiteEnvironmentView env) {
		return super.computeDefaultRotationProbs();
	}

	@Override
	protected void afterMoveDispatch(AnimalEnvironmentView env, Time dt) {
		env.selectAfterMoveDispatch(this, dt);		
	}
	
	protected void afterMoveTermite(TermiteEnvironmentView env, Time dt) { }
	
	public boolean isEnemy(Animal other) {
		return !this.isDead() && !other.isDead() && other.isEnemyDispatch(this);
	}
	
	protected boolean isEnemyDispatch(Ant ant) { return true; }
	
	protected boolean isEnemyDispatch(Termite termite) { return false; }
	
	@Override
	public int getMinAttackStrength() { return getConfig().getInt(TERMITE_MIN_STRENGTH); }
	@Override
	public int getMaxAttackStrength() { return getConfig().getInt(TERMITE_MAX_STRENGTH); }
	@Override
	public Time getMaxAttackDuration() { return getConfig().getTime(TERMITE_ATTACK_DURATION); }
}
