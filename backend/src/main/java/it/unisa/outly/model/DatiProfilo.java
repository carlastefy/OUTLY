package it.unisa.outly.model;

public record DatiProfilo(String nome, String cognome, String email,
                          String passwordCodificata, String telefono) {
    public DatiProfilo {
        Vincoli.testo(nome, 1, Integer.MAX_VALUE);
        Vincoli.testo(cognome, 1, Integer.MAX_VALUE);
        Vincoli.testo(email, 1, Integer.MAX_VALUE);
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) { throw new IllegalArgumentException("Email non valida"); }
        Vincoli.testo(passwordCodificata, 1, 512);
        if (!passwordCodificata.matches("pbkdf2-sha256\\$[0-9]+\\$[A-Za-z0-9+/]+={0,2}\\$[A-Za-z0-9+/]+={0,2}")) { throw new IllegalArgumentException("Formato credenziale codificata non valido"); }
        Vincoli.testo(telefono, 1, Integer.MAX_VALUE);
    }
    @Override public String toString() { return "DatiProfilo[riservati]"; }
}
