package it.unisa.outly.model;

public final class Organizzatore extends Cliente {
    private final Long idAbilitazione;
    private final DatiEsercizio datiEsercizio;
    public Organizzatore(Long idUtente, DatiProfilo profilo, PreferenzeCliente preferenze,
                         int affidabilita, Long idAbilitazione, DatiEsercizio esercizio) {
        super(idUtente, profilo, preferenze, affidabilita);
        this.idAbilitazione = Vincoli.id(idAbilitazione);
        this.datiEsercizio = Vincoli.richiesto(esercizio);
    }
    public static Organizzatore daCliente(Cliente cliente, Long idAbilitazione, DatiEsercizio dati) {
        Vincoli.stato(!(cliente instanceof Organizzatore));
        return new Organizzatore(cliente.getIdUtente(), cliente.getDatiProfilo(), cliente.getPreferenze(),
                cliente.getGradoAffidabilita(), idAbilitazione, dati);
    }
    public Long getIdAbilitazione() { return idAbilitazione; }
    public DatiEsercizio getDatiEsercizio() { return datiEsercizio; }
    public String getPartitaIVA() { return datiEsercizio.partitaIVA(); }
    public String getNomeEsercizio() { return datiEsercizio.nomeEsercizio(); }
    public String getTipologiaEsercizio() { return datiEsercizio.tipologiaEsercizio(); }
    public String getIndirizzoEsercizio() { return datiEsercizio.indirizzoEsercizio(); }
    public String getDescrizioneEsercizio() { return datiEsercizio.descrizioneEsercizio(); }
    public String getDocumentoGestioneEsercizio() { return datiEsercizio.documentoGestioneEsercizio(); }
}
