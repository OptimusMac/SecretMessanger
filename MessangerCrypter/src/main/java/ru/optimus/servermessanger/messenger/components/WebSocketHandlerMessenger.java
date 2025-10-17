package ru.optimus.servermessanger.messenger.components;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.optimus.servermessanger.messenger.context.RoomContext;
import ru.optimus.servermessanger.messenger.context.RoomManager;
import ru.optimus.servermessanger.messenger.context.User;
import ru.optimus.servermessanger.messenger.services.CommandService;

import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Component
public class WebSocketHandlerMessenger implements WebSocketHandler {

    @Getter
    private WebSocketSession session;
    private final CommandService commandService;
    private RoomManager roomManager;

    public WebSocketHandlerMessenger(CommandService commandService, RoomManager roomManager) {
        this.commandService = commandService;
        this.roomManager = roomManager;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        this.session = session;
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(string -> {
                    boolean execute = commandService.executeCommand(string);
                    if (!execute) {
                        for (Map.Entry<String, RoomContext> rooms : roomManager.getRooms().entrySet()) {
                            for (User user : rooms.getValue().getSessions()) {
                                if (user.session().getId().equals(session.getId())) {
                                    rooms.getValue().broadcast(user.name(), string, session, this);
                                }
                            }
                        }
                    }
                }).then();
    }



    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            session.send(Mono.just(session.textMessage(message)))
                    .doOnError(err -> System.out.println("Send error: " + err.getMessage()))
                    .subscribe();
        }
    }

    public void sendMessageToSession(WebSocketSession targetSession, String message) {
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.send(Mono.just(targetSession.textMessage(message)))
                    .doOnError(err -> System.out.println("Send error: " + err.getMessage()))
                    .subscribe();
        }
    }

    public void sendExecute(int opcode, Map<String, String> values) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"opcode\":").append(opcode);

            if (values != null && !values.isEmpty()) {
                sb.append(",\"data\":{");
                boolean first = true;
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    if (!first) sb.append(",");
                    first = false;

                    String key = escapeJson(entry.getKey());
                    String value = entry.getValue();

                    value = Base64.getEncoder().encodeToString(value.getBytes());

                    sb.append("\"").append(key).append("\":");
                    sb.append("\"").append(value).append("\"");
                }
                sb.append("}");
            }

            sb.append("}");

            sendMessage(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
