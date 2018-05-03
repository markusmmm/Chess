package resources;

public abstract class Tuple<A,B> {
    protected final A fst;
    protected final B snd;

    protected Tuple(A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
    }
}
