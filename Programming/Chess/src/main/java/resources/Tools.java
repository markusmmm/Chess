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

    @SafeVarargs
    public final HashSet<E> merge(HashSet<E> set, E... elems) {
        HashSet<E> temp = (HashSet<E>)set.clone();
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
