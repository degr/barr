package com.scnsoft.permissions.persistence.repository;

import com.scnsoft.permissions.persistence.entity.AdditionalPermission;
import com.scnsoft.permissions.persistence.entity.CompositePermissionId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface AdditionalPermissionRepository extends CrudRepository<AdditionalPermission, CompositePermissionId> {

}