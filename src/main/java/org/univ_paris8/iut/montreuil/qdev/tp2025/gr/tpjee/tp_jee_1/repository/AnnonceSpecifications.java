package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;

import java.sql.Timestamp;
import java.time.LocalDate;

public class AnnonceSpecifications {

    public static Specification<Annonce> hasKeyword(String q) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(q))
                return null;
            String pattern = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern));
        };
    }

    public static Specification<Annonce> hasStatus(AnnonceStatus status) {
        return (root, query, cb) -> {
            if (status == null)
                return null;
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Annonce> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null)
                return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Annonce> hasAuthorId(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null)
                return null;
            return cb.equal(root.get("author").get("id"), authorId);
        };
    }

    public static Specification<Annonce> createdBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, cb) -> {
            if (fromDate == null && toDate == null)
                return null;
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("date"), Timestamp.valueOf(fromDate.atStartOfDay()),
                        Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
            } else if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("date"), Timestamp.valueOf(fromDate.atStartOfDay()));
            } else {
                return cb.lessThan(root.get("date"), Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
            }
        };
    }
}
