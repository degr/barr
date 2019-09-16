package org.kurento.tutorial.groupcall.permissions.service;

import lombok.RequiredArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.converter.EntityConverter;
import org.kurento.tutorial.groupcall.permissions.dto.EntityDTO;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.PersistenceEntity;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public abstract class BaseCrudService<T extends PersistenceEntity<R>, K extends EntityDTO, R extends Serializable> implements EntityService<K, R> {
    private final CrudRepository<T, R> repository;
    private final EntityConverter<T, K> converter;

    @Override
    public K save(K entityDTO) {
        return Optional.ofNullable(entityDTO)
                .map(converter::toPersistence)
                .map(repository::save)
                .map(converter::toDTO)
                .orElseThrow(UnsupportedOperationException::new);
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

    @Override
    public List<K> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(converter::toDTO)
                .collect(Collectors.toList());
    }

    public long count() {
        return repository.count();
    }
}