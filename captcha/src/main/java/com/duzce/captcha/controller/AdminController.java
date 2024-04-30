package com.duzce.captcha.controller;

import com.duzce.captcha.model.Captcha;
import com.duzce.captcha.service.CaptchaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping()
    public String adminPage() {
        return "admin";
    }

    @DeleteMapping("/captcha")
    public void deleteCaptcha(@RequestParam(name = "id") int id, HttpServletResponse response) {
        captchaService.deleteCaptcha(id);
    }

    @PutMapping("/captcha")
    public void createCaptcha(@RequestBody Map<String, String> reqBody, HttpServletResponse response) {
        String code;
        try {
            code = reqBody.get("code");
        } catch (NullPointerException e) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            return;
        }
        captchaService.createCaptcha(code);
        response.setStatus(HttpStatus.OK.value());
    }

    @GetMapping("/captcha")
    public ResponseEntity<Map<String, List<Captcha>>> getCaptchas(
            @RequestParam(name = "first") int first,
            @RequestParam(name = "size") int size
    ) {
        List<Captcha> captchas = captchaService.getCaptchas(first, size);
        Map<String, List<Captcha>> response = new HashMap<>();
        response.put("captchas", captchas);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/captcha/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getCaptchaImageById(@RequestParam(name = "id") int id) {
        Captcha captcha = captchaService.getCaptchaById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(captcha.getImage(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/captcha/count")
    public ResponseEntity<Map<String, Integer>> getCaptchaCount() {
        captchaService.getCaptchaCount();
        Map<String, Integer> response = new HashMap<>();
        response.put("result", captchaService.getCaptchaCount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
