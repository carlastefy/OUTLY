package it.unisa.outly.model;

public record Luogo(String indirizzo, String citta, String provincia) {
    public Luogo {
        Vincoli.testo(indirizzo, 2, 100);
        Vincoli.testo(citta, 2, 50);
        if (!citta.matches("[\\p{L} '\u2019-]+")) { throw new IllegalArgumentException("Citta non valida"); }
        if (provincia == null || !provincia.matches("[A-Z]{2}")) { throw new IllegalArgumentException("Provincia non valida"); }
    }
    @Override public String toString() { return indirizzo + ", " + citta + ", (" + provincia + ")"; }
}
