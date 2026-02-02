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
 * Servlet pour l'ajout d'annonces
 */
@WebServlet("/AnnonceAdd")
public class AnnonceAdd extends HttpServlet {

    /**
     * GET : Affiche le formulaire d'ajout
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
    }

    /**
     * POST : Traite la soumission du formulaire
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Récupération des paramètres du formulaire
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");

        // Création de l'objet Annonce
        Annonce annonce = new Annonce();
        annonce.setTitle(title);
        annonce.setDescription(description);
        annonce.setAdress(adress);
        annonce.setMail(mail);

        // Sauvegarde via DAO
        AnnonceDAO dao = new AnnonceDAO();
        dao.create(annonce);

        // Redirection vers la liste
        response.sendRedirect("AnnonceList");
    }
}
