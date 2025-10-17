package ru.optimus.servermessanger.messenger.commands.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.optimus.servermessanger.messenger.commands.anno.ProcessorRegistered;
import ru.optimus.servermessanger.messenger.commands.interfaces.Processor;
import ru.optimus.servermessanger.messenger.commands.interfaces.Required;
import ru.optimus.servermessanger.messenger.components.WebSocketHandlerMessenger;
import ru.optimus.servermessanger.messenger.context.RoomManager;
import ru.optimus.servermessanger.messenger.context.User;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Required(cmd = "connect")
@Component
@ProcessorRegistered
@RequiredArgsConstructor
public class ConnectRoomProcessor implements Processor {

    private final WebSocketHandlerMessenger socket;
    private final RoomManager roomManager;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Callback process(String command, String[] args) {
        return () -> {
            if (args.length == 0) {
                socket.sendMessage("Usage: /connect <roomName>");
                return;
            }

            String roomName = args[0];
            Map<String, String> flags = parseFlags(args);

            if (!roomManager.hasRoom(roomName)){
                socket.sendMessage("Unknown room %s".formatted(roomName));
                return;
            }

            String name;
            if (flags.containsKey("u")) {
                name = flags.get("u");
            }else{
                name = generateLowercaseString(8);
            }

            var room = roomManager.getRoom(roomName);
            room.addSession(new User(socket.getSession(), name));
            socket.sendExecute(0x2, Map.of("username", name));
            socket.sendExecute(0x1, Map.of("key", new String(room.getCrypt())));
            socket.sendMessage("Joined room: " + roomName);
        };
    }

    private Map<String, String> parseFlags(String[] args) {
        Map<String, String> flags = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-") && i + 1 < args.length) {
                String key = arg.substring(1);
                String value = args[i + 1];
                flags.put(key, value);
                i++;
            }
        }
        return flags;
    }

    public static String generateLowercaseString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
}
