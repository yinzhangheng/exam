package com.mynt.exam.controller;


import com.mynt.exam.service.CalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculateController {

    @Autowired
    private CalculateService calculateService;

    @GetMapping(value = "/calculator")
    public ResponseEntity<Object> calculate(@RequestParam(value = "weight") Double weight,
                                            @RequestParam(value = "height") Double height,
                                            @RequestParam(value = "width") Double width,
                                            @RequestParam(value = "length") Double length,
                                            @RequestParam(value = "voucherCode",  required = false) String voucherCode
    ) {

        Object result = calculateService.calculate(weight, height, width, length, voucherCode);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
