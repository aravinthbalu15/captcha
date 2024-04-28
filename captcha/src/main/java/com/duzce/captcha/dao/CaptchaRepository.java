package com.duzce.captcha.dao;

import com.duzce.captcha.model.Captcha;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class CaptchaRepository {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Captcha findRandomCaptcha() {
        Session session = sessionFactory.openSession();
        try {
            String query = "SELECT c FROM Captcha c ORDER BY FUNCTION('RANDOM') LIMIT 1";
            Captcha result = session.createQuery(query, Captcha.class).getSingleResult();
            return result;
        } finally {
            session.close();
        }
    }

}
