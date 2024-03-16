package ru.shem.dm.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.shem.dm.service.UpdateProducer;
import ru.shem.dm.utils.MessageUtils;

import static ru.shem.dm.model.RabbitQueue.*;

//Класс распределяет входящие сообщение от бота
@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer){
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }
    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update){ //метод для обработки входящих сообщений
        if(update == null){
            log.error("Receiver update is null");
            return;
        }

        if(update.getMessage() != null){
            distributeMessageByTye(update);

        }else {
            log.error("Receiver unsupported message type " + update);
        }

    }
    private void distributeMessageByTye(Update update){
        Message message = update.getMessage();
        if(message.getText() != null){
            processTextMessage(update);
        } else if (message.getDocument() != null){
            processDocMessage(update);
        } else if (message.getPhoto() != null){
            processPhotoMessage(update);
        } else {
            serUnsupportedMessageTypeView(update);
        }

    }

    private void processTextMessage(Update update){
        updateProducer.producer(TEXT_MESSAGE_UPDATE, update);

    }

    private void processDocMessage(Update update){
        updateProducer.producer(DOC_MESSAGE_UPDATE, update);
        setFileReceivedView(update);

    }
    private void processPhotoMessage(Update update){
        updateProducer.producer(PHOTO_MESSAGE_UPDATE, update);
        setFileReceivedView(update);

    }

    private void setFileReceivedView(Update update) {
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update,
                "Файл получен! Обрабатывается...");
        setView(sendMessage);

    }

    private void serUnsupportedMessageTypeView(Update update){
        SendMessage sendMessage = messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип данных!");
        setView(sendMessage);

    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }


}
