package ru.optimus.client.messengerclient.utils;

import lombok.Getter;

import java.util.Map;

public class OpcodeHandler {
    private int opcode;
    @Getter
    private Map<String, String> data;

    public OpcodeHandler() {}

    public OpcodeHandler(int opcode, Map<String, String> data) {
        this.opcode = opcode;
        this.data = data;
    }

    public int getOpcode() {
        return opcode;
    }

}
