package com.credit.repository;

import com.credit.model.Profile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

  Optional<Profile> findByUserId(Long userId);

  boolean existsByUserId(Long userId);
}