package resources;

import java.util.HashMap;
import java.util.Set;

public class BiMap<L, R> {
    private HashMap<L, R> stRight = new HashMap<>();
    private HashMap<R, L> stLeft = new HashMap<>();

    public BiMap() {

    }

    public R getRight(L l) {
        return stRight.get(l);
    }
    public L getLeft(R r) {
        return stLeft.get(r);
    }

    public Set<L> leftKeys() {
        return stRight.keySet();
    }
    public Set<R> rightKeys() {
        return stLeft.keySet();
    }

    public void put(L l, R r) {
        stRight.put(l, r);
        stLeft.put(r, l);
    }
    public BiMap<L,R> putAll(L[] l, R[] r) {
        if(l.length != r.length) throw new IllegalArgumentException("Input arrays must be of same length");

        int n = l.length;
        for(int i = 0; i < n; i++)
            put(l[i], r[i]);

        return this;
    }
    public void remove(L l) {
        stRight.remove(l);
        stLeft.remove(stRight.get(l));
    }
}
