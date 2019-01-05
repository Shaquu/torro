package com.github.shaquu.controller;

public interface Command {
    String description();

    void execute();
}
