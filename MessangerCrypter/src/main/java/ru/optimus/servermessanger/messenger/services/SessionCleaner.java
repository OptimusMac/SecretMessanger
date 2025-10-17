package ru.optimus.servermessanger.messenger.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.optimus.servermessanger.messenger.context.RoomManager;

@Component
public class SessionCleaner {

    private final RoomManager roomManager;

    public SessionCleaner(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Scheduled(fixedRate = 1000)
    public void cleanupClosedSessions() {
        roomManager.getRooms().values().forEach(room ->
                room.getSessions().removeIf(user -> !user.session().isOpen())
        );
    }
}
