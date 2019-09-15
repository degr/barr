package org.kurento.tutorial.groupcall.permissions.service;

import org.kurento.tutorial.groupcall.permissions.dto.EntityDTO;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface EntityService<T extends EntityDTO, K extends Serializable> {
    Optional<T> saveEntity(T e);

    Optional<T> findById(K id);

    void deleteById(K id);

    Collection<T> findAll();
}