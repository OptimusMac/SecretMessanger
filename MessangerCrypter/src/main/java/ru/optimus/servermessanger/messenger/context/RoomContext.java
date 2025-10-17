package ru.optimus.servermessanger.messenger.context;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import ru.optimus.servermessanger.messenger.components.WebSocketHandlerMessenger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RoomContext {

    private final String roomName;
    private final Set<User> sessions = ConcurrentHashMap.newKeySet();
    private final byte[] crypt;

    public RoomContext(String roomName, byte[] crypt) {
        this.roomName = roomName;
        this.crypt = crypt;
    }

    public void addSession(User user) {
        sessions.add(user);
    }

    public void removeSession(User user) {
        sessions.remove(user);
    }


    /**
     * Broadcast сообщения всем в комнате, кроме отправителя.
     * Сообщение приходит в формате JSON: {"username":"имя","message":"шифрованное сообщение Base64"}
     */
    public void broadcast(String username, String message, WebSocketSession sender, WebSocketHandlerMessenger messenger) {
        String payload = String.format("{\"username\":\"%s\",\"message\":\"%s\"}", escapeJson(username), escapeJson(message));

        for (User user : sessions) {
            WebSocketSession session = user.session();
            if (!session.getId().equals(sender.getId()) && session.isOpen()) {
                messenger.sendMessageToSession(session, payload);
            }
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
