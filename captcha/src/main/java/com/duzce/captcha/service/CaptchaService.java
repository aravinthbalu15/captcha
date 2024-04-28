package com.duzce.captcha.service;

import com.duzce.captcha.dao.CaptchaRepository;
import com.duzce.captcha.model.Captcha;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class CaptchaService {

    private Map<String, CaptchaSession> captchaSessions = new HashMap<>();

    @Autowired
    private CaptchaRepository captchaRepository;

    @Transactional(readOnly = true)
    public Captcha getNewCaptcha(HttpSession session) {
        String sessionId = session.getId();
        CaptchaSession captchaSession = captchaSessions.get(sessionId);

        if (captchaSession != null && captchaSession.getChangeCaptchaCount().get() >= 4) {
            if(captchaSession.getExpirationTime().isBefore(Instant.now())) {
                captchaSessions.remove(sessionId);
                captchaSession = captchaSessions.get(sessionId);
            } else {
                return null;
            }
        }
        Captcha captcha = captchaRepository.findRandomCaptcha();
        if (captchaSession == null) {
            captchaSession = new CaptchaSession(sessionId, captcha.getCode(), Instant.now().plusSeconds(30), 1);
        } else {
            captchaSession.setCaptchaCode(captcha.getCode());
            captchaSession.setExpirationTime(Instant.now().plusSeconds(30));
            captchaSession.setChangeCaptchaCount(captchaSession.getChangeCaptchaCount().get() + 1);
        }
        captchaSessions.put(sessionId, captchaSession);
        return captcha;
    }

    public boolean validateCaptcha(HttpSession session, String code) {
        String sessionId = session.getId();
        CaptchaSession captchaSession = captchaSessions.get(sessionId);
        if (
                captchaSession == null ||
                !captchaSession.getCaptchaCode().equals(code) ||
                captchaSession.getExpirationTime().isBefore(Instant.now())
        ) {
            return false;
        }
        captchaSessions.remove(sessionId);
        return true;
    }

    public class CaptchaSession {

        private String userId;

        private String captchaCode;

        private Instant expirationTime;

        private AtomicInteger changeCaptchaCount;

        public CaptchaSession() {
        }

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
                    },
                    30000
            );
        }

    }

}