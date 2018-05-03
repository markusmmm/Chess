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
    public HashSet<E> mergeSets(HashSet<E>... collections) {
        HashSet<E> temp = new HashSet<>();

        for(Collection<E> c : collections)
            for(E e : c)
                temp.add(e);

        return temp;
    }

    public HashSet<E> addAll(HashSet<E> set, E... elems) {
        HashSet<E> temp = cloneSet(set);

        for(E e : elems)
            temp.add(e);

        return temp;
    }
}
