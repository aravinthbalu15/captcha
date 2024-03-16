package com.duzce.captcha.service;

import com.duzce.captcha.model.Captcha;
import com.duzce.captcha.repository.CaptchaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Service
public class CaptchaService {

    private Map<String, CaptchaSession> captchaSessions = new HashMap<>();

    @Autowired
    private CaptchaRepository captchaRepository;

    public Captcha getNewCaptcha(HttpSession session) {
        String sessionId = session.getId();
        CaptchaSession captchaSession = captchaSessions.get(sessionId);
        if (captchaSession != null && captchaSession.getChangeCaptchaCount() >= 4) {
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
            captchaSession.setChangeCaptchaCount(captchaSession.getChangeCaptchaCount() + 1);
        }
        captchaSessions.put(sessionId, captchaSession);
        return captcha;
    }

    public boolean validateCaptcha(HttpSession session, String code) {
        String sessionId = session.getId();
        CaptchaSession captchaSession = captchaSessions.get(sessionId);
        if (captchaSession == null || !captchaSession.getCaptchaCode().equals(code) || captchaSession.getExpirationTime().isBefore(Instant.now())) {
            return false;
        }
        captchaSessions.remove(sessionId);
        return true;
    }

    public class CaptchaSession {
        private String userId;
        private String captchaCode;
        private Instant expirationTime;
        private int changeCaptchaCount;

        public CaptchaSession() {
        }

        public CaptchaSession(String userId, String captchaCode, Instant expirationTime, int changeCaptchaCount) {
            this.userId = userId;
            this.captchaCode = captchaCode;
            this.expirationTime = expirationTime;
            this.changeCaptchaCount = changeCaptchaCount;
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

        public int getChangeCaptchaCount() {
            return changeCaptchaCount;
        }

        public void setChangeCaptchaCount(int changeCaptchaCount) {
            this.changeCaptchaCount = changeCaptchaCount;
        }
    }

}
