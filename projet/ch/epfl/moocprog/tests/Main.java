package ch.epfl.moocprog.tests;

import ch.epfl.moocprog.app.ApplicationInitializer;
import ch.epfl.moocprog.config.ImmutableConfigManager;
import java.io.File;
import java.util.Arrays;
import ch.epfl.moocprog.Pheromone;
import ch.epfl.moocprog.*;
import ch.epfl.moocprog.utils.*;
import ch.epfl.moocprog.app.Context;
import ch.epfl.moocprog.config.Config;

public class Main {

    public static void main(String[] args) {
        ApplicationInitializer.initializeApplication(
            new ImmutableConfigManager(
                new File("res/app.cfg")
            )
        );
        final int width  = Context.getConfig().getInt(Config.WORLD_WIDTH);
        final int height = Context.getConfig().getInt(Config.WORLD_HEIGHT);
        Environment env = new Environment();

        ToricPosition tp1 = new ToricPosition();
        ToricPosition tp2 = new ToricPosition(1.2, 2.3);
        ToricPosition tp3 = new ToricPosition(new Vec2d(4.5, 6.7));
        ToricPosition tp4 = tp3.add(tp2);
        ToricPosition tp5 = new ToricPosition(width, height);
        ToricPosition tp6 = new ToricPosition(width/2, height/2);
        ToricPosition tp7 = tp4.add(tp6.add(new Vec2d(width/2, height/2)));
        ToricPosition tp8 = new ToricPosition(3, 4);
        Vec2d v1 = tp2.toricVector(tp3);

        System.out.println("Some tests for ToricPosition");
        System.out.println("Default toric position : " + tp1);
        System.out.println("tp2 : " + tp2);
        System.out.println("tp3 : " + tp3);
        System.out.println("tp4 (tp2 + tp3) : " + tp4);
        System.out.println("Toric vector between tp2 and tp3 : " + v1);
        System.out.println("World dimension (clamped) : " + tp5);
        System.out.println("Half world dimension : " +tp6);
        System.out.println("tp3 + 2 * half world dimension = " + tp7);
        System.out.println("Length of vector (3, 4) : " + tp1.toricDistance(tp8));

        Positionable p1 = new Positionable();
        Positionable p2 = new Positionable(tp4);

        System.out.println();
        System.out.println("Some tests for Positionable");
        System.out.println("Default position : " + p1.getPosition());
        System.out.println("Initialized at tp4 : " + p2.getPosition());
        
        Food f1 = new Food(tp2, 4.7);
        Food f2 = new Food(tp3, 6.7);
        System.out.println();
        
        System.out.println("Some tests for Food");
        System.out.println("Display : ");
        System.out.println(f1);
        System.out.println("Initial : " + f1.getQuantity() + ", taken : " + f1.takeQuantity(5.0) + ", left : " + f1.getQuantity());
        System.out.println("Initial : " + f2.getQuantity() + ", taken : " + f2.takeQuantity(2.0) + ", left : " + f2.getQuantity());
        final Time foodGenDelta = Context.getConfig().getTime(Config.FOOD_GENERATOR_DELAY);
        env.addFood(f1);
        env.addFood(f2);
        System.out.println();
        System.out.println("Some tests for Environment");
        System.out.println("Inital food quantities : " + env.getFoodQuantities());
        env.update(foodGenDelta);
        System.out.println("After update : " + env.getFoodQuantities());
        
        // Quelques tests pour l'étape 5
        System.out.println();
        System.out.println ("A termite before update :");
        Termite t1 = new Termite(new ToricPosition(20, 30));
        System.out.println(t1);
        env.addAnimal(t1);
        env.update(Time.fromSeconds(1.));
        System.out.println("The same termite after one update :");
        System.out.println(t1);
        
        // Quelques tests pour l'étape 7
        Anthill anthill = new Anthill(new ToricPosition(10, 20));
        System.out.println("Displaying an anthill");
        System.out.println(anthill);
        env = new Environment();
        env.addAnthill(anthill);
        Food f3 = new Food(new ToricPosition(15, 15), 20.);
        Food f4 = new Food(new ToricPosition(40, 40), 15.);
        env.addFood(f3);
        env.addFood(f4);
        System.out.println();
        AntWorker worker = new AntWorker(new ToricPosition(5, 10), anthill.getAnthillId());
        System.out.println("Displaying a worker ant");
        System.out.println(worker +"\n" );
        System.out.print("Can the worker ant drop some food in its anthill : ");
        // true car la fourmi est assez proche de sa fourmilière
        System.out.println(env.dropFood(worker));
        System.out.println("Displaying the anthill after the antworker dropped food:");
        // aucun changement car la fourmi ne transporte pas de nourriture
        System.out.println(anthill);
        worker.setFoodQuantity(13);
        env.dropFood(worker);
        System.out.println("Displaying the anthill after the antworker dropped food again:");
        System.out.println(anthill);
        System.out.println("\nClosest food seen by the worker ant:" );
        // la fourmi ne « voit » que f3
        // si l'on n'avait que f4, l'appel suivant retournerait null
        System.out.println(env.getClosestFoodForAnt(worker));
        // quelques tests pour l'étape 10
        System.out.println();
        double minQty = Context.getConfig().getDouble(Config.PHEROMONE_THRESHOLD);
        Pheromone pher1 = new Pheromone( new ToricPosition(10.,10.), minQty);
        System.out.print("Pheromone pher1 created with quantity PHEROMONE_THRESHOLD = ");
        System.out.println( minQty );
        System.out.println("the position of the pheromone is :" + pher1.getPosition());
        System.out.println("getQuantity() correctly returns the value " + minQty + " : " + (pher1.getQuantity() == minQty));
        System.out.print("the quantity of the pheromone is negligible : ");
        System.out.println(pher1.isNegligible());
        env = new Environment();
        env.addPheromone(pher1);
        env.update(Time.fromSeconds(1.));
        System.out.print("After one step of evaporation (dt = 1 sec), ");
        System.out.print(" the quantity of pher1 is ");
        System.out.println(pher1.getQuantity() + "\n");
        double offset = minQty / 5.;
        Pheromone pher2 = new Pheromone( new ToricPosition(20.,20.),
        Context.getConfig().getDouble(Config.PHEROMONE_THRESHOLD) - offset);
        System.out.println("Pheromone created with quantity PHEROMONE_THRESHOLD - " + offset);
        System.out.println("the position of the pheromone is :" + pher2.getPosition());
        System.out.print("the quantity of the pheromone is negligible : ");
        System.out.println(pher2.isNegligible() + "\n");
        env.addPheromone(pher2);
        System.out.print("The quantities of pheromone in the environment are: ");
        System.out.println(env.getPheromonesQuantities());
        env.update(Time.fromSeconds(1.));
	     // toute les quantités deviennent négligeables et doivent être supprimées
	     System.out.print("After one update of the environment, ");
	     System.out.print("the quantities of pheromone in the environment are: ");
	     System.out.println(env.getPheromonesQuantities() + "\n");
	     System.out.println("Finding pheromones around a given position : ");
	     ToricPosition antPosition = new ToricPosition(100., 100.);
	     Pheromone pher3 = new Pheromone(new ToricPosition(105., 105.), 1.0);
	     Pheromone pher4 = new Pheromone(new ToricPosition(95., 95.), 2.0);
	     // cette quantité est trop éloignée (ne doit pas être perçue) :
	     Pheromone pher5 = new Pheromone(new ToricPosition(500., 500.), 4.0);
	     env.addPheromone(pher3);
	     env.addPheromone(pher4);
	     env.addPheromone(pher5);
	     System.out.print("The quantities of pheromone in the environment are: ");
	     System.out.println(env.getPheromonesQuantities());
	     double[] pheromonesAroundPosition =
	     env.getPheromoneQuantitiesPerIntervalForAnt(antPosition, 0.,
	     new double[] {Math.toRadians(-180), Math.toRadians(-100),
	     Math.toRadians(-55), Math.toRadians(-25),
	     Math.toRadians(-10), Math.toRadians(0),
	     Math.toRadians(10), Math.toRadians(25),
	     Math.toRadians(55), Math.toRadians(100),
	     Math.toRadians(180)
	     });
	     System.out.println(Arrays.toString(pheromonesAroundPosition) + "\n");
	     System.out.print("After enough time, no pheromones should be left : ");
	     env.update(Time.fromSeconds(30.));
	     System.out.println(env.getPheromonesQuantities());
	     // Spread hormones tests
	     /*AntWorker antWorker = new AntWorker(new ToricPosition(), Uid.createUid());
	     env.addAnimal(antWorker);
	     env.update(Time.fromSeconds(0.5));
	     antWorker.spreadPheromones(env);
	     System.out.println("The antworker moved. Distance : " + new ToricPosition().toricDistance(antWorker.getPosition()));
	     double density = getConfig().getDouble(ANT_PHEROMONE_DENSITY);
	     double energy = getConfig().getDouble(ANT_PHEROMONE_ENERGY);
	     System.out.println("The density of pheromone is : "
	     + density + " and the energy : " + energy);
	     System.out.println("The antworker spreads pheromones");
	     System.out.println("The quantities of pheromone in the environment are now: ");
	     System.out.println(env.getPheromonesQuantities());*/
	     double[] I = {-180, -135, -90, -45, 0, 45, 90, 135, 180}; // angles
	 	 double[] P = {0.00, 0.01, 0.09, 0.15, 0.5, 0.15, 0.09, 0.01, 0.00};  // prob par angle
	 	 double[] Q = {20, 0, 0, 2, 0, 0, 10, 0, 5};  // quantities per sector
	 	 double[] Q1 = new double[Q.length];
	 	 System.out.print("Q' --> ");
		 for (int i = 0; i < Q1.length; ++i) {  // apply formula to obtain Q'
			Q1[i] = Math.round((1.0 / (1.0 + Math.exp(-1*(Q[i]-5)))) * 1000d) / 1000d;
			System.out.print(Q1[i] + " ");  // print Q'
		 }
		 System.out.println();
		 double s = 1.0;
		 double[] P1 = new double[P.length];
		 System.out.print("P' --> ");
		 for (int j = 0; j < P.length; ++j) {
			 double n = P[j] * Math.pow(Q1[j], 5);
			 s += n;
			 P1[j] = Math.round((((n / s) * 1000d) / 1000d));
			 System.out.print(P1[j] + " - ");  // print P' 
		 }
		 System.out.println();
		 System.out.println("S: " + s);
	  	 // double[] Q' = {1, 0.007 , 0.007 ,0.047 , 0.007, 0.007 , 0.993, 0.007 , 0.5};  // prob avec QZero=5 et beta=1
		 /* numerateurs P' : {0.00, 
		 1.34293736789941e-13
		 1.20864363110947e-12
		 3.59888473343695e-08
		 6.71468683949705e-12
		 2.01440605184912e-12
		 0.08702826299281
		 1.34293736789941e-13
		 0.00} */
	 	 // double[] P' = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.00} avec alpha=5
		 // quelques tests pour l'étape 13
		 System.out.println("\nA termite is added to an empty environment:");
		 ToricPosition termitePosition = new ToricPosition(20, 30);
		 Termite termit = new Termite(termitePosition);
		 ToricPosition positionBeforeUpdate = termitePosition;
		 System.out.println("Characteristics of the termite:");
		 System.out.println(termit);
		 env = new Environment();
		 env.addAnimal(termit);
		 env.update(Time.fromSeconds(1.));
		 // on teste si la termite est à nouveau capable de se déplacer
		 boolean hasMoved = !positionBeforeUpdate.equals(termit.getPosition());
		 System.out.print("The termite is now able to move : " );
		 System.out.println(hasMoved + "\n");
		 // on vérifie les probabilités de rotation
		 System.out.println("The rotation probabilities for the termite are:");
		 RotationProbability rotProbs = env.selectComputeRotationProbsDispatch(termit);
		 System.out.println("Angles :" + Arrays.toString(rotProbs.getAngles()));
		 System.out.println("Probabilities : " + Arrays.toString(rotProbs.getProbabilities()));
		 System.out.println();
		// On vérifie les tests sur les ennemis
		 Termite termit2 = new Termite(termitePosition);
		 env.addAnimal(termit2);
		 System.out.print("Is a termite the ennemy of another termite : ");
		 System.out.println(termit2.isEnemy(termit));
		 Anthill anthill1 = new Anthill(new ToricPosition(10, 20));
		 AntWorker worker1 = new AntWorker(new ToricPosition(22, 28), anthill.getAnthillId());
		 env.addAnimal(worker1);
		 System.out.print("Is a termite the ennemy of a worker ant : ");
		 System.out.println(termit2.isEnemy(worker1));
		 // faire la même choses pour les autres combinaisons possibles
		 // (soldates, fourmis d'une même foumilières etc.)
		 // On vérifie les méthodes permettant la détection d'ennemis:
		 System.out.print("Can the worker ant be seen by an ennemy :");
		 System.out.println(env.isVisibleFromEnemies(worker1));
		 System.out.println("Characteristics of the visible ennemies :");
		 System.out.println(env.getVisibleEnemiesForAnimal(worker1));
		 // à compléter avec d'autres configuration pour les distances
		 // On vérifie les forces et temps d'attaque
		 System.out.println((termit2.getMinAttackStrength()
		 == Context.getConfig().getInt(Config.TERMITE_MIN_STRENGTH)));
		 System.out.println((termit2.getMaxAttackStrength()
		 == Context.getConfig().getInt(Config.TERMITE_MAX_STRENGTH)));
		 System.out.println(termit2.getMaxAttackDuration().equals(
		 Context.getConfig().getTime(Config.TERMITE_ATTACK_DURATION)));
    }
}
