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