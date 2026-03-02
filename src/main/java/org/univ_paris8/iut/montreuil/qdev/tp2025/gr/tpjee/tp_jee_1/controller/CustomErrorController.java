package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute("jakarta.servlet.error.status_code");
        Object exception = request.getAttribute("jakarta.servlet.error.exception");
        Object message = request.getAttribute("jakarta.servlet.error.message");
        Object requestUri = request.getAttribute("jakarta.servlet.error.request_uri");

        String errorId = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("message", message);
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("errorId", errorId);

        return "error";
    }
}
