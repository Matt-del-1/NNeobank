package com.credit.cache;

import java.util.Objects;
import lombok.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Value
public class LoanQueryKey {

  Long profileId;
  String category;
  String lastName;
  String state;
  String username;
  int page;
  int size;
  String sort;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LoanQueryKey that = (LoanQueryKey) o;
    return page == that.page && size == that.size && Objects.equals(profileId, that.profileId) && Objects.equals(category, that.category) && Objects.equals(lastName, that.lastName) && Objects.equals(state, that.state) && Objects.equals(username, that.username) && Objects.equals(sort, that.sort);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profileId, category, lastName, state, username, page, size, sort);
  }

  public LoanQueryKey(Long profileId, String category, String lastName,
      String state, String username, Pageable pageable) {
    this.profileId = profileId;
    this.category = category;
    this.lastName = lastName;
    this.state = state;
    this.username = username;
    this.page = pageable.getPageNumber();
    this.size = pageable.getPageSize();
    this.sort = extractSort(pageable);
  }

  private static String extractSort(Pageable pageable) {
    Sort sort = pageable.getSort();
    return sort.isUnsorted() ? "unsorted" : sort.toString();
  }

}