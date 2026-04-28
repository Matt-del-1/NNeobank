package com.credit.cache;

import com.credit.dto.LoanDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class LoanCache {

  private final Map<LoanQueryKey, Page<LoanDto>> cache = new HashMap<>();

  public Page<LoanDto> get(LoanQueryKey key) {
    Page<LoanDto> result = cache.get(key);
    if (result != null) {
      System.out.println(">>> CACHE HIT: " + key);
    }
    return result;
  }

  public void put(LoanQueryKey key, Page<LoanDto> value) {
    cache.put(key, value);
    System.out.println(">>> CACHE PUT: " + key);
  }

  public void clear() {
    cache.clear();
    System.out.println(">>> CACHE CLEARED");
  }

  public void invalidateByProfileId(Long profileId) {
    cache.entrySet().removeIf(entry ->
        entry.getKey().getProfileId() != null &&
        entry.getKey().getProfileId().equals(profileId)
    );
    System.out.println(">>> CACHE INVALIDATED for profileId: " + profileId);
  }

  public int size() {
    return cache.size();
  }

  public static LoanQueryKey createKeyForAll(Pageable pageable) {
    return LoanQueryKey.forAll(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        extractSort(pageable)
    );
  }

  public static LoanQueryKey createKeyForCategory(String category, Pageable pageable) {
    return LoanQueryKey.forCategory(
        category,
        pageable.getPageNumber(),
        pageable.getPageSize(),
        extractSort(pageable)
    );
  }

  public static LoanQueryKey createKeyForLastName(String lastName, Pageable pageable) {
    return LoanQueryKey.forLastName(
        lastName,
        pageable.getPageNumber(),
        pageable.getPageSize(),
        extractSort(pageable)
    );
  }

  public static LoanQueryKey createKeyForUsername(String username, Pageable pageable) {
    return LoanQueryKey.forUsername(
        username,
        pageable.getPageNumber(),
        pageable.getPageSize(),
        extractSort(pageable)
    );
  }

  public static LoanQueryKey createKeyForProfile(Long profileId, Pageable pageable) {
    return LoanQueryKey.forProfile(
        profileId,
        pageable.getPageNumber(),
        pageable.getPageSize(),
        extractSort(pageable)
    );
  }

  public static LoanQueryKey createKeyForState(String state, Pageable pageable) {
    return LoanQueryKey.forState(
        state,
        pageable.getPageNumber(),
        pageable.getPageSize(),
        extractSort(pageable)
    );
  }

  public static LoanQueryKey createKeyForCategoryAndState(String category, String state, Pageable pageable) {
    return LoanQueryKey.forCategoryAndState(
        category,
        state,
        pageable.getPageNumber(),
        pageable.getPageSize(),
        extractSort(pageable)
    );
  }

  private static String extractSort(Pageable pageable) {
    Sort sort = pageable.getSort();
    if (sort.isUnsorted()) {
      return "unsorted";
    }
    return sort.toString();
  }
}