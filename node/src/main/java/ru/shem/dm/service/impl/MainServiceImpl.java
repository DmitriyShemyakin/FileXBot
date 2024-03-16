package ru.shem.dm.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.shem.dm.RawDataDAO;
import ru.shem.dm.RawData;
import ru.shem.dm.configuration.exceptions.UploadFileException;
import ru.shem.dm.dao.AppUserDAO;
import ru.shem.dm.entity.AppDocument;
import ru.shem.dm.entity.AppPhoto;
import ru.shem.dm.entity.AppUser;
import ru.shem.dm.service.AppUserService;
import ru.shem.dm.service.FileService;
import ru.shem.dm.service.MainService;
import ru.shem.dm.service.ProducerService;
import ru.shem.dm.service.enums.LinkType;
import ru.shem.dm.service.enums.ServiceCommands;

import static ru.shem.dm.entity.UserState.BASIC_STATE;
import static ru.shem.dm.entity.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.shem.dm.service.enums.ServiceCommands.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserState();
        var text = update.getMessage().getText();
        var output = "";


        var serviceCommand = ServiceCommands.fromValue(text);
        if(CANCEL.equals(serviceCommand)){
            output = cancelProcess(appUser);
        }else if(BASIC_STATE.equals(userState)){
            output = producerServiceCommand(appUser, text);
        }else if(WAIT_FOR_EMAIL_STATE.equals(userState)){
            output = appUserService.setEmail(appUser, text);
        }else {
            log.error("Unknown user tate: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if(isNotAllowToSenContext(chatId, appUser)){
            return;
        }
        try{
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружено! Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        }catch (UploadFileException e){
            log.error(e);
            var answer = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(answer, chatId);

        }
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if(isNotAllowToSenContext(chatId, appUser)){
            return;
        }

        try {
            AppDocument appDocument = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(appDocument.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен! Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);

        }catch (UploadFileException e){
            log.error(e);
            var answer = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(answer, chatId);
        }
    }

    private boolean isNotAllowToSenContext(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();
        if(!appUser.getIsActive()){
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if(!BASIC_STATE.equals(userState)){
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String producerServiceCommand(AppUser appUser, String cmd) {
        var serviceCommand = ServiceCommands.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команда:\n"
                + "/cancel - отмена выполнения текущей команды;\n"
                + "/registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
        var optional = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if(optional.isEmpty()){
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .userState(BASIC_STATE)
                    .build();

            return appUserDAO.save(transientAppUser);

        }
        return optional.get();

    }

    private void saveRawData(Update update){
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
