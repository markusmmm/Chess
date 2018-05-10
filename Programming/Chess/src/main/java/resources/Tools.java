package resources;

import java.util.*;

public class Tools<E> {
    public HashSet<E> cloneSet(HashSet<E> set) {
        HashSet<E> temp = new HashSet<>();

        for(E e : set)
            temp.add(e);

        return temp;
    }

    public HashSet<E> addAll(HashSet<E> set, E... elems) {
        HashSet<E> temp = cloneSet(set);

        for(E e : elems)
            temp.add(e);

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
