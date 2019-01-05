package com.github.shaquu.logger;

import java.util.Date;

public class Logger {

    public void log(String message) {
        System.out.println(new Date() + " | " + message);
    }

}
