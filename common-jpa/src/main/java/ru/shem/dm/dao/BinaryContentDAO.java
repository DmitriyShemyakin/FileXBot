package ru.shem.dm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shem.dm.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
