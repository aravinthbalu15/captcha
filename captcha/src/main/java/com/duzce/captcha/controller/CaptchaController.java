package com.duzce.captcha.controller;

import com.duzce.captcha.model.Captcha;
import com.duzce.captcha.service.CaptchaService;
import jakarta.servlet.http.HttpSession;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
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
    public  ResponseEntity<String> validateCaptcha(HttpSession session, @RequestParam(name = "code") String code) {
        JSONObject jsonResObject = new JSONObject();
        if (code.length() != 6 && code.chars().noneMatch(Character::isLowerCase)) {
            jsonResObject.put("result", false);
            return new ResponseEntity<>(jsonResObject.toString(), HttpStatus.BAD_REQUEST);
        }
        boolean isValid = captchaService.validateCaptcha(session, code);
        jsonResObject.put("result", isValid);
        return new ResponseEntity<>(jsonResObject.toString(), isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
