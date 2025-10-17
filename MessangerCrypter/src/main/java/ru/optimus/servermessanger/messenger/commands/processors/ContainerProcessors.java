package ru.optimus.servermessanger.messenger.commands.processors;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.optimus.servermessanger.messenger.commands.interfaces.Processor;
import ru.optimus.servermessanger.messenger.components.WebSocketHandlerMessenger;
import ru.optimus.servermessanger.messenger.context.RoomManager;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ContainerProcessors {

    @Getter
    private final Collection<Processor> processors = new ArrayList<>();
    private final RoomManager roomManager;
    private final WebSocketHandlerMessenger socket;

    public ContainerProcessors(@Lazy WebSocketHandlerMessenger socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
        register();
    }

    @PostConstruct
    private void register() {
        processors.add(new CreateRoomProcessor(socket, roomManager));
        processors.add(new ConnectRoomProcessor(socket, roomManager));

    }
}
