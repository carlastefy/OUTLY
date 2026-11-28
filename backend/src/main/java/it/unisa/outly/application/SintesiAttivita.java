package it.unisa.outly.application;

import it.unisa.outly.model.Luogo;
import it.unisa.outly.model.StatoAttivita;
import it.unisa.outly.model.Vincoli;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record SintesiAttivita(Long idAttivita, String titolo, LocalDate data, LocalTime ora,
        Luogo luogo, BigDecimal costo, StatoAttivita stato) {
    public SintesiAttivita {
        Vincoli.id(idAttivita); Vincoli.testo(titolo, 5, 50); Vincoli.richiesto(data); Vincoli.richiesto(ora);
        Vincoli.richiesto(luogo); Vincoli.richiesto(costo); Vincoli.richiesto(stato);
        if (costo.signum() < 0) { throw new IllegalArgumentException("Costo negativo"); }
    }
    public boolean visibileAl(LocalDateTime adesso) {
        return stato == StatoAttivita.PUBBLICATA && data.atTime(ora).isAfter(Vincoli.richiesto(adesso));
    }
}
