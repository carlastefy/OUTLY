package it.unisa.outly.model;

public abstract sealed class UtenteRegistrato permits Cliente, Amministratore {
    private final Long idUtente;
    private DatiProfilo datiProfilo;
    protected UtenteRegistrato(Long idUtente, DatiProfilo datiProfilo) {
        this.idUtente = Vincoli.id(idUtente);
        this.datiProfilo = Vincoli.richiesto(datiProfilo);
    }
    public final Long getIdUtente() { return idUtente; }
    public final DatiProfilo getDatiProfilo() { return datiProfilo; }
    public final String getNome() { return datiProfilo.nome(); }
    public final String getCognome() { return datiProfilo.cognome(); }
    public final String getEmail() { return datiProfilo.email(); }
    public final String getPasswordCodificata() { return datiProfilo.passwordCodificata(); }
    public final String getTelefono() { return datiProfilo.telefono(); }
    public final void aggiornaProfilo(DatiProfilo dati) { datiProfilo = Vincoli.richiesto(dati); }
}
