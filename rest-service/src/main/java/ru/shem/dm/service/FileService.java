package ru.shem.dm.service;

import org.springframework.core.io.FileSystemResource;
import ru.shem.dm.entity.AppDocument;
import ru.shem.dm.entity.AppPhoto;
import ru.shem.dm.entity.BinaryContent;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    //метод для преобразования массива байт в объект FileSystemResource для передачи его в теле HTTP запроса
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
