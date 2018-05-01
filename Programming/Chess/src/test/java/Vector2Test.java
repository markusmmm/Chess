import org.junit.Test;
import resources.Console;
import resources.Vector2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Vector2Test {
    @Test
    public void addTest() {
        Vector2 u = new Vector2(1,2);
        assertTrue(u.add(new Vector2(2,3)).equals(new Vector2(3, 5)));
    }

    @Test
    public void subTest() {
        Vector2 u = new Vector2(1,2);
        assertTrue(u.sub(new Vector2(2,3)).equals(new Vector2(-1, -1)));
    }

    @Test
    public void multTest() {
        Vector2 u = new Vector2(1,2);

        assertTrue(u.mult(2).equals(new Vector2(2,4)));
        assertTrue(u.mult(new Vector2(3,2)).equals(new Vector2(3,4)));
    }

    @Test
    public void isParallelToTest() {
        Vector2 u = new Vector2(1,2);
        Vector2 v1 = u.mult(2);
        Vector2 v2 = u.mult(-1);
        Vector2 v3 = u.mult(new Vector2(2,3));

        assertTrue(u.isParallelTo(v1));
        assertTrue(u.isParallelTo(v2));
        assertFalse(u.isParallelTo(v3));
    }

    @Test
    public void stepTowardTest() {
        Vector2 a = Vector2.E.mult(2);

        assertTrue(new Vector2(1,0).stepToward(a).equals(new Vector2(2,0)));
        assertTrue(new Vector2(3,2).stepToward(a).equals(new Vector2(2,1)));
    }
}
