package it.unisa.outly.model;

import java.util.Set;

public sealed class Cliente extends UtenteRegistrato permits Organizzatore {
    private int gradoAffidabilita;
    private PreferenzeCliente preferenze;
    public Cliente(Long idUtente, DatiProfilo datiProfilo) {
        this(idUtente, datiProfilo, PreferenzeCliente.nonSelezionate(), 10);
    }
    public Cliente(Long idUtente, DatiProfilo datiProfilo, PreferenzeCliente preferenze, int gradoAffidabilita) {
        super(idUtente, datiProfilo);
        if (gradoAffidabilita < 0 || gradoAffidabilita > 10) { throw new IllegalArgumentException("Affidabilita non valida"); }
        this.preferenze = Vincoli.richiesto(preferenze);
        this.gradoAffidabilita = gradoAffidabilita;
    }
    public int getGradoAffidabilita() { return gradoAffidabilita; }
    public PreferenzeCliente getPreferenze() { return preferenze; }
    public Set<String> getInteressi() { return preferenze.interessi(); }
    public Livello getPreferenzeSocialita() { return preferenze.preferenzeSocialita(); }
    public Set<String> getCaratteristicheAccessibilita() { return preferenze.caratteristicheAccessibilita(); }
    public void aggiornaPreferenze(PreferenzeCliente dati) { preferenze = Vincoli.richiesto(dati); }
    void applicaMalusSegnalazione() { gradoAffidabilita = Math.max(0, gradoAffidabilita - 1); }
}
