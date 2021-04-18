package ch.epfl.moocprog;

public class Positionable {
	private ToricPosition tp;
	
	public Positionable() {
		this.tp = new ToricPosition();
	}
	
	public Positionable(ToricPosition tp) {
		this.tp = new ToricPosition(tp.toVec2d().getX(), tp.toVec2d().getY());
	}
	
	public ToricPosition getPosition() { return this.tp; }
	
	protected final void setPosition(ToricPosition position) {
		this.tp = position;
	}
	
	public String toString() {
		return this.tp.toString();
	}
}
