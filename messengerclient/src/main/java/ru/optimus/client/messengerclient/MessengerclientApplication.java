package ru.optimus.client.messengerclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.optimus.client.messengerclient.commands.ImplCommandHook;
import ru.optimus.client.messengerclient.components.WebSocketClientMessenger;

@SpringBootApplication
public class MessengerclientApplication {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--host".equals(arg) && i + 1 < args.length) {
                host = args[i + 1];
                i++;
            } else if ("--port".equals(arg) && i + 1 < args.length) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port: " + args[i + 1] + ", using default 8080");
                }
            }
        }

        ConfigurableApplicationContext context = SpringApplication.run(MessengerclientApplication.class, args);
        WebSocketClientMessenger client = context.getBean(WebSocketClientMessenger.class);
        client.setHostAndPort(host, port);

        try {
            String uri = client.getUri();
            System.out.println("Connecting to: " + uri);
            client.connect();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid host or port: " + e.getMessage());
            return;
        }

        ImplCommandHook hook = context.getBean(ImplCommandHook.class);
        hook.parse(System.in);
    }
}

