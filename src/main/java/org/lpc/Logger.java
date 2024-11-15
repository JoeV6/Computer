package org.lpc;

public class Logger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";

    public static void startColor(String color) {
        System.out.print(color);
    }

    public static void endColor() {
        System.out.print(ANSI_RESET);
    }

    public static void log(Object... message) {
        for (Object m : message) {
            System.out.print(m.toString());
        }
        System.out.println();
    }

    public static void logLnColor(String color, Object... message) {
        startColor(color);
        for (Object m : message) {
            System.out.print(m.toString());
        }
        endColor();
        System.out.println();
    }

    public static void logErr(Object... message) {
        for (Object m : message) {
            startColor(ANSI_RED);
            System.out.println(m.toString());
            endColor();
        }
        System.err.println();
    }

    public static void logLn(Object... message) {
        for (Object m : message) {
            System.out.println(m.toString());
        }
        System.out.println();
    }

    public static String strLine(String text, int totalLength) {
        if (text.length() >= totalLength) {
            return text + "\n";
        }

        int paddingLength = (totalLength - text.length() - 2) / 2;
        String padding = "-".repeat(paddingLength);

        return padding + " " + text + " " + padding +
                (paddingLength * 2 + text.length() + 2 < totalLength ? "-" : "") + "\n";
    }

    public static String strLine(int totalLength) {
        return "-".repeat(totalLength) + "\n";
    }


}
