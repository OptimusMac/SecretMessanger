package ru.optimus.client.messengerclient.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;
import ru.optimus.client.messengerclient.utils.CryptoWrapper;
import ru.optimus.client.messengerclient.utils.GlobalKeyReceiver;
import ru.optimus.client.messengerclient.utils.OpcodeHandler;
import ru.optimus.client.messengerclient.utils.TwofishCrypto;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

@Component
public class WebSocketClientMessenger {

    private final ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
    private WebSocketSession session;
    private final TwofishCrypto twofishCrypto;
    private final CryptoWrapper cryptoWrapper;

    private String host = "localhost";
    private int port = 8080;

    public WebSocketClientMessenger(TwofishCrypto twofishCrypto, CryptoWrapper cryptoWrapper) {
        this.twofishCrypto = twofishCrypto;
        this.cryptoWrapper = cryptoWrapper;
    }

    public void setHostAndPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getUri() {
        if (host == null || host.isBlank() || port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Host or port is invalid");
        }
        return "ws://" + host + ":" + port + "/ws";
    }

    public void connect() {
        String uri = getUri();
        client.execute(URI.create(uri), this::handleSession)
                .doOnError(err -> System.err.println("Connection error: " + err.getMessage()))
                .subscribe();
    }

    private Mono<Void> handleSession(WebSocketSession session) {
        this.session = session;

        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(msg -> {
                    msg = msg.trim();

                    if (msg.startsWith("{") && msg.endsWith("}")) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            OpcodeHandler handler = mapper.readValue(msg, OpcodeHandler.class);

                            if (handler.getOpcode() != 0) {
                                switch (handler.getOpcode()) {
                                    case 0x1 -> {
                                        String base64Key = handler.getData().get("key");
                                        if (base64Key != null) {
                                            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
                                            GlobalKeyReceiver.setKey(decodedKey);
                                        }
                                    }
                                    case 0x2 -> {
                                        GlobalKeyReceiver.inSession = true;
                                    }
                                    default -> System.out.println("[SYSTEM] Unknown opcode: " + handler.getOpcode());
                                }
                                return;
                            }

                            printMaybeDecrypted(msg);

                        } catch (JsonProcessingException e) {
                            printMaybeDecrypted(msg);
                        }
                    } else {
                        printMaybeDecrypted(msg);
                    }
                })
                .then();
    }

    private void printMaybeDecrypted(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(json, Map.class);

            String username = (String) map.get("username");
            String msg = (String) map.get("message");

            if (GlobalKeyReceiver.inSession && GlobalKeyReceiver.getKey() != null && msg != null) {
                try {
                    byte[] decoded = Base64.getDecoder().decode(msg);
                    byte[] decrypted = TwofishCrypto.decrypt(decoded, GlobalKeyReceiver.getKey());
                    System.out.println("[" + username + "] " + new String(decrypted));
                } catch (IllegalArgumentException e) {
                    System.out.println("[" + username + "] " + msg);
                } catch (Exception e) {
                    System.out.println("[ERROR] Cannot decrypt message: " + msg);
                }
            } else {
                System.out.println("[" + username + "] " + msg);
            }

        } catch (Exception e) {
            System.out.println(json);
        }
    }

    public void sendMessage(String message) {
        if (session == null || !session.isOpen()) {
            System.out.println("Session is not ready yet!");
            return;
        }

        try {
            if (message.startsWith("/")) {
                session.send(Mono.just(session.textMessage(message))).subscribe();
                return;
            }

            String encryptedBase64 = GlobalKeyReceiver.encrypt(message.getBytes(), cryptoWrapper, twofishCrypto);

            session.send(Mono.just(session.textMessage(encryptedBase64))).subscribe();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
