package org.kurento.tutorial.groupcall.permissions.persistence.repository;

import org.kurento.tutorial.groupcall.permissions.persistence.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findUserByLogin(@Param("login") String login);

    boolean existsByLogin(@Param("login") String login);

    @Query("select user from User user where login in :logins")
    List<User> findUsersByNames(@Param("logins") List<String> loginList);

    @Override
    @NonNull
    List<User> findAllById(@Param("ids") @NonNull Iterable<Long> ids);
}