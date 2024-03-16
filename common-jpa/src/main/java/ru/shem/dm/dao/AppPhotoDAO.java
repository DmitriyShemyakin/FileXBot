package ru.shem.dm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shem.dm.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
