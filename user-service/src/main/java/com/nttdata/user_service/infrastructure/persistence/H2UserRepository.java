package com.nttdata.user_service.infrastructure.persistence;

import com.nttdata.user_service.domain.model.User;
import com.nttdata.user_service.domain.repository.UserRepository;
import com.nttdata.user_service.infrastructure.persistence.jpa.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class H2UserRepository implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public H2UserRepository(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}