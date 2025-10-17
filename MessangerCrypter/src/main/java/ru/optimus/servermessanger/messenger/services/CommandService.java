package ru.optimus.servermessanger.messenger.services;

import org.springframework.stereotype.Component;
import ru.optimus.servermessanger.messenger.commands.interfaces.Processor;
import ru.optimus.servermessanger.messenger.commands.interfaces.Required;
import ru.optimus.servermessanger.messenger.commands.processors.ContainerProcessors;

import java.util.Arrays;

@Component
public class CommandService {

    private final ContainerProcessors containerProcessors;

    public CommandService(ContainerProcessors containerProcessors) {
        this.containerProcessors = containerProcessors;
    }

    public boolean executeCommand(String inputLine) {
        if (inputLine == null || inputLine.isEmpty()) return false;

        String[] parts = inputLine.trim().split("\\s+");
        String cmd = parts[0];
        if (cmd.startsWith("/")) cmd = cmd.substring(1);
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        boolean found = false;

        for (Processor processor : containerProcessors.getProcessors()) {
            Required required = processor.getClass().getAnnotation(Required.class);

            if (required != null && cmd.equalsIgnoreCase(required.cmd())) {
                processor.process(cmd, args).call();
                found = true;
                break;
            }
        }

        return found;
    }
}
