package org.removeBG.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RazorPayOrderDTO {
    private String id;
    private String entity;
    private Integer amount;
    private Integer amount_due;
    private Integer amount_paid;
    private String currency;
    private String status;
    private String receipt;
}