package org.removeBG.repository;

import org.removeBG.entity.ConfigApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigApiRepository extends JpaRepository<ConfigApiEntity, Long> {
}
