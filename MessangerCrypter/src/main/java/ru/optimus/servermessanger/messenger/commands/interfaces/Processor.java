package ru.optimus.servermessanger.messenger.commands.interfaces;

import org.springframework.stereotype.Component;

@FunctionalInterface
public interface Processor {
    Callback process(String command, String[] args);

    @FunctionalInterface
    interface Callback {
        void call();
    }
}
