package org.kurento.tutorial.groupcall.permissions.converter;

import org.kurento.tutorial.groupcall.permissions.dto.EntityDTO;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.PersistenceEntity;

public interface EntityConverter<T extends PersistenceEntity, K extends EntityDTO> {
    K toDTO(T entity);

    T toPersistence(K entity);
}