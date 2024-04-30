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
        return captchaRepository.getCaptchas(setFirstResult, setMaxResults);
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

    public static class CaptchaGenerator {

        private static final int IMAGE_WIDTH = 440;

        private static final int IMAGE_HEIGHT = 220;

        private static final int CHAR_COUNT = 6;

        private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        private static final Random random = new Random();

        public static byte[] generateImage(String code) {
            try {
                // Resim oluşturma
                BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();

                // Arka plan rengi
                g2d.setColor(new Color(242, 242, 242));
                g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

                // Font ayarlama
                Font font = new Font("Times New Roman", Font.ITALIC, 80);
                g2d.setFont(font);

                // Kodu dağınık şekilde resme yazma
                g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));

                // Metni tam ortada başlatmak için
                FontMetrics fm = g2d.getFontMetrics(font);
                int charWidth = fm.charWidth('A'); // Ortalama karakter genişliği

                int textWidth = fm.stringWidth(code);
                int startX = (IMAGE_WIDTH - textWidth - 30) / 2; // Horizontal centering
                int startY = ((IMAGE_HEIGHT + charWidth) / 2); // Vertical centering


                int charHeight = font.getSize();

                char[] chars = code.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    int x = startX + ((i * charWidth * 10) / 9);
                    int y = startY + random.nextInt(charHeight / 2) - charHeight / 3; // Vertical distortion
                    double rotationAngle = Math.toRadians(random.nextInt(31) - 15); // Maksimum ±15 derece dönüş
                    g2d.rotate(rotationAngle, x, y);
                    g2d.drawString(String.valueOf(chars[i]), x, y);
                    g2d.rotate(-rotationAngle, x, y); // Dönüşü geri al
                }

                // Rastgele noktalar ekleme
                g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                for (int i = 0; i < 200; i++) {
                    int x1 = random.nextInt(IMAGE_WIDTH);
                    int y1 = random.nextInt(220);
                    g2d.drawRect(x1, y1, 1, 1);
                }

                // Parabolik çizgiler ekleme
                g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                for (int i = 0; i < 10; i++) {
                    int x1 = random.nextInt(IMAGE_WIDTH);
                    int y1 = random.nextInt(IMAGE_HEIGHT);
                    int x2 = random.nextInt(IMAGE_WIDTH);
                    int y2 = random.nextInt(IMAGE_HEIGHT);
                    int x3 = random.nextInt(IMAGE_WIDTH);
                    int y3 = random.nextInt(IMAGE_HEIGHT);

                    int[] xPoints = {x1, x2, x3};
                    int[] yPoints = {y1, y2, y3};

                    g2d.drawPolyline(xPoints, yPoints, 3);
                }

                g2d.dispose();
                // Resmi byte dizisine dönüştürme
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                return baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static String generateRandomCode() {
            SecureRandom random = new SecureRandom();
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
                char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
                sb.append(randomChar);
            }
            return sb.toString();
        }

    }

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

}