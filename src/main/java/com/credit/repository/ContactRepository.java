package com.credit.repository;

import com.credit.model.Contact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

  List<Contact> findByProfileId(Long profileId);

  List<Contact> findByEmail(String email);
}
