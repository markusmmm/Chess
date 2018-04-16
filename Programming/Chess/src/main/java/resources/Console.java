package resources;

import main.GameBoard;

public class Console {
    private static final boolean DO_PRINT = true;

    private static Class[] ignoredCallers = new Class[] {
            //GameBoard.class
    };

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String ANSI_BLACK_BG = "\u001B[40m";
    private static final String ANSI_RED_BG = "\u001B[41m";
    private static final String ANSI_GREEN_BG = "\u001B[42m";
    private static final String ANSI_YELLOW_BG = "\u001B[43m";
    private static final String ANSI_BLUE_BG = "\u001B[44m";
    private static final String ANSI_PURPLE_BG = "\u001B[45m";
    private static final String ANSI_CYAN_BG = "\u001B[46m";
    private static final String ANSI_WHITE_BG = "\u001B[47m";

    private static boolean doPrint() {
        if(!DO_PRINT) return false;

        String callerName = getCaller().getClassName();
        for(Class c : ignoredCallers) {
            if (c.getCanonicalName().equals(callerName))
                return false;
        }

        return true;
    }

    public static void print(String msg) {
        if(!doPrint()) return;
        System.out.print(ANSI_WHITE + msg + ANSI_RESET);
    }
    public static void print(Object obj) {
        print(obj.toString());
    }
    public static void println(String msg) {
        if(!doPrint()) return;
        System.out.println(ANSI_WHITE + msg + ANSI_RESET);
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
        System.out.println(ANSI_CYAN + msg + ANSI_RESET);
    }
    public static void printNotice(Object obj) {
        print(obj.toString());
    }
    public static void printSuccess(String msg) {
        if(!doPrint()) return;
        System.out.println(ANSI_GREEN + msg + ANSI_RESET);
    }
    public static void printSuccess(Object obj) {
        print(obj.toString());
    }
    public static void printWarning(String msg) {
        if(!doPrint()) return;
        System.out.println(ANSI_YELLOW + msg + ANSI_RESET);
    }
    public static void printWarning(Object obj) {
        print(obj.toString());
    }
    public static void printError(String msg) {
        if(!doPrint()) return;
        System.out.println(ANSI_RED_BG + ANSI_WHITE + msg + ANSI_RESET);
    }
    public static void printError(Object obj) {
        print(obj.toString());
    }

    public static void printCaller(int i) {
        if(!doPrint()) return;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println("\t" + stackTraceElements[i]);
    }
    public static void printCaller() {
        if(!doPrint()) return;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for(StackTraceElement e : stackTraceElements)
            System.out.println("\t" + e);
    }

    private static StackTraceElement getCaller() {
        return Thread.currentThread().getStackTrace()[4];
    }
}
