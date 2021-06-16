package com.jackbelmont;

/*
    For printing logs but allows you to turn it on or off with this flag
*/
public class Logger {
    private static boolean enableConsoleLog = false;
    private boolean enableInstanceConsoleLog = false;
    Logger() { }

    public static void enableConsoleLogging() {
        enableConsoleLog = true;
    }

    public static void disableConsoleLogging() {
        enableConsoleLog = false;
    }

    public void enableInstanceConsoleLogging() {
        this.enableInstanceConsoleLog = true;
    }

    public void disableInstanceConsoleLogging() {
        this.enableInstanceConsoleLog = false;
    }

    public void iLogStr(String str) {
        if (enableInstanceConsoleLog) {
            System.out.println(str);
        }
    }

    public static void logStr(String str) {
        if (enableConsoleLog) {
            System.out.println(str);
        }
    }
}
