package com.vinifsouza.api.controller;

import com.vinifsouza.api.domain.coupon.Coupon;
import com.vinifsouza.api.domain.coupon.CouponRequestDTO;
import com.vinifsouza.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService service;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Coupon> addCouponsToEvent(@PathVariable UUID eventId, @RequestBody CouponRequestDTO data) {
        Coupon coupons = service.addCouponToEvent(eventId, data);
        return ResponseEntity.ok(coupons);
    }
}