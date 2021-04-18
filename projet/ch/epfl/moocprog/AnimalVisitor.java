package ch.epfl.moocprog;

/**
 * Interface permettant à un RenderingMedia de « visiter »
 * un animal et de d�terminer son type dynamique.
 *
 */
public interface AnimalVisitor {
    /**
     * Indique que l'animal visit� est un {@link AntWorker}.
     *
     * @param antWorker L'instance de la fourmi ouvri�re
     * @param s le support de rendu
     */
    void visit(AntWorker antWorker, RenderingMedia s);

    /**
     * Indique que l'animal visit� est un {@link AntSoldier}.
     * Retourne le r�sultat de la visite de la fourmi soldate.
     *
     * @param antSoldier L'instance de la fourmi soldate
     * @param s le support de rendu
     */
    void visit(AntSoldier antSoldier, RenderingMedia s);

    /**
     * Indique que l'animal visit� est un {@link Termite}.
     * Retourne le r�sultat de la visite de la termite.
     *
     * @param termite L'instance de la termite
     * @param s le support de rendu
     */
    void visit(Termite termite, RenderingMedia s);
}
