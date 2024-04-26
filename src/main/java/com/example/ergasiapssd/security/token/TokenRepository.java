package com.example.ergasiapssd.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t INNER JOIN User u on t.user.id = u.id WHERE u.id = ?1 AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokensByUser(Long userId);

    @Query("SELECT t FROM Token t WHERE t.id = ?1")
    Optional<Token> findByToken(String token);
}
