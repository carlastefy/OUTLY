package it.unisa.outly.dto;

import java.math.BigDecimal;
import java.util.List;

public final class ActivityDetail {
    public Long idAttivita;
    public Long idOrganizzatore;
    public String titolo;
    public String foto;
    public String descrizione;
    public String data;
    public String ora;
    public ActivitySummary.Luogo luogo;
    public BigDecimal costo;
    public Integer durataMinuti;
    public String preferenzeSocialita;
    public List<String> caratteristicheAccessibilita;
    public String tipologiaAttivita;
    public String difficoltaTecnica;
    public String difficoltaFisica;
    public Integer numeroMassimoPartecipanti;
    public Long numeroPrenotazioni;
    public String stato;
    public Boolean prenotabile;
}
