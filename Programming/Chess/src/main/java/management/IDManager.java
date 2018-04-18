package management;

import resources.Console;

public class IDManager {
    private int availID = 0;

    public int nextID() {
        String caller = Console.getCaller().getClassName();
        Console.printNotice("ID " + availID + " assigned to " + caller);
        return availID++;
    }
}
