package org.kurento.tutorial.groupcall.permissions.service;

import org.kurento.tutorial.groupcall.permissions.dto.EntityDTO;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface EntityService<T extends EntityDTO, K extends Serializable> {
    T save(T e);

    Optional<T> findById(K id);

    void deleteById(K id);

    List<T> findAll();
}