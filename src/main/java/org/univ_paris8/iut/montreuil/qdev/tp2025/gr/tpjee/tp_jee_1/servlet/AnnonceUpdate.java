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
 * Servlet pour la modification d'annonces
 */
@WebServlet("/AnnonceUpdate")
public class AnnonceUpdate extends HttpServlet {

    /**
     * GET : Affiche le formulaire de modification avec les données de l'annonce
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                AnnonceDAO dao = new AnnonceDAO();
                Annonce annonce = dao.find(id);

                if (annonce != null) {
                    request.setAttribute("annonce", annonce);
                    this.getServletContext().getRequestDispatcher("/AnnonceUpdate.jsp").forward(request, response);
                } else {
                    response.sendRedirect("AnnonceList");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect("AnnonceList");
            }
        } else {
            response.sendRedirect("AnnonceList");
        }
    }

    /**
     * POST : Traite la mise à jour de l'annonce
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);

                // Récupération des paramètres
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String adress = request.getParameter("adress");
                String mail = request.getParameter("mail");

                // Création de l'objet Annonce avec mise à jour
                Annonce annonce = new Annonce();
                annonce.setId(id);
                annonce.setTitle(title);
                annonce.setDescription(description);
                annonce.setAdress(adress);
                annonce.setMail(mail);

                // Mise à jour via DAO
                AnnonceDAO dao = new AnnonceDAO();
                dao.update(annonce);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Redirection vers la liste
        response.sendRedirect("AnnonceList");
    }
}
