package com.duzce.captcha.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Table(name = "captchas")
public class Captcha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "image", nullable = false)
    private byte[] Image;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    public Captcha() {
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getImage() {
        return Image;
    }

    public void setImage(byte[] captchaImage) {
        this.Image = Image;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}