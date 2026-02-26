package com.ShoeStore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private String orderCode;
    private String customerName;
    private Date createdAt;
    private double finalAmount;
    private int status;
    private String paymentMethod;
}