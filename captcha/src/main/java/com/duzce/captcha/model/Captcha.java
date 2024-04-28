package com.duzce.captcha.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.*;
import org.hibernate.type.descriptor.jdbc.TimestampJdbcType;

import java.security.Timestamp;
import java.sql.Types;

import static org.postgresql.core.Oid.TIMESTAMP;

@Entity
@Table(name = "captchas")
public class Captcha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", length = 6, nullable = false)
    private String code;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "image", nullable = false)
    private byte[] image;


    // Default constructor
    public Captcha() {
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}