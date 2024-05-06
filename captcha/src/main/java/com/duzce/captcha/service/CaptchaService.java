package com.duzce.captcha.service;

import com.duzce.captcha.dao.CaptchaRepository;
import com.duzce.captcha.model.Captcha;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class CaptchaService {

    private Map<String, CaptchaSession> captchaSessions = new HashMap<>();

    @Autowired
    private CaptchaRepository captchaRepository;

    public Captcha getRandomCaptcha(HttpSession session) {
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
        Captcha captcha = captchaRepository.getRandomCaptcha();
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

    public List<Captcha> getCaptchas(int setFirstResult, int setMaxResults) {
        List<Captcha> captchas = captchaRepository.getCaptchas(setFirstResult, setMaxResults);
        if (captchas != null) {
            for (Captcha captcha : captchas) {
                captcha.setImage(null);
            }
        }
        return captchas;
    }

    @Transactional(readOnly = false)
    public void deleteCaptcha(int id) {
        captchaRepository.deleteCaptcha(id);
    }

    @Transactional(readOnly = false)
    public void createCaptcha(String code) {
            Captcha captcha = new Captcha();
            captcha.setCode(code);
            captcha.setImage(CaptchaGenerator.generateImage(captcha.getCode()));
            captchaRepository.insertCaptcha(captcha);
    }

    @Transactional(readOnly = false)
    public Captcha getCaptchaById(int id) {
        Captcha captcha = captchaRepository.getCaptchaById(id);
        return captcha;
    }

    public int getCaptchaCount() {
        captchaRepository.getRowCount();
        return captchaRepository.getRowCount();
    }

}