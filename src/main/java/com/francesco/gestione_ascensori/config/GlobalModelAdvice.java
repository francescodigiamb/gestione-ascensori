package com.francesco.gestione_ascensori.config;

import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalModelAdvice {

    private final InterventoRepository interventoRepository;

    public GlobalModelAdvice(InterventoRepository interventoRepository) {
        this.interventoRepository = interventoRepository;
    }

    @ModelAttribute("navbarAlertCount")
    public long navbarAlertCount() {
        return interventoRepository.countInScadenzaEntro(StatoIntervento.DA_FARE, LocalDate.now().plusDays(7));
    }
}
