package com.credit.cache;

import java.util.Objects;
import lombok.Getter;

@Getter
public class LoanQueryKey {

  private final QueryType queryType;
  private final String category;
  private final String lastName;
  private final String state;
  private final String username;
  private final Long profileId;
  private final int page;
  private final int size;
  private final String sort;

  public enum QueryType {
    ALL,
    BY_CATEGORY,
    BY_LASTNAME,
    BY_USERNAME,
    BY_PROFILE,
    BY_STATE,
    BY_CATEGORY_AND_STATE
  }

  private LoanQueryKey(Builder builder) {
    this.queryType = builder.queryType;
    this.category = builder.category;
    this.lastName = builder.lastName;
    this.state = builder.state;
    this.username = builder.username;
    this.profileId = builder.profileId;
    this.page = builder.page;
    this.size = builder.size;
    this.sort = builder.sort;
  }

  // Статические фабричные методы
  public static LoanQueryKey forAll(int page, int size, String sort) {
    return new Builder(QueryType.ALL).page(page).size(size).sort(sort).build();
  }

  public static LoanQueryKey forCategory(String category, int page, int size, String sort) {
    return new Builder(QueryType.BY_CATEGORY).category(category).page(page).size(size).sort(sort).build();
  }

  public static LoanQueryKey forLastName(String lastName, int page, int size, String sort) {
    return new Builder(QueryType.BY_LASTNAME).lastName(lastName).page(page).size(size).sort(sort).build();
  }

  public static LoanQueryKey forUsername(String username, int page, int size, String sort) {
    return new Builder(QueryType.BY_USERNAME).username(username).page(page).size(size).sort(sort).build();
  }

  public static LoanQueryKey forProfile(Long profileId, int page, int size, String sort) {
    return new Builder(QueryType.BY_PROFILE).profileId(profileId).page(page).size(size).sort(sort).build();
  }

  public static LoanQueryKey forState(String state, int page, int size, String sort) {
    return new Builder(QueryType.BY_STATE).state(state).page(page).size(size).sort(sort).build();
  }

  public static LoanQueryKey forCategoryAndState(String category, String state, int page, int size, String sort) {
    return new Builder(QueryType.BY_CATEGORY_AND_STATE).category(category).state(state).page(page).size(size).sort(sort).build();
  }

  // equals и hashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LoanQueryKey that = (LoanQueryKey) o;
    return page == that.page
        && size == that.size
        && queryType == that.queryType
        && Objects.equals(category, that.category)
        && Objects.equals(lastName, that.lastName)
        && Objects.equals(state, that.state)
        && Objects.equals(username, that.username)
        && Objects.equals(profileId, that.profileId)
        && Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queryType, category, lastName, state, username, profileId, page, size, sort);
  }

  // Builder
  private static class Builder {
    private final QueryType queryType;
    private String category;
    private String lastName;
    private String state;
    private String username;
    private Long profileId;
    private int page;
    private int size;
    private String sort;

    Builder(QueryType queryType) { this.queryType = queryType; }
    Builder category(String category) { this.category = category; return this; }
    Builder lastName(String lastName) { this.lastName = lastName; return this; }
    Builder state(String state) { this.state = state; return this; }
    Builder username(String username) { this.username = username; return this; }
    Builder profileId(Long profileId) { this.profileId = profileId; return this; }
    Builder page(int page) { this.page = page; return this; }
    Builder size(int size) { this.size = size; return this; }
    Builder sort(String sort) { this.sort = sort; return this; }

    LoanQueryKey build() { return new LoanQueryKey(this); }
  }

}