package ru.shem.dm.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.shem.dm.entity.AppDocument;
import ru.shem.dm.entity.AppPhoto;
import ru.shem.dm.service.enums.LinkType;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);
}
