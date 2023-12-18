package dev.noop.blueprint.springboot.repository;

import dev.noop.blueprint.springboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

}