package com.duzce.captcha.repository;

import com.duzce.captcha.model.Captcha;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaRepository extends CrudRepository<Captcha, Long> {
    @Query(value = "SELECT * FROM captchas ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Captcha findRandomCaptcha();
}