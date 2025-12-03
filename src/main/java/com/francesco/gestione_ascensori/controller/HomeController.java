package com.francesco.gestione_ascensori.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.francesco.gestione_ascensori.repository.LuogoRepository;

/**
 * ✅ Controller per la homepage
 * Mostra la lista dei luoghi (Pescara, Chieti, ecc.)
 */
@Controller
public class HomeController {

    private final LuogoRepository luogoRepository;

    public HomeController(LuogoRepository luogoRepository) {
        this.luogoRepository = luogoRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        // Titolo pagina
        model.addAttribute("pageTitle", "Gestione Impianti Ascensori");

        // Lista di tutti i luoghi dal DB
        model.addAttribute("luoghi", luogoRepository.findAll());

        return "index";
    }
}