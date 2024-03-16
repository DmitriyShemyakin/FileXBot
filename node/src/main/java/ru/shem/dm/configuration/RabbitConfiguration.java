package ru.shem.dm.configuration;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Класс для интеграции с RabbitMQ
@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter(){//преобразование update в json
        return new Jackson2JsonMessageConverter();
    }

}
