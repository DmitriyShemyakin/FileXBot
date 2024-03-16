package ru.shem.dm.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shem.dm.service.ConsumerService;
import ru.shem.dm.service.MainService;

import static ru.shem.dm.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    public ConsumerServiceImpl(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumerTextMessageUpdate(Update update) {
        log.debug("NODE: text message is received!");
        mainService.processTextMessage(update);

    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumerDocMessageUpdate(Update update) {
        log.debug("NODE: text message is received!");
        mainService.processDocMessage(update);

    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumerPhotoMessageUpdate(Update update) {
        log.debug("NODE: text message is received!");
        mainService.processPhotoMessage(update);

    }
}
