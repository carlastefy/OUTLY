package it.unisa.outly.model;

import java.time.LocalDateTime;

public record DecisioneAmministrativa(Long idAmministratore, String motivazione, LocalDateTime dataOra) {
    public DecisioneAmministrativa {
        Vincoli.id(idAmministratore); Vincoli.testo(motivazione, 1, Integer.MAX_VALUE);
        Vincoli.richiesto(dataOra);
    }
}
