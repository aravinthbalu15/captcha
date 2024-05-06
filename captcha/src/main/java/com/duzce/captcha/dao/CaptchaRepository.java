package com.duzce.captcha.dao;

import com.duzce.captcha.exception.ResourceNotFoundException;
import com.duzce.captcha.model.Captcha;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.*;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CaptchaRepository {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Captcha getRandomCaptcha() {
        Captcha captcha = new Captcha();
        try {
            String query = "SELECT c FROM Captcha c ORDER BY FUNCTION('RANDOM') LIMIT 1";
            captcha = getSession().createQuery(query, Captcha.class).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        return captcha;
    }

    public List<Captcha> getCaptchas(int setFirstResult, int setMaxResults) {
        List<Captcha> captchas = null;
        try {
            CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
            CriteriaQuery<Captcha> criteriaQuery = criteriaBuilder.createQuery(Captcha.class);

            Root<Captcha> root = criteriaQuery.from(Captcha.class);

            criteriaQuery
                    .select(root)
                    .orderBy(criteriaBuilder.asc(root.get("id")));

            Query<Captcha> query = getSession().createQuery(criteriaQuery);
            query.setFirstResult(setFirstResult);
            query.setMaxResults(setMaxResults);
            captchas = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        return captchas;
    }

    public Captcha getCaptchaById(int id) {
        try {
            CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
            CriteriaQuery<Captcha> criteriaQuery = criteriaBuilder.createQuery(Captcha.class);
            Root<Captcha> root = criteriaQuery.from(Captcha.class);
            criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
            Captcha captcha = getSession().createQuery(criteriaQuery).getSingleResult();
            return captcha;
        } catch (NoResultException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
    }

    public void deleteCaptcha(int id) {
        try {
            CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
            CriteriaDelete<Captcha> criteriaDelete = criteriaBuilder.createCriteriaDelete(Captcha.class);
            Root<Captcha> root = criteriaDelete.from(Captcha.class);
            criteriaDelete.where(criteriaBuilder.equal(root.get("id"), id));
            getSession().createQuery(criteriaDelete).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
    }

    public int getRowCount() {
        int rowCount = -1;
        try {
            CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            Root<Captcha> root = countQuery.from(Captcha.class);
            countQuery.select(criteriaBuilder.count(root));
            rowCount =  getSession().createQuery(countQuery).getSingleResult().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
        return rowCount;
    }

    public void insertCaptcha(Captcha captcha) {
        try {
            getSession().save(captcha);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
    }

}
