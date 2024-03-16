package ru.shem.dm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.shem.dm.CryptoTool;

@Configuration
public class NodeConfiguration {
    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool getCryptoTool(){
        return new CryptoTool(salt);
    }
}
