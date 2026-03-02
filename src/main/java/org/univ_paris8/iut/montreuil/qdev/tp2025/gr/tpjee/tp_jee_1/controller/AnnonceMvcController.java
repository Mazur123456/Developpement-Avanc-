package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.CategoryService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AnnonceMvcController {

    private final AnnonceService annonceService;
    private final CategoryService categoryService;

    @GetMapping("/annonces")
    public String list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {

        if (page < 1)
            page = 1;

        Page<Annonce> annonces = annonceService.findPublished(page);
        int totalPages = annonceService.getTotalPages();
        List<Category> categories = categoryService.findAll();

        model.addAttribute("annonces", annonces.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "annonces/list";
    }

    @GetMapping("/annonce/detail")
    public String detail(@RequestParam("id") Long id, Model model) {
        Optional<Annonce> annonceOpt = annonceService.findByIdWithDetails(id);
        if (annonceOpt.isPresent()) {
            model.addAttribute("annonce", annonceOpt.get());
            return "annonces/detail";
        } else {
            return "redirect:/annonces";
        }
    }

    @GetMapping("/annonce/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "annonces/create";
    }

    @PostMapping("/annonce/create")
    public String create(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("adress") String adress,
            @RequestParam("mail") String mail,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (title == null || title.trim().isEmpty()) {
            model.addAttribute("error", "Le titre est obligatoire");
            populateModelForCreate(model, title, description, adress, mail, categoryId);
            return "annonces/create";
        }

        try {
            Annonce annonce = annonceService.create(title, description, adress, mail, userId, categoryId);
            return "redirect:/annonce/detail?id=" + annonce.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la création: " + e.getMessage());
            populateModelForCreate(model, title, description, adress, mail, categoryId);
            return "annonces/create";
        }
    }

    @GetMapping("/annonce/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        Optional<Annonce> annonceOpt = annonceService.findByIdWithDetails(id);
        if (annonceOpt.isPresent()) {
            model.addAttribute("annonce", annonceOpt.get());
            model.addAttribute("categories", categoryService.findAll());
            return "annonces/edit";
        } else {
            return "redirect:/annonces";
        }
    }

    @PostMapping("/annonce/edit")
    public String edit(
            @RequestParam("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("adress") String adress,
            @RequestParam("mail") String mail,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        try {
            annonceService.update(id, title, description, adress, mail, categoryId, userId);
            return "redirect:/annonce/detail?id=" + id;
        } catch (SecurityException e) {
            model.addAttribute("error", "Non autorisé");
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur de modification : " + e.getMessage());

            Annonce temp = new Annonce(title, description, adress, mail);
            temp.setId(id);
            model.addAttribute("annonce", temp);
            model.addAttribute("categories", categoryService.findAll());
            return "annonces/edit";
        }
    }

    @PostMapping("/annonce/delete")
    public String delete(@RequestParam("id") Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        try {
            annonceService.delete(id, userId);
        } catch (Exception e) {
            // Log or handle error if needed
        }
        return "redirect:/annonces";
    }

    @PostMapping("/annonce/status")
    public String updateStatus(
            @RequestParam("id") Long id,
            @RequestParam("action") String action,
            HttpSession session,
            Model model) {

        Long userId = (Long) session.getAttribute("userId");
        try {
            if ("PUBLISH".equalsIgnoreCase(action)) {
                annonceService.publish(id, userId);
            } else if ("ARCHIVE".equalsIgnoreCase(action)) {
                annonceService.archive(id, userId);
            }
            return "redirect:/annonce/detail?id=" + id;
        } catch (SecurityException e) {
            model.addAttribute("error", "Non autorisé");
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors du changement de statut : " + e.getMessage());
            return "redirect:/annonce/detail?id=" + id;
        }
    }

    private void populateModelForCreate(Model model, String title, String description, String adress, String mail,
            Long categoryId) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("adress", adress);
        model.addAttribute("mail", mail);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.findAll());
    }
}
