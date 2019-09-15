package org.kurento.tutorial.groupcall.permissions.persistence.repository;

import org.kurento.tutorial.groupcall.permissions.persistence.entity.AdditionalPermission;
import org.kurento.tutorial.groupcall.permissions.persistence.entity.CompositePermissionId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AdditionalPermissionRepository extends CrudRepository<AdditionalPermission, CompositePermissionId> {

}