package ch.epfl.moocprog;

import static ch.epfl.moocprog.app.Context.getConfig;
import static ch.epfl.moocprog.config.Config.*;
import ch.epfl.moocprog.utils.Vec2d;

public final class ToricPosition {
	private final Vec2d position;
	
	private static Vec2d clampedPosition(double x, double y) {
		int wx = getConfig().getInt(WORLD_WIDTH);
		int wy = getConfig().getInt(WORLD_HEIGHT);
		if (x >= wx) {
			while(x >= wx ) { x -= wx; }
		} else if (x < 0) {
			while(x < 0) { x += wx; }
		}
		if (y >= wy) {
			while(y >= wy) { y -= wy; }
		} else if (y < 0) {
			while(y < 0) { y += wy; }
		}
		return new Vec2d(x, y);
	}
	
	public ToricPosition() {
		this.position = new Vec2d(0.0, 0.0);
	}
	
	public ToricPosition(double x, double y) {
		this.position = clampedPosition(x, y);
	}
	
	public ToricPosition(Vec2d vect) {
		this.position = clampedPosition(vect.getX(), vect.getY());
	}
	
	public ToricPosition add(ToricPosition that) {
		return new ToricPosition(this.position.add(that.position));
	}
	
	public ToricPosition add(Vec2d vec) {
		return new ToricPosition(this.position.add(vec));
	}
	
	public Vec2d toVec2d() { return this.position; }
	
	private boolean min(double a, double b) { 
		if (a > b) { 
			return true; 
		}
		return false;
	}
	
	public final Vec2d toricVector(ToricPosition that) { /* plus court chemin de this.v a that.v */
		int wx = getConfig().getInt(WORLD_WIDTH);
		int wy = getConfig().getInt(WORLD_HEIGHT);
		double res = this.position.distance(that.position); /* that lui meme*/
		Vec2d vres = that.toVec2d().minus(this.position);
		/*that (0, +height)*/
		double dist = this.position.distance(that.toVec2d().add(new Vec2d(0, wy)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().add(new Vec2d(0, wy))).minus(this.position);
			res = dist;
		}
		/*that (0, -height)*/
		dist = this.position.distance(that.toVec2d().minus(new Vec2d(0, wy)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().minus(new Vec2d(0, wy))).minus(this.position);
			res = dist;
		}
		/*that (+width, 0)*/
		dist = this.position.distance(that.toVec2d().add(new Vec2d(wx, 0)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().add(new Vec2d(wx, 0))).minus(this.position);
			res = dist;
		}
		/*that (-width, 0)*/
		dist = this.position.distance(that.toVec2d().minus(new Vec2d(wx, 0)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().minus(new Vec2d(wx, 0))).minus(this.position);
			res = dist;
		}
		/*that (+width, +height)*/
		dist = this.position.distance(that.toVec2d().add(new Vec2d(wx, wy)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().add(new Vec2d(wx, wy))).minus(this.position);
			res = dist;
		}
		/*that (-width, -height)*/
		dist = this.position.distance(that.toVec2d().minus(new Vec2d(wx, wy)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().minus(new Vec2d(wx, wy))).minus(this.position);
			res = dist;
		}
		/*that (+width, -height)*/
		dist = this.position.distance(that.toVec2d().add(new Vec2d(wx, -wy)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().add(new Vec2d(wx, -wy))).minus(this.position);
			res = dist;
		}
		/*that (-width, +height)*/
		dist = this.position.distance(that.toVec2d().add(new Vec2d(-wx, wy)));
		if (min(res, dist)) { 
			vres = (that.toVec2d().add(new Vec2d(-wx, wy))).minus(this.position);
			res = dist;}
		
		return vres;
	}
	
	public double toricDistance(ToricPosition that) {
		return toricVector(that).length();
	}
	
	public String toString() { return this.position.toString(); }
}
