package org.kurento.tutorial.groupcall.permissions.persistence.repository;

import org.kurento.tutorial.groupcall.permissions.persistence.entity.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "permissions", path = "permissions")
public interface PermissionRepository extends CrudRepository<Permission, Long> {
    Optional<Permission> findPermissionByName(@Param("name") String name);

    @Query("select permission from Permission permission where name in :names")
    List<Permission> findPermissionsByNames(@Param("names") List<String> strings);
}