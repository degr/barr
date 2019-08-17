package com.scnsoft.permissions.persistence.repository;

import com.scnsoft.permissions.persistence.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "users", path = "users  ")
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByLogin(@Param("login") String login);

    boolean existsByLogin(@Param("login") String login);
    @Query("select user from User user where login in :logins")
    Iterable<User> findUsersByByNames(@Param("logins") Iterable<String> loginList);

}