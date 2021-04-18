package ch.epfl.moocprog;

import java.util.Objects;

/**
 * Simple classe repr�sentant un identifieur unique
 */
public final class Uid {
    private static long counter = 0;
    private final long id;

    // Emp�che l'instanciation directe de Uid car r�vellerait
    // la repr�sentation interne de cette classe.
    private Uid(long id) {
        this.id = id;
    }

    /**
     * Cr�e un nouveau identifiant unique.
     *
     * @return Un nouveau identifiant unique
     */
    public static Uid createUid() {
        return new Uid(counter++);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Uid) {
            Uid that = (Uid) o;
            return that.id == this.id;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
