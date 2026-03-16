package com.credit.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  private String password; // В реальном проекте здесь хранится хеш

  // Связь 1:1. cascade = CascadeType.ALL означает, что при удалении юзера удалится и профиль
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private Profile profile;
}