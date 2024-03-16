package ru.shem.dm.service.impl;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.shem.dm.CryptoTool;
import ru.shem.dm.configuration.exceptions.UploadFileException;
import ru.shem.dm.dao.AppDocumentDAO;
import ru.shem.dm.dao.AppPhotoDAO;
import ru.shem.dm.dao.BinaryContentDAO;
import ru.shem.dm.entity.AppDocument;
import ru.shem.dm.entity.AppPhoto;
import ru.shem.dm.entity.BinaryContent;
import ru.shem.dm.service.FileService;
import ru.shem.dm.service.enums.LinkType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

//Сервис получает telegram Message и выполняет все необходимые действия для скачивания файла,
// и сохраняет его в БД
@Log4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private  final AppPhotoDAO appPhotoDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK){
            JSONObject jsonObject = new JSONObject(response.getBody());
            String filePath = String.valueOf(jsonObject.getJSONObject("result").getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent binaryContent = BinaryContent.builder()
                    .fileAsArrayOfBytes(fileInByte)
                    .build();

            BinaryContent persistentBinaryContent = binaryContentDAO.save(binaryContent);
            Document teleramDoc = telegramMessage.getDocument();
            AppDocument appDocument = buildTransAppDoc(teleramDoc, persistentBinaryContent);
            return  appDocumentDAO.save(appDocument);
        }else {
            throw new UploadFileException("Bad response from telegram service " + response);
        }

    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSize = telegramMessage.getPhoto().size();
        var photoIndex = photoSize > 1 ? telegramMessage.getPhoto().size() - 1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode() == HttpStatus.OK){
            JSONObject jsonObject = new JSONObject(response.getBody());
            String filePath = String.valueOf(jsonObject.getJSONObject("result").getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent binaryContent = BinaryContent.builder()
                    .fileAsArrayOfBytes(fileInByte)
                    .build();

            BinaryContent persistentBinaryContent = binaryContentDAO.save(binaryContent);
            AppPhoto appPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(appPhoto);
        }else {
            throw new UploadFileException("Bad response from telegram service " + response);
        }
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token,fileId
        );
    }

    private byte[] downloadFile(String filePath) {
        String fulUri = fileStorageUri.replace("{token}", token).replace("{filePath}", filePath);
        URL urlObj = null;

        try{
            urlObj = new URL(fulUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        try(InputStream is = urlObj.openStream()){
            return is.readAllBytes(); //TODO readAllBytes не лучшее решение
        }catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }

    }

    private AppDocument buildTransAppDoc(Document teleramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(teleramDoc.getFileId())
                .docName(teleramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(teleramDoc.getMimeType())
                .fileSize(teleramDoc.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }
}
