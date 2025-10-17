package ru.optimus.client.messengerclient.commands.interfaces;

import java.io.InputStream;

public interface CommandHook {


    void parse(InputStream inputStream);
}
