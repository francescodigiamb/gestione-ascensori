package com.francesco.gestione_ascensori.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // ✅ Rotta per la homepage
    // Quando l'utente va su "/", ritorniamo il template "index.html"
    @GetMapping("/")
    public String home(Model model) {

        // Per ora mettiamo un semplice titolo fittizio da mostrare nella view
        model.addAttribute("pageTitle", "Gestione Impianti Ascensori");

        // Ritorna il nome del file in templates: "index.html"
        return "index";
    }
}
