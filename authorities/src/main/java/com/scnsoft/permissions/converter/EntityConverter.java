package com.scnsoft.permissions.converter;

import com.scnsoft.permissions.dto.EntityDTO;
import com.scnsoft.permissions.persistence.entity.PersistenceEntity;

public interface EntityConverter<T extends PersistenceEntity, K extends EntityDTO> {
    K toDTO(T entity);

    T toPersistence(K entity);
}