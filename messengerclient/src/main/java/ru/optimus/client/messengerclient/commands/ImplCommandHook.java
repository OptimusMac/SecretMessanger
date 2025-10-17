package ru.optimus.client.messengerclient.commands;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.optimus.client.messengerclient.commands.interfaces.CommandHook;
import ru.optimus.client.messengerclient.components.WebSocketClientMessenger;
import ru.optimus.client.messengerclient.utils.GlobalKeyReceiver;

import java.io.InputStream;
import java.util.Scanner;


@Component
@AllArgsConstructor
public class ImplCommandHook implements CommandHook {

    private final WebSocketClientMessenger socket;

    @Override
    public void parse(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("/") || GlobalKeyReceiver.inSession) {
                socket.sendMessage(line);
            }
        }
    }
}
