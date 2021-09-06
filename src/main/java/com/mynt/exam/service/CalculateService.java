package com.mynt.exam.service;

public interface CalculateService {

    Object calculate(Double weight, Double height, Double width, Double length, String voucherCode);

    Double calculateVolumePrice(Double height, Double width, Double length);

    double calculateDiscount(String voucherCode);
}
