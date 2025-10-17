package ru.optimus.servermessanger.messenger.context;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class RoomManager {

    private final Map<String, RoomContext> rooms = new ConcurrentHashMap<>();

    public void addRoom(RoomContext roomContext){
        rooms.put(roomContext.getRoomName(), roomContext);
    }

    public RoomContext getRoom(String roomName) {
        return rooms.get(roomName);
    }

    public boolean hasRoom(String name){
        return rooms.containsKey(name);
    }
}
