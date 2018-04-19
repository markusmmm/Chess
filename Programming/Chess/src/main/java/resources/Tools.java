package resources;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Tools<E> {
    public Set<E> cloneSet(Set<E> set) {
        Set<E> temp = new HashSet();

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
}
