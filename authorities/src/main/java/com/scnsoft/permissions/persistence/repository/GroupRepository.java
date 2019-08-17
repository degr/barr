package com.scnsoft.permissions.persistence.repository;

import com.scnsoft.permissions.persistence.entity.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
@RepositoryRestResource(collectionResourceRel = "user_groups", path = "user_groups")
public interface GroupRepository extends CrudRepository<Group, Long> {
    Optional<Group> findUserGroupByName(@Param("name") String name);

    void deleteByName(@Param("name") String name);
}