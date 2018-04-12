package resources;

public class Console {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BG = "\u001B[40m";
    public static final String ANSI_RED_BG = "\u001B[41m";
    public static final String ANSI_GREEN_BG = "\u001B[42m";
    public static final String ANSI_YELLOW_BG = "\u001B[43m";
    public static final String ANSI_BLUE_BG = "\u001B[44m";
    public static final String ANSI_PURPLE_BG = "\u001B[45m";
    public static final String ANSI_CYAN_BG = "\u001B[46m";
    public static final String ANSI_WHITE_BG = "\u001B[47m";

    public static void printNotice(String msg) {
        System.out.println(ANSI_CYAN + msg + ANSI_RESET);
    }
    public static void printSuccess(String msg) {
        System.out.println(ANSI_GREEN + msg + ANSI_RESET);
    }
    public static void printWarning(String msg) {
        System.out.println(ANSI_YELLOW + msg + ANSI_RESET);
    }
    public static void printError(String msg) {
        System.out.println(ANSI_RED_BG + ANSI_WHITE + msg + ANSI_RESET);
    }
}
