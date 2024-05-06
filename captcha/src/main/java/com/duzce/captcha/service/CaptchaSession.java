package com.duzce.captcha.service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class CaptchaSession {

    private String userId;

    private String captchaCode;

    private Instant expirationTime;

    private AtomicInteger changeCaptchaCount;

    public CaptchaSession() {}

    public CaptchaSession(String userId, String captchaCode, Instant expirationTime, int changeCaptchaCount) {
        this.userId = userId;
        this.captchaCode = captchaCode;
        this.expirationTime = expirationTime;
        this.changeCaptchaCount = new AtomicInteger(changeCaptchaCount);
        delayedDecreaseChangeCaptchaCount();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public AtomicInteger getChangeCaptchaCount() {
        return changeCaptchaCount;
    }

    public void setChangeCaptchaCount(int changeCaptchaCount) {
        this.changeCaptchaCount.set(changeCaptchaCount);
        delayedDecreaseChangeCaptchaCount();
    }

    private void delayedDecreaseChangeCaptchaCount() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {if (changeCaptchaCount.get() >= 1) changeCaptchaCount.getAndDecrement();}
                }, 30000
        );
    }

}