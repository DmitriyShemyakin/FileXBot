package ru.shem.dm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shem.dm.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
