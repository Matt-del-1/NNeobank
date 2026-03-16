package com.credit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoanDto(
    Long id,
    @JsonProperty("client_name") String clientName,
    Double amount,
    String status
) {}