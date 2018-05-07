package resources;

import management.ChessComputerMedium;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Console {
    // Suppresses all console calls, regardless of caller
    private static final boolean DO_PRINT = true;

    private static Set<Class> ignoredCallers = new HashSet<>(Arrays.asList(
            ChessComputerMedium.class
            //, AbstractChessPiece.class
            //, ChessPiece.class
    ));

    private static boolean doPrint() {
        if(!DO_PRINT) return false;

        String callerName = getCaller().getClassName();
        for(Class c : ignoredCallers)
            if (c.getCanonicalName().equals(callerName))
                return false;

        return true;
    }

    public static void suppressCallers(Class... c) {
        ignoredCallers.addAll(Arrays.asList(c));
    }
    public static void releaseCallers(Class... c) {
        ignoredCallers.removeAll(Arrays.asList(c));
    }
    public static boolean callerSuppressed(Class c) {
        return ignoredCallers.contains(c);
    }

    public static void print(String msg) {
        if(!doPrint()) return;
        System.out.print(ANSI.WHITE + msg + ANSI.RESET);
    }
    public static void print(Object obj) {
        print(obj.toString());
    }
    public static void println(String msg) {
        if(!doPrint()) return;
        System.out.print(ANSI.WHITE + msg + ANSI.RESET + "\n");
    }
    public static void println(Object obj) {
        print(obj.toString());
    }
    public static void println() {
        if(!doPrint()) return;
        System.out.println();
    }

    public static void printNotice(String msg) {
        if(!doPrint()) return;
        System.out.print(ANSI.CYAN + msg + ANSI.RESET  + "\n");
    }
    public static void printNotice(Object obj) {
        print(obj.toString());
    }
    public static void printSuccess(String msg) {
        if(!doPrint()) return;
        System.out.print(ANSI.GREEN + msg + ANSI.RESET + "\n");
    }
    public static void printSuccess(Object obj) {
        print(obj.toString());
    }
    public static void printWarning(String msg) {
        if(!doPrint()) return;
        System.out.print(ANSI.YELLOW + msg + ANSI.RESET + "\n");
    }
    public static void printWarning(Object obj) {
        print(obj.toString());
    }
    public static void printError(String msg) {
        if(!doPrint()) return;
        System.out.print(ANSI.RED + msg + ANSI.RESET + "\n");
    }
    public static void printError(Object obj) {
        print(obj.toString());
    }

    public static void printCustom(String msg, String ansi) {
        if(!doPrint()) return;
        System.out.print(ansi + msg + ANSI.RESET + "\n");
    }

    public static String indent(String msg, int amount) {
        StringBuilder str = new StringBuilder();
        String[] lines = msg.split("\n");

        for(String line : lines) {
            String indentation = "";
            for(int i = 0; i < amount; i++)
                indentation += "\t";

            str.append(indentation + line + "\n");
        }
        return str.toString();
    }
    public static String indent(String msg) {
        return indent(msg, 1);
    }

    public static void printCaller(int i) {
        if(!doPrint()) return;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.print("\t" + stackTraceElements[i] + "\n");
    }
    public static void printCaller() {
        if(!doPrint()) return;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for(StackTraceElement e : stackTraceElements)
            System.out.print("\t" + e + "\n");
    }
    public static void printFirstCaller() {
        printCaller(4);
    }

    public static StackTraceElement getCaller() {
        return Thread.currentThread().getStackTrace()[4];
    }
}
