package com.credit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoanDTO(
        Long id,
        @JsonProperty("client_name") String clientName,
        Double amount,
        String status
) {}