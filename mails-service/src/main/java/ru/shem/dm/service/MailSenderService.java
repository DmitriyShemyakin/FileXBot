package ru.shem.dm.service;

import ru.shem.dm.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
