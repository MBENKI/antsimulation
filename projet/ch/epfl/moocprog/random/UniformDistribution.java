package ch.epfl.moocprog.random;

import java.util.Random;

/**
 * Classe utilitaire fournissant un moyen d'obtenir des valeurs
 * pseudo-al�atoires suivant une distribution uniforme
 */
public final class UniformDistribution {
    private static final Random random = new Random();

    // Emp�che l'instanciation de cette classe
    private UniformDistribution() {}

    /**
     * Retourne une valeur suivant une distribution uniforme
     * comprise entre min et max.
     *
     * @param min La borne inf�rieure de la distribution
     * @param max La borne sup�rieure de la distribution
     * @return Une valeur pseudo-al�atoire suivant une distribution
     *         uniforme avec les param�tres donn�s
     */
    public static double getValue(double min, double max) {
        if(max < min) {
            throw new IllegalArgumentException();
        }
        return random.nextDouble()*(max - min) + min;
    }
}
