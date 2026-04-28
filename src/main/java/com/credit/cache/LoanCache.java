package com.credit.cache;

import com.credit.dto.LoanDto;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoanCache {

  private final Map<LoanQueryKey, Page<LoanDto>> cache = new HashMap<>();

  public Page<LoanDto> get(LoanQueryKey key) {
    return cache.get(key);
  }

  public void put(LoanQueryKey key, Page<LoanDto> value) {
    cache.put(key, value);
  }

  public void clear() {
    cache.clear();
    log.info("Cache cleared");
  }

  private void invalidate(Predicate<LoanQueryKey> condition) {
    cache.entrySet().removeIf(entry -> condition.test(entry.getKey()));
  }

  public void invalidateByProfileId(Long profileId) {
    invalidate(key -> profileId.equals(key.getProfileId()));
  }

  public void invalidateByCategory(String category) {
    invalidate(key -> category.equalsIgnoreCase(key.getCategory()));
  }

  public void invalidateByState(String state) {
    invalidate(key -> state.equals(key.getState()));
  }

  public int size() {
    return cache.size();
  }
}