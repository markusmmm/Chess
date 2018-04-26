package resources;

import java.util.*;

public class Tools<E> {
    public static final Set<Vector2> unitVectors = new HashSet<>(Arrays.asList(
            new Vector2(-1, -1), new Vector2(0, -1), new Vector2(1, -1),
            new Vector2(-1, 0),                            new Vector2(1, 0),
            new Vector2(-1, 1),  new Vector2(0, 1),  new Vector2(1, 1)));

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
