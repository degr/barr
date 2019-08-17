package com.scnsoft.permissions.service;

import com.scnsoft.permissions.converter.EntityConverter;
import com.scnsoft.permissions.dto.EntityDTO;
import com.scnsoft.permissions.persistence.entity.PersistenceEntity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class BaseCrudService<T extends PersistenceEntity<R>, K extends EntityDTO, R extends Serializable> implements EntityService<K, R> {
    private CrudRepository<T, R> repository;
    private EntityConverter<T, K> converter;

    public BaseCrudService(CrudRepository<T, R> repository, EntityConverter<T, K> converter) {
        this.repository = repository;
        this.converter = converter;
    }

    @Override
    public Optional<K> saveEntity(K entityDTO) {
        return Optional.ofNullable(entityDTO)
                .map(converter::toPersistence)
                .filter(entity -> Optional.ofNullable(entity.getId())
                        .map(id -> !repository.existsById(id))
                        .orElse(true)
                )
                .map(repository::save)
                .map(converter::toDTO);
    }

    @Override
    public Optional<K> findById(R id) {
        return Optional.ofNullable(id)
                .flatMap(repository::findById)
                .map(converter::toDTO);
    }

    @Override
    public void deleteById(R id) {
        Optional.ofNullable(id)
                .ifPresent(repository::deleteById);
    }

    Stream<K> entities() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(converter::toDTO);
    }
}