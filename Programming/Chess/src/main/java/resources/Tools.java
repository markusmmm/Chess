package resources;

import java.util.*;

public class Tools<E> {
    public E randomElem(Set<E> set) {
        if(set.size() == 0) return null;

        Random rand = new Random();
        int r = rand.nextInt(set.size());

        int i = 0;
        for(E e : set)
            if(i++ == r) return e;
        return null;
    }


    public HashSet<E> cloneSet(HashSet<E> set) {
        HashSet<E> temp = new HashSet<>();
        temp.addAll(set);
        return temp;
    }

    public Set<E> merge(Set<E> set, Set<E> other) {
        set.addAll(other);
        return set;
    }

    public HashSet<E> addAll(HashSet<E> set, E... elems) {
        HashSet<E> temp = cloneSet(set);
        temp.addAll(Arrays.asList(elems));
        return temp;
    }

    public static int allianceDir(Alliance alliance) {
        if(alliance == Alliance.BLACK) return 1;
        if(alliance == Alliance.WHITE) return -1;
        return 0;
    }

    public static int firstRankOfAlliance(Alliance alliance) {
        if(alliance == Alliance.BLACK) return 0;
        if(alliance == Alliance.WHITE) return 7;
        return -1;
    }

    public static int sign(double d) {
        return d < 0 ? -1 : d > 0 ? 1 : 0;
    }
}
