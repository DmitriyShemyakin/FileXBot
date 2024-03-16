package ru.shem.dm;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shem.dm.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
