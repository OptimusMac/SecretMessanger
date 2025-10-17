package ru.optimus.servermessanger.messenger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.optimus.servermessanger.messenger.context.RoomManager;

@Configuration
public class ConfigBeans {

    @Bean
    public RoomManager roomManager(){
        return new RoomManager();
    }
}
