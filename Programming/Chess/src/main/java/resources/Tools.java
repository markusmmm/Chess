package resources;

import java.util.*;

public class Tools<E> {
    public HashSet<E> cloneSet(HashSet<E> set) {
        HashSet<E> temp = new HashSet<>();

        for(E e : set)
            temp.add(e);

        return temp;
    }
    public SortedSet<E> cloneSortedSet(SortedSet<E> set) {
        SortedSet<E> temp = new TreeSet<>();

        for(E e : set)
            temp.add(e);

        return temp;
    }
    public HashSet<E> mergeSets(HashSet<E>... sets) {
        HashSet<E> temp = new HashSet<>();

        for(HashSet<E> s : sets)
            temp.addAll(s);

        return temp;
    }

    public E randomElem(Set<E> set) {
        if(set.size() == 0) return null;

        Random rand = new Random();
        int r = rand.nextInt(set.size());

        int i = 0;
        for(E e : set)
            if(i++ == r) return e;
        return null;
    }

    public HashSet<E> addAll(HashSet<E> set, E... elems) {
        HashSet<E> temp = cloneSet(set);

        for(E e : elems)
            temp.add(e);

        return temp;
    }

    public boolean containsAny(HashSet<E> set, HashSet<E> other) {
        for(E e : other)
            if(set.contains(e)) return true;
        return false;
    }

    public static int allianceDir(Alliance alliance) {
        if(alliance == Alliance.BLACK) return 1;
        if(alliance == Alliance.WHITE) return -1;
        return 0;
    }
}
