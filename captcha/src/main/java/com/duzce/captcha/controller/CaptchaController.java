package com.duzce.captcha.controller;

import com.duzce.captcha.model.Captcha;
import com.duzce.captcha.service.CaptchaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping()
    public String showCaptchaPage() {
        return "captcha";
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getCaptchaImage(HttpSession session) {
        Captcha captcha = captchaService.getRandomCaptcha(session);
        if (captcha == null) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(captcha.getImage(), headers, HttpStatus.OK);
    }

    @PostMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Boolean>> validateCaptcha(HttpSession session, @RequestBody Map<String, String> reqBody) {
        String code;
        try {
            code = reqBody.get("code");
        } catch (NullPointerException e) {
            Map<String, Boolean> response = new HashMap<>();
            response.put("result", false);
            return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
        }
        if (code.length() != 6 && code.chars().noneMatch(Character::isLowerCase)) {
            Map<String, Boolean> response = new HashMap<>();
            response.put("result", false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        boolean isValid = captchaService.validateCaptcha(session, code);
        Map<String, Boolean> response = new HashMap<>();
        response.put("result", isValid);
        return new ResponseEntity<>(response, isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
