package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.bean.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dao.AnnonceDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet pour la suppression d'annonces
 */
@WebServlet("/AnnonceDelete")
public class AnnonceDelete extends HttpServlet {

    /**
     * GET : Supprime l'annonce et redirige vers la liste
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);

                // Création d'un objet Annonce avec l'ID pour la suppression
                Annonce annonce = new Annonce();
                annonce.setId(id);

                // Suppression via DAO
                AnnonceDAO dao = new AnnonceDAO();
                dao.delete(annonce);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Redirection vers la liste
        response.sendRedirect("AnnonceList");
    }
}
