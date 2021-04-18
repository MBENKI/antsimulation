package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.ANT_SOLDIER_HP;
import static ch.epfl.moocprog.config.Config.ANT_SOLDIER_LIFESPAN;
import static ch.epfl.moocprog.config.Config.ANT_SOLDIER_SPEED;
import static ch.epfl.moocprog.config.Config.ANT_SOLDIER_ATTACK_DURATION;
import static ch.epfl.moocprog.config.Config.ANT_SOLDIER_MAX_STRENGTH;
import static ch.epfl.moocprog.config.Config.ANT_SOLDIER_MIN_STRENGTH;
import ch.epfl.moocprog.utils.Time;

public final class AntSoldier extends Ant {
	
	public AntSoldier(ToricPosition tp, Uid hillId) {
		super(tp, getConfig().getInt(ANT_SOLDIER_HP), getConfig().getTime(ANT_SOLDIER_LIFESPAN), hillId);
	}
	
	public AntSoldier(ToricPosition tp, Uid hillId, AntRotationProbabilityModel antSRotMod) {
		super(tp, getConfig().getInt(ANT_SOLDIER_HP), getConfig().getTime(ANT_SOLDIER_LIFESPAN), hillId, antSRotMod);
	}
	
	public void accept(AnimalVisitor v, RenderingMedia s) {
		v.visit(this, s);
	}
	
	public void seekForEnemies(AntEnvironmentView env, Time dt) { 
		super.move(env, dt);
		super.spreadPheromones(env);
		super.fight(env, dt);
	}
	
	@Override
	protected void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) { env.selectSpecificBehaviorDispatch(this, dt); }
	
	@Override
	public double getSpeed() { return getConfig().getDouble(ANT_SOLDIER_SPEED); }
	
	@Override
	protected int getMinAttackStrength() { return getConfig().getInt(ANT_SOLDIER_MIN_STRENGTH); }
	@Override
	protected int getMaxAttackStrength() { return getConfig().getInt(ANT_SOLDIER_MAX_STRENGTH); }
	@Override
	protected Time getMaxAttackDuration() { return getConfig().getTime(ANT_SOLDIER_ATTACK_DURATION); }
}
