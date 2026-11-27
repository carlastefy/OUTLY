package it.unisa.outly.dto;

import java.math.BigDecimal;

public final class ActivitySummary {
    public Long idAttivita;
    public String titolo;
    public String data;
    public String ora;
    public Luogo luogo;
    public BigDecimal costo;
    public String stato;

    public static final class Luogo {
        public String indirizzo;
        public String citta;
        public String provincia;
    }
}
