package com.example.demo.email;


import org.springframework.stereotype.Service;

/**
 * @author rulingfeng
 * @time 2023/11/1 11:19
 * @desc
 */
@Service
public class CouponThreadLocalContext {

    private ThreadLocal<String> verificationCoupon = new ThreadLocal<String>();
    private ThreadLocal<String> nameCou = new ThreadLocal<String>();



    public ThreadLocal<String> getVerificationCoupon() {
        return verificationCoupon;
    }

    public ThreadLocal<String> getNameCou() {
        return nameCou;
    }
}
