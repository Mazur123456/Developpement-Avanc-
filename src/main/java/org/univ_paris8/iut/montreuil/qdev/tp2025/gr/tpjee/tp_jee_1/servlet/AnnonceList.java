package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.servlet;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dao.AnnonceDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet pour afficher la liste des annonces
 */
@WebServlet("/AnnonceList")
public class AnnonceList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        AnnonceDAO dao = new AnnonceDAO();
        request.setAttribute("annonces", dao.findAll());
        this.getServletContext().getRequestDispatcher("/AnnonceList.jsp").forward(request, response);
    }
}
