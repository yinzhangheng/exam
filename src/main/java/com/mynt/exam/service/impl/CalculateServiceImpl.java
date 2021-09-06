package com.mynt.exam.service.impl;

import com.mynt.exam.dto.VoucherItem;
import com.mynt.exam.service.CalculateService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class CalculateServiceImpl implements CalculateService {

    private static final Log log = LogFactory.getLog(CalculateServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${rule.reject.weight}")
    private Double rejectWeight;

    @Value("${rule.heavy.weight}")
    private Double heavyWeight;

    @Value("${rule.heavy.price}")
    private Double heavyPrice;

    @Value("${rule.small.volume}")
    private Double smallVolume;

    @Value("${rule.small.price}")
    private Double smallPrice;

    @Value("${rule.medium.volume}")
    private Double mediumVolume;

    @Value("${rule.medium.price}")
    private Double mediumPrice;

    @Value("${rule.large.price}")
    private Double largePrice;

    @Value("${voucher.url}")
    private String voucherUrl;
    @Value("${voucher.params}")
    private String voucherParams;


    @Override
    public Object calculate(Double weight, Double height, Double width, Double length, String voucherCode) {
        //rule 1 reject weight exceeds 50kg
        if (weight.compareTo(rejectWeight) > 0) {
            return  "N/A";
        }
        //get the discount value
        double discount = 0;
        if (StringUtils.hasText(voucherCode)) {
            discount = calculateDiscount(voucherCode);
        }
        Double result = null;
        //rule 2 heavy parcel
        if (weight.compareTo(heavyWeight) > 0) {
            result = heavyPrice * weight;
            result = result.compareTo(discount) > 0 ? result : 0;
            return result;
        }
        // volume calculate
        result = calculateVolumePrice(height, width, length);
        result = result.compareTo(discount) > 0 ? result : 0;
        return result;
    }

    @Override
    public Double calculateVolumePrice(Double height, Double width, Double length) {
        Double volume = height * width * length;
        // rule 3 Volume is less than smallVolume
        if (volume.compareTo(smallVolume) < 0) {
            return smallPrice * volume;
        }
        // rule 4 Volume is less than mediumVolume
        if (volume.compareTo(mediumVolume) < 0) {
            return mediumPrice * volume;
        }
        // rule 5 large parcel
        return largePrice * volume;
    }

    @Override
    public double calculateDiscount(String voucherCode) {
        try {
            ResponseEntity<VoucherItem> voucherItemResponseEntity = restTemplate.getForEntity(voucherUrl + voucherCode + voucherParams, VoucherItem.class);
            VoucherItem voucherItem = voucherItemResponseEntity.getBody();
            Date expiry = voucherItem.getExpiry();
            Date current = new Date();
            if (expiry.after(current)) {
                return voucherItem.getDiscount();
            }
        }catch(HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
        }
        return 0;
    }
}
