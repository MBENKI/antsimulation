package ch.epfl.moocprog;

import ch.epfl.moocprog.utils.Time;
import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.PHEROMONE_THRESHOLD;
import static ch.epfl.moocprog.config.Config.PHEROMONE_EVAPORATION_RATE;

public final class Pheromone extends Positionable {
	private double quantity;
	
	public Pheromone(ToricPosition tp, double qt) {
		super(tp);
		this.quantity = qt;
	}
	
	public double getQuantity() { return this.quantity;	}
	
	private void decrementQuantity(double q) { this.quantity -= q; }
	
	public boolean isNegligible() { return this.quantity < getConfig().getDouble(PHEROMONE_THRESHOLD); }
	
	public void update(Time dt) {
		final double evapRate = getConfig().getDouble(PHEROMONE_EVAPORATION_RATE);
		if (!isNegligible()) decrementQuantity(dt.toSeconds()*evapRate);
		if (getQuantity() < 0) this.quantity = 0.0;
	}
	
}
