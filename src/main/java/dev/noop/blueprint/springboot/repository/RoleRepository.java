package dev.noop.blueprint.springboot.repository;

import dev.noop.blueprint.springboot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
