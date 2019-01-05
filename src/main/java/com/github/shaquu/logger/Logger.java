package com.github.shaquu.logger;

import java.util.Date;

public class Logger {

    public static boolean DEBUG = false;

    public void debug(String message) {
        if (DEBUG)
            System.out.println(new Date() + " | " + message);
    }

    public void log(String message) {
        System.out.println(new Date() + " | " + message);
    }

}
