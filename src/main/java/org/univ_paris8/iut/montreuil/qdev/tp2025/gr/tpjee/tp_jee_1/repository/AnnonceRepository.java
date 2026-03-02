package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;

import java.util.List;
import java.util.Optional;

public interface AnnonceRepository extends JpaRepository<Annonce, Long> {

        @Query("SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.id = :id")
        Optional<Annonce> findByIdWithDetails(@Param("id") Long id);

        @Query(value = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category", countQuery = "SELECT COUNT(a) FROM Annonce a")
        Page<Annonce> findAllWithDetails(Pageable pageable);

        @Query(value = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category " +
                        "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))", countQuery = "SELECT COUNT(a) FROM Annonce a "
                                        +
                                        "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                                        "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<Annonce> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

        @Query(value = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category " +
                        "WHERE a.category.id = :categoryId AND a.status = :status", countQuery = "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :categoryId AND a.status = :status")
        Page<Annonce> findByCategoryIdAndStatusWithDetails(@Param("categoryId") Long categoryId,
                        @Param("status") AnnonceStatus status, Pageable pageable);

        @Query(value = "SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.status = :status", countQuery = "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status")
        Page<Annonce> findByStatusWithDetails(@Param("status") AnnonceStatus status, Pageable pageable);

        @Query("SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.author.id = :authorId ORDER BY a.date DESC")
        List<Annonce> findByAuthorIdWithDetails(@Param("authorId") Long authorId);

        long countByStatus(AnnonceStatus status);
}
