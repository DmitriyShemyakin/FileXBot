package ru.shem.dm.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.shem.dm.model.RabbitQueue.*;

//Класс для интеграции с RabbitMQ
@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter(){//преобразование update в json
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue(){//преобразование update в json
        return new Queue(TEXT_MESSAGE_UPDATE);
    }
    @Bean
    public Queue docMessageQueue(){//преобразование update в json
        return new Queue(DOC_MESSAGE_UPDATE);
    }
    @Bean
    public Queue photoMessageQueue(){//преобразование update в json
        return new Queue(PHOTO_MESSAGE_UPDATE);
    }
    @Bean
    public Queue answerMessageQueue(){//преобразование update в json
        return new Queue(ANSWER_MESSAGE);
    }
}
