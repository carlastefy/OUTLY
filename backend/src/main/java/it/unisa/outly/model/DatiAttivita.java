package it.unisa.outly.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record DatiAttivita(String titolo, String foto, String descrizione, LocalDate data,
        LocalTime ora, Luogo luogo, BigDecimal costo, int durataMinuti, Livello preferenzeSocialita,
        Set<String> caratteristicheAccessibilita, String tipologiaAttivita, Livello difficoltaTecnica,
        Livello difficoltaFisica, int numeroMassimoPartecipanti) {
    public DatiAttivita {
        Vincoli.testo(titolo, 5, 50);
        Vincoli.testo(foto, 1, Integer.MAX_VALUE);
        Vincoli.testo(descrizione, 10, 500);
        Vincoli.richiesto(data); Vincoli.richiesto(ora); Vincoli.richiesto(luogo);
        if (Vincoli.richiesto(costo).signum() < 0) { throw new IllegalArgumentException("Costo negativo"); }
        if (durataMinuti <= 0) { throw new IllegalArgumentException("Durata non valida"); }
        Vincoli.richiesto(preferenzeSocialita);
        Vincoli.richiesto(caratteristicheAccessibilita)
                .forEach(s -> Vincoli.testo(s, 1, Integer.MAX_VALUE));
        caratteristicheAccessibilita = Set.copyOf(caratteristicheAccessibilita);
        Vincoli.testo(tipologiaAttivita, 1, Integer.MAX_VALUE);
        Vincoli.richiesto(difficoltaTecnica); Vincoli.richiesto(difficoltaFisica);
        if (numeroMassimoPartecipanti < 0 || numeroMassimoPartecipanti > 99) { throw new IllegalArgumentException("Capienza non valida"); }
        data.atTime(ora).plusMinutes(durataMinuti);
    }
}
