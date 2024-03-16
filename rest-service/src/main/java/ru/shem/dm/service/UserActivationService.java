package ru.shem.dm.service;

import ru.shem.dm.CryptoTool;

public interface UserActivationService {
    boolean activation(String cryptoUserId);
}
