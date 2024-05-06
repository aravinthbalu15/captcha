package com.duzce.captcha.controller;

import com.duzce.captcha.model.Captcha;
import com.duzce.captcha.service.CaptchaService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import netscape.javascript.JSObject;
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

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String adminPage() {
        return "admin";
    }

    @DeleteMapping("/captcha")
    public void deleteCaptcha(@RequestParam(name = "id") int id) {
        captchaService.deleteCaptcha(id);
    }

    @PutMapping("/captcha")
    public void createCaptcha(@RequestParam(name = "code") String code, HttpServletResponse response) {
        if (code.length() != 6 && code.chars().noneMatch(Character::isLowerCase)) {
            response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
            return;
        }
        captchaService.createCaptcha(code);
        response.setStatus(HttpStatus.OK.value());
    }

    @GetMapping(value = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getCaptchas(
            @RequestParam(name = "first") int first,
            @RequestParam(name = "size") int size
    ) {
        JSONObject jsonObject = new JSONObject();
        List<Captcha> captchas = captchaService.getCaptchas(first, size);
        jsonObject.put("captchas", captchas);
        return jsonObject.toString();
    }

    @GetMapping(value = "/captcha/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getCaptchaImageById(@RequestParam(name = "id") int id) {
        Captcha captcha = captchaService.getCaptchaById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(captcha.getImage(), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/captcha/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String getCaptchaCount() {
        captchaService.getCaptchaCount();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", captchaService.getCaptchaCount());
        return jsonObject.toString();
    }

}
