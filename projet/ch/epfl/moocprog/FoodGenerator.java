package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;
import static ch.epfl.moocprog.app.Context.getConfig;
import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.random.NormalDistribution;
import ch.epfl.moocprog.random.UniformDistribution;

public final class FoodGenerator {
	private Time time;
	
	public FoodGenerator() { this.time = Time.ZERO; }
	
	public void update(FoodGeneratorEnvironmentView env, Time dt) {
		this.time = this.time.plus(dt);
		final Time foodGeneratorDelay = Context.getConfig().getTime(Config.FOOD_GENERATOR_DELAY);
		double tailleX = Context.getConfig().getInt(Config.WORLD_WIDTH);
		double tailleY = Context.getConfig().getInt(Config.WORLD_HEIGHT);
		double nourriture = UniformDistribution.getValue(getConfig().getDouble(Config.NEW_FOOD_QUANTITY_MIN), getConfig().getDouble(Config.NEW_FOOD_QUANTITY_MAX));
		double X = NormalDistribution.getValue(tailleX/2.0, (tailleX*tailleX)/16.0);
		double Y = NormalDistribution.getValue(tailleY/2.0, (tailleY*tailleY)/16.0);
		while (this.time.compareTo(foodGeneratorDelay) >= 0) {
			this.time = this.time.minus(foodGeneratorDelay);
			Food f = new Food(new ToricPosition(X, Y), nourriture);
			env.addFood(f);
		}
	}
}
