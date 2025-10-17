package ru.optimus.client.messengerclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.optimus.client.messengerclient.utils.CryptoWrapper;
import ru.optimus.client.messengerclient.utils.TwofishCrypto;

@Configuration
public class ClientConfig {


    @Bean
    public TwofishCrypto twofishCrypto(){
        return new TwofishCrypto();
    }

    @Bean
    public CryptoWrapper cryptoWrapper(){
        return new CryptoWrapper(twofishCrypto());
    }
}
