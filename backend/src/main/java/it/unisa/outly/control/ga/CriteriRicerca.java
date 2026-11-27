package it.unisa.outly.control.ga;

import it.unisa.outly.model.Livello;
import it.unisa.outly.model.Vincoli;
import java.math.BigDecimal;
import java.util.Set;

public record CriteriRicerca(BigDecimal budgetMax, String citta, String provincia,
        Set<String> caratteristicheAccessibilita, String tipologiaAttivita,
        Livello preferenzeSocialita, Livello difficoltaTecnica, Livello difficoltaFisica) {
    public CriteriRicerca {
        if (budgetMax != null && budgetMax.signum() < 0) {
            throw new IllegalArgumentException("Budget negativo");
        }
        if (citta != null) {
            Vincoli.testo(citta, 2, 50);
            if (!citta.matches("[\\p{L} '\u2019-]+")) {
                throw new IllegalArgumentException("Citta non valida");
            }
        }
        if (provincia != null && !provincia.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Provincia non valida");
        }
        if (tipologiaAttivita != null) {
            Vincoli.testo(tipologiaAttivita, 1, Integer.MAX_VALUE);
        }
        if (caratteristicheAccessibilita == null) {
            caratteristicheAccessibilita = Set.of();
        }
        if (caratteristicheAccessibilita.stream().anyMatch(s -> s == null || s.isBlank())) {
            throw new IllegalArgumentException("Accessibilita non valida");
        }
        caratteristicheAccessibilita = Set.copyOf(caratteristicheAccessibilita);
    }
}
