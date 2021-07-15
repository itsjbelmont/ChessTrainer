package com.jackbelmont;

/*
    For printing logs but allows you to turn it on or off with this flag
*/
public class Logger {
    private static boolean enableConsoleLog = false;
    private boolean enableInstanceConsoleLog = false;
    Logger() { }

    public static Boolean enableConsoleLogging() {
        Boolean previousStatus = enableConsoleLog;
        enableConsoleLog = true;
        return previousStatus;
    }

    public static Boolean disableConsoleLogging() {
        Boolean previousStatus = enableConsoleLog;
        enableConsoleLog = false;
        return previousStatus;
    }

    public void enableInstanceConsoleLogging() {
        this.enableInstanceConsoleLog = true;
    }

    public void disableInstanceConsoleLogging() {
        this.enableInstanceConsoleLog = false;
    }

    public boolean getConsoleLoggingStatus() {return enableConsoleLog;}

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
