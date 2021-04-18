package ch.epfl.moocprog;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;
import ch.epfl.moocprog.gfx.EnvironmentRenderer;
import ch.epfl.moocprog.utils.Time;
import ch.epfl.moocprog.utils.Utils;

public final class Environment implements FoodGeneratorEnvironmentView, AnthillEnvironmentView, AntEnvironmentView, AntWorkerEnvironmentView, TermiteEnvironmentView {
	private FoodGenerator fg;
	private List<Food> listFoods;
	private List<Animal> listAnimals;
	private List<Anthill> listAnthills;
	private List<Pheromone> listPheromones;
	
	public Environment() {
		this.fg = new FoodGenerator();
		this.listFoods = new LinkedList<Food>();
		this.listAnimals = new LinkedList<Animal>();
		this.listAnthills = new LinkedList<Anthill>();
		this.listPheromones = new LinkedList<Pheromone>();
	}

	@Override
	public void addFood(Food food) {
		Utils.requireNonNull(food);
		this.listFoods.add(food);
	}
	
	@Override
	public void addAnt(Ant ant) { 
		Utils.requireNonNull(ant);
		addAnimal(ant);
	}
	
	@Override
	public void addPheromone(Pheromone pheromone) {
		Utils.requireNonNull(pheromone);
		this.listPheromones.add(pheromone);
	}
	
	@Override
	public Food getClosestFoodForAnt(AntWorker antWorker) {
		if(antWorker == null) { throw new IllegalArgumentException(); }
		double perceptibleDist = Context.getConfig().getDouble(Config.ANT_MAX_PERCEPTION_DISTANCE);
		ArrayList<Food> perceptibleFoods = new ArrayList<Food>();
		for (Food f : this.listFoods) {
			double distance = antWorker.getPosition().toricDistance(f.getPosition());
			if (distance <= perceptibleDist) { perceptibleFoods.add(f); }
		}
		return Utils.closestFromPoint(antWorker, perceptibleFoods);
	}
	
	@Override
	public double[] getPheromoneQuantitiesPerIntervalForAnt(ToricPosition position, double directionAngleRad, double[] angles) {
		if (position == null || angles == null) throw new IllegalArgumentException();
		double smellDist = Context.getConfig().getDouble(Config.ANT_SMELL_MAX_DISTANCE);
		//double[] smellAngles = {-180, -100, -55, -25, -10, 0, 10, 25, 55, 100, 180};
		double[] T = new double[angles.length];
		Iterator<Pheromone> iterPher = this.listPheromones.iterator();
		while (iterPher.hasNext()) {
			Pheromone p = iterPher.next();
			if (!p.isNegligible() && (position.toricDistance(p.getPosition()) <= smellDist)) {
				double betaAngle = position.toricVector(p.getPosition()).angle();
				double minDistAngles = closestAngleFrom(angles[0], betaAngle);
				int minIndex = 0;
				for (int i = 1; i < angles.length; ++i) {
					double close = closestAngleFrom(angles[i], betaAngle);
					if (minDistAngles > close) {
						minDistAngles = close;
						minIndex = i;
					}
				}
				T[minIndex] += p.getQuantity();
			}
		}			
		return T;		
	}
	
	private static double normalizedAngle(double angle) {
		if (angle < 0) return angle + 2 * Math.PI;
		else if (angle > 2 * Math.PI) return angle - 2 * Math.PI;
		else return angle;
	}
	
	private static double closestAngleFrom(double angle, double target) {
		double diff = normalizedAngle(angle-target);
		double dunno = 2*Math.PI - diff;
		if (diff < dunno) return diff;
		else return dunno;
	}
	
	@Override
	public boolean dropFood(AntWorker antWorker) {
		Utils.requireNonNull(antWorker);
		double perceptibleDist = Context.getConfig().getDouble(Config.ANT_MAX_PERCEPTION_DISTANCE);
		for (int i = 0; i < this.listAnthills.size(); ++i) {
			Anthill hill = this.listAnthills.get(i);
			double distance = antWorker.getPosition().toricDistance(hill.getPosition());
			if (hill.getAnthillId().equals(antWorker.getAnthillId()) && distance <= perceptibleDist) {
				double antLoad = antWorker.getFoodQuantity();
				if (antLoad < 0) { throw new IllegalArgumentException(); }
				else {
					hill.dropFood(antLoad);
				    return true;
				}
			}
		}
		return false;
	}
	
	public List<Double> getFoodQuantities() {
		List<Double> listQuantities = new ArrayList<Double>();
		for (Food f : this.listFoods) { listQuantities.add(f.getQuantity()); }
		return listQuantities;
	}
	
	public List<ToricPosition> getAnimalsPosition() {
		List<ToricPosition> listPositions = new ArrayList<ToricPosition>();
		for (Animal a : this.listAnimals) { listPositions.add(a.getPosition()); }
		return listPositions;
	}
	
	public List<Double> getPheromonesQuantities() {
		List<Double> listPheros = new ArrayList<Double>();
		for (Pheromone p : this.listPheromones) { listPheros.add(p.getQuantity()); }
		return listPheros;
	}
	
	public void update(Time dt) {
		this.fg.update(this, dt); // update/generate food sources in environment
		// update pheromones 
		Iterator<Pheromone> iterPher = this.listPheromones.iterator();
		while (iterPher.hasNext()) {
			Pheromone p = iterPher.next();
			if (p.isNegligible()) { iterPher.remove(); }
			else { p.update(dt); }
		}
		// update anthills and creation of new ants
		Iterator<Anthill> iterHill = this.listAnthills.iterator();
		while (iterHill.hasNext()) {
			Anthill h = iterHill.next();
			h.update(this, dt);
		}
		// update animals in environment
		Iterator<Animal> iter = this.listAnimals.iterator();
		while (iter.hasNext()) {
			Animal a = iter.next();
			if (a.isDead()) { iter.remove(); }
			else { a.update(this, dt); }
		}
		// eliminate empty food sources
		this.listFoods.removeIf(food -> food.getQuantity() <= 0);
	}
	
	public void renderEntities(EnvironmentRenderer environmentRenderer) {
		this.listAnimals.forEach(environmentRenderer::renderAnimal);
		this.listFoods.forEach(environmentRenderer::renderFood);
		this.listAnthills.forEach(environmentRenderer::renderAnthill);
		this.listPheromones.forEach(environmentRenderer::renderPheromone);
	}
	
	public void addAnthill(Anthill anthill) {
		Utils.requireNonNull(anthill);
		this.listAnthills.add(anthill);
	}
	
	public void addAnimal(Animal animal) {
		Utils.requireNonNull(animal);
		this.listAnimals.add(animal);
	}
	
	public int getWidth() {
		return Context.getConfig().getInt(Config.WORLD_WIDTH);
	}
	
	public int getHeight() {
		return Context.getConfig().getInt(Config.WORLD_HEIGHT);
	}

	@Override
	public void selectSpecificBehaviorDispatch(AntWorker antWorker, Time dt) {
		antWorker.seekForFood(this, dt);		
	}

	@Override
	public void selectSpecificBehaviorDispatch(AntSoldier antSoldier, Time dt) {
		antSoldier.seekForEnemies(this, dt);		
	}
	
	@Override
	public RotationProbability selectComputeRotationProbsDispatch(Ant ant) {
		return ant.computeRotationProbs(this);
	}
	
	@Override
	public void selectAfterMoveDispatch(Ant ant, Time dt) {
		ant.afterMoveAnt(this, dt);
	}
	
	@Override
	public void selectSpecificBehaviorDispatch(Termite termite, Time dt) { 
		termite.seekForEnemies(this, dt);
	}
	
	@Override
	public RotationProbability selectComputeRotationProbsDispatch(Termite termite) {
		return termite.computeRotationProbs(this);
	}
	
	@Override
	public void selectAfterMoveDispatch(Termite termite, Time dt) {
		termite.afterMoveTermite(this, dt);
	}
	
	public List<Animal> getVisibleEnemiesForAnimal(Animal from) {
		if(from == null) { throw new IllegalArgumentException(); }
		double animalSight = Context.getConfig().getDouble(Config.ANIMAL_SIGHT_DISTANCE);
		List<Animal> sightedAnimals = new ArrayList<Animal>();
		for (Animal a : this.listAnimals) {
			if (isVisibleFromEnemies(a) && from.isEnemy(a) && from.getPosition().toricDistance(a.getPosition()) <= animalSight) { 
				sightedAnimals.add(a); 
			}
		}
		return sightedAnimals;		
	}
	
	public boolean isVisibleFromEnemies(Animal from) {
		if(from == null) { throw new IllegalArgumentException(); }
		double animalSight = Context.getConfig().getDouble(Config.ANIMAL_SIGHT_DISTANCE);
		ToricPosition fromPosition = from.getPosition();
		int count = 0;
		for (ToricPosition apos : getAnimalsPosition()) {
			if (fromPosition.toricDistance(apos) <= animalSight) count++;
		}
		return count > 0;		
	}
}
