package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.bean.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dao.AnnonceDAO;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/AnnonceAdd")
public class AnnonceAdd extends HttpServlet {

    // Affiche le formulaire
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher("/AnnonceAdd.jsp").forward(request, response);
    }

    // Traite la soumission du formulaire
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Annonce a = new Annonce();
        a.setTitle(request.getParameter("title"));
        a.setDescription(request.getParameter("description"));
        a.setAdress(request.getParameter("adress"));
        a.setMail(request.getParameter("mail"));

        AnnonceDAO dao = new AnnonceDAO();
        dao.create(a);

        // Redirection vers la liste après ajout
        response.sendRedirect("AnnonceList");
    }
}