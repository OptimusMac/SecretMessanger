package ru.optimus.servermessanger.messenger.context;

import org.springframework.web.reactive.socket.WebSocketSession;

public record User(
        WebSocketSession session,
        String name
) {

}
