package ch.epfl.moocprog;

public final class Food extends Positionable{
	private double quantite;
	
	public Food(ToricPosition p, double q) {
		super(p);
		if (q < 0) {
			this.quantite = 0.0;
		} else {
			this.quantite = q;
		}				
	}
	
	public double getQuantity() { return this.quantite; }
	public void setQuantity(double q) { this.quantite = q; }
	
	public double takeQuantity(double t) throws IllegalArgumentException {
		if (t < 0) {
			throw new IllegalArgumentException();
		} else if (t <= getQuantity()) {
			setQuantity(getQuantity()-t);
			return t;
		} else {
			t = getQuantity();
			setQuantity(0);
			return t;
		}
	}
	
	public String toString() {
		return super.toString() + "\n" + String.format("Quantity : %.2f", getQuantity()); 
	}

}
