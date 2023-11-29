package eu.brnt.qualibration.model;

import java.util.Objects;

/**
 * Represent a couple of things that can be of different types.
 */
public class Couple<T1, T2> {

    private final T1 first;
    private final T2 second;

    private int hash = 0;

    public Couple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            long bits = 7L;
            bits = 31L * bits + first.hashCode();
            bits = 31L * bits + second.hashCode();
            hash = (int) (bits ^ (bits >> 32));
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Couple couple = (Couple) o;
        return Objects.equals(couple.first, first) && Objects.equals(couple.second, second);
    }

    @Override
    public String toString() {
        return "(" + first.toString()
                + "," + second.toString()
                + ")";
    }
}
