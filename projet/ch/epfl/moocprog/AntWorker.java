package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.ANT_WORKER_HP;
import static ch.epfl.moocprog.config.Config.ANT_WORKER_LIFESPAN;
import static ch.epfl.moocprog.config.Config.ANT_WORKER_SPEED;
import static ch.epfl.moocprog.config.Config.ANT_MAX_FOOD;
import static ch.epfl.moocprog.config.Config.ANT_WORKER_ATTACK_DURATION;
import static ch.epfl.moocprog.config.Config.ANT_WORKER_MAX_STRENGTH;
import static ch.epfl.moocprog.config.Config.ANT_WORKER_MIN_STRENGTH;
import ch.epfl.moocprog.utils.Time;

public final class AntWorker extends Ant {
	private double foodQuantity = 0;
	
	public AntWorker(ToricPosition tp, Uid hillId) {
		super(tp, getConfig().getInt(ANT_WORKER_HP), getConfig().getTime(ANT_WORKER_LIFESPAN), hillId);
	}
	
	public AntWorker(ToricPosition tp, Uid hillId, AntRotationProbabilityModel antWRotMod) {
		super(tp, getConfig().getInt(ANT_WORKER_HP), getConfig().getTime(ANT_WORKER_LIFESPAN), hillId, antWRotMod);
	}
	
	public void accept(AnimalVisitor v, RenderingMedia s) {
		v.visit(this, s);
	}
	
	public double getFoodQuantity() { return this.foodQuantity; }
	
	public void setFoodQuantity(double a) { this.foodQuantity = a; }
	
	@Override
	public double getSpeed() { return getConfig().getDouble(ANT_WORKER_SPEED); }
	@Override
	protected int getMinAttackStrength() { return getConfig().getInt(ANT_WORKER_MIN_STRENGTH); }
	@Override
	protected int getMaxAttackStrength() { return getConfig().getInt(ANT_WORKER_MAX_STRENGTH); }
	@Override
	protected Time getMaxAttackDuration() { return getConfig().getTime(ANT_WORKER_ATTACK_DURATION); }
	
	private void uTurn() {
		this.setDirection(this.getDirection() + Math.PI);
		/*double nextAngle = this.getDirection() + Math.PI;
		if (nextAngle > (2*Math.PI)) this.setDirection(this.getDirection()-(2*Math.PI));
		else this.setDirection(nextAngle);*/
	}
	
	public void seekForFood(AntWorkerEnvironmentView env, Time dt) {
		super.move(env, dt);
		super.spreadPheromones(env);
		if (getFoodQuantity() <= 0) {  //no food load
			Food closeSource = env.getClosestFoodForAnt(this);
			if (closeSource != null) {
				double foodTaken = closeSource.takeQuantity(getConfig().getDouble(ANT_MAX_FOOD));
				setFoodQuantity(foodTaken);
				uTurn();
			}
		}
		if (getFoodQuantity() > 0) {  //with food load
			if (env.dropFood(this)) {
				setFoodQuantity(0);
				uTurn();
			}			
		}
	}
	
	@Override
	protected void specificBehaviorDispatch(AnimalEnvironmentView env, Time dt) { env.selectSpecificBehaviorDispatch(this, dt); }
	
	public String toString() { return super.toString() + String.format("Quantity : %.2f\n", getFoodQuantity());	}
}
