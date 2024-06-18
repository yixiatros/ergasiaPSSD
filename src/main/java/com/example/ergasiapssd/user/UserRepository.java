package com.example.ergasiapssd.user;

import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findUserByUsername(String username);

    @Query("SELECT u FROM User u, Role r WHERE r.name = ?1 AND r member u.roles")
    List<User> findAllUsersByRole(String role);

    @Query("SELECT u FROM User u, Role r WHERE u.username LIKE %?2% AND r.name = ?1 AND r member u.roles")
    List<User> findAllUsersByRoleAndSearch(String role, String search);

    @Query("SELECT u FROM User u WHERE u.username LIKE %?1%")
    List<User> findUsersByUsernameViaSearch(String search);

    @Query("SELECT u.username FROM User u, Role r WHERE r.name = ?1 AND r member u.roles")
    List<String> findUsernamesByRole(String role);

    @Query("SELECT u.username FROM User u, Role r WHERE u.username LIKE %?2% AND r.name = ?1 AND r member u.roles")
    List<String> findUsernamesByRoleViaSearch(String role, String search);
}
