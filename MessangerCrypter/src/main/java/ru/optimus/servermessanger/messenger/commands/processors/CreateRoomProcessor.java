package ru.optimus.servermessanger.messenger.commands.processors;

import org.springframework.stereotype.Component;
import ru.optimus.servermessanger.messenger.commands.anno.ProcessorRegistered;
import ru.optimus.servermessanger.messenger.commands.interfaces.Processor;
import ru.optimus.servermessanger.messenger.commands.interfaces.Required;
import ru.optimus.servermessanger.messenger.components.WebSocketHandlerMessenger;
import ru.optimus.servermessanger.messenger.context.RoomContext;
import ru.optimus.servermessanger.messenger.context.RoomManager;

import java.util.Map;
import java.util.Random;

@Required(cmd = "createroom")
@ProcessorRegistered
@Component
public class CreateRoomProcessor implements Processor {

    private WebSocketHandlerMessenger socket;
    private RoomManager roomManager;

    public CreateRoomProcessor(WebSocketHandlerMessenger socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }


    @Override
    public Callback process(String command, String[] args) {
        return () -> {
            if (args.length == 0) {
                socket.sendMessage("Usage: /create_room <room_name>");
                return;

            }

            StringBuilder sb = new StringBuilder();

            for (String arg : args) {
                sb.append(arg).append(" ");
            }
            byte[] crypt = new byte[16];
            new Random().nextBytes(crypt);

            RoomContext roomContext = new RoomContext(args[0], crypt);
            roomManager.addRoom(roomContext);

            socket.sendMessage("room is created as %s".formatted(sb.toString().trim()));

            socket.sendExecute(0x1, Map.of("key", new String(crypt)));
        };
    }
}
