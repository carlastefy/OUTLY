package it.unisa.outly.api.ga;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import it.unisa.outly.model.DatiAttivita;
import it.unisa.outly.model.Livello;
import it.unisa.outly.model.Luogo;
import it.unisa.outly.model.Vincoli;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record RichiestaAttivita(String titolo, String foto, String descrizione,
        @JsonFormat(pattern = "dd/MM/yyyy") LocalDate data,
        @JsonFormat(pattern = "HH:mm") LocalTime ora, LuogoRichiesta luogo, BigDecimal costo,
        Integer durataMinuti, Livello preferenzeSocialita, Set<String> caratteristicheAccessibilita,
        String tipologiaAttivita, Livello difficoltaTecnica, Livello difficoltaFisica,
        Integer numeroMassimoPartecipanti) {
    public RichiestaAttivita {
        Vincoli.richiesto(luogo); Vincoli.richiesto(durataMinuti);
        Vincoli.richiesto(numeroMassimoPartecipanti); Vincoli.richiesto(caratteristicheAccessibilita);
        if (caratteristicheAccessibilita.stream().anyMatch(s -> s == null)) {
            throw new IllegalArgumentException("Accessibilita non valida");
        }
        caratteristicheAccessibilita = Set.copyOf(caratteristicheAccessibilita);
        new DatiAttivita(titolo, foto, descrizione, data, ora, luogo.dominio(), costo, durataMinuti,
                preferenzeSocialita, caratteristicheAccessibilita, tipologiaAttivita,
                difficoltaTecnica, difficoltaFisica, numeroMassimoPartecipanti);
    }
    public DatiAttivita dominio() {
        return new DatiAttivita(titolo, foto, descrizione, data, ora, luogo.dominio(), costo, durataMinuti,
                preferenzeSocialita, caratteristicheAccessibilita, tipologiaAttivita,
                difficoltaTecnica, difficoltaFisica, numeroMassimoPartecipanti);
    }
    @JsonAnySetter
    public void campoSconosciuto(String nome, Object valore) {
        throw new IllegalArgumentException("Campo non ammesso");
    }
    public record LuogoRichiesta(String indirizzo, String citta, String provincia) {
        public LuogoRichiesta { new Luogo(indirizzo, citta, provincia); }
        public Luogo dominio() { return new Luogo(indirizzo, citta, provincia); }
        @JsonAnySetter
        public void campoSconosciuto(String nome, Object valore) {
            throw new IllegalArgumentException("Campo non ammesso");
        }
    }
}
