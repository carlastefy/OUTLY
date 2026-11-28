package it.unisa.outly.api.ga;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import it.unisa.outly.control.ga.CriteriRicerca;
import it.unisa.outly.model.Livello;
import java.math.BigDecimal;
import java.util.Set;

public record RichiestaRicerca(BigDecimal budgetMax, String citta, String provincia,
        Set<String> caratteristicheAccessibilita, String tipologiaAttivita,
        Livello preferenzeSocialita, Livello difficoltaTecnica, Livello difficoltaFisica) {
    public RichiestaRicerca {
        CriteriRicerca validati = new CriteriRicerca(budgetMax, citta, provincia,
                caratteristicheAccessibilita, tipologiaAttivita,
                preferenzeSocialita, difficoltaTecnica, difficoltaFisica);
        caratteristicheAccessibilita = validati.caratteristicheAccessibilita();
    }
    public CriteriRicerca dominio() {
        return new CriteriRicerca(budgetMax, citta, provincia, caratteristicheAccessibilita,
                tipologiaAttivita, preferenzeSocialita, difficoltaTecnica, difficoltaFisica);
    }
    @JsonAnySetter
    public void campoSconosciuto(String nome, Object valore) {
        throw new IllegalArgumentException("Campo non ammesso");
    }
}
