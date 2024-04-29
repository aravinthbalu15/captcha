package com.duzce.captcha.controller;

import com.duzce.captcha.service.CaptchaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

}
