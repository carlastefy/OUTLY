package it.unisa.outly.dto;

import java.math.BigDecimal;
import java.util.Set;

public final class ActivitySearchRequest {
    public final BigDecimal budgetMax;
    public final String citta;
    public final String provincia;
    public final Set<String> caratteristicheAccessibilita;
    public final String tipologiaAttivita;
    public final String preferenzeSocialita;
    public final String difficoltaTecnica;
    public final String difficoltaFisica;

    public ActivitySearchRequest(BigDecimal budgetMax, String citta, String provincia,
                                 Set<String> caratteristicheAccessibilita,
                                 String tipologiaAttivita, String preferenzeSocialita,
                                 String difficoltaTecnica, String difficoltaFisica) {
        this.budgetMax = budgetMax;
        this.citta = citta;
        this.provincia = provincia;
        this.caratteristicheAccessibilita = caratteristicheAccessibilita;
        this.tipologiaAttivita = tipologiaAttivita;
        this.preferenzeSocialita = preferenzeSocialita;
        this.difficoltaTecnica = difficoltaTecnica;
        this.difficoltaFisica = difficoltaFisica;
    }
}
