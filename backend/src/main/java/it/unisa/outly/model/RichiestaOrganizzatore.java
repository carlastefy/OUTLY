package it.unisa.outly.model;

import java.time.LocalDateTime;
import java.util.Optional;

public final class RichiestaOrganizzatore {
    private final Long idRichiestaOrganizzatore;
    private final Long idCliente;
    private DatiEsercizio datiEsercizio;
    private final LocalDateTime dataOraRichiesta;
    private StatoRichiestaOrganizzatore stato;
    private DecisioneAmministrativa decisione;
    public RichiestaOrganizzatore(Long id, Long idCliente, DatiEsercizio dati, LocalDateTime dataOra) {
        this(id, idCliente, Vincoli.richiesto(dati), dataOra, StatoRichiestaOrganizzatore.IN_ATTESA_DI_VERIFICA, null);
    }
    public RichiestaOrganizzatore(Long id, Long idCliente, DatiEsercizio dati, LocalDateTime dataOra,
                                 StatoRichiestaOrganizzatore stato, DecisioneAmministrativa decisione) {
        this.idRichiestaOrganizzatore = Vincoli.id(id); this.idCliente = Vincoli.id(idCliente);
        this.datiEsercizio = dati; this.dataOraRichiesta = Vincoli.richiesto(dataOra);
        this.stato = Vincoli.richiesto(stato);
        if ((stato == StatoRichiestaOrganizzatore.IN_ATTESA_DI_VERIFICA) != (decisione == null)) { throw new IllegalArgumentException("Esito richiesta incoerente"); }
        this.decisione = decisione;
    }
    public Long getIdRichiestaOrganizzatore() { return idRichiestaOrganizzatore; }
    public Long getIdCliente() { return idCliente; }
    public Optional<DatiEsercizio> getDatiEsercizio() { return Optional.ofNullable(datiEsercizio); }
    public void scollegaDatiPersonali() { datiEsercizio = null; }
    public LocalDateTime getDataOraRichiesta() { return dataOraRichiesta; }
    public StatoRichiestaOrganizzatore getStato() { return stato; }
    public Optional<DecisioneAmministrativa> getDecisione() { return Optional.ofNullable(decisione); }
    public void approva(DecisioneAmministrativa esito) {
        Vincoli.stato(datiEsercizio != null); decidi(StatoRichiestaOrganizzatore.APPROVATA, esito);
    }
    public void rifiuta(DecisioneAmministrativa esito) { decidi(StatoRichiestaOrganizzatore.RIFIUTATA, esito); }
    private void decidi(StatoRichiestaOrganizzatore nuovoStato, DecisioneAmministrativa esito) {
        Vincoli.stato(stato == StatoRichiestaOrganizzatore.IN_ATTESA_DI_VERIFICA);
        decisione = Vincoli.richiesto(esito); stato = nuovoStato;
    }
    public String getNomeEsercizio() { return datiEsercizio == null ? null : datiEsercizio.nomeEsercizio(); }
    public String getTipologiaEsercizio() { return datiEsercizio == null ? null : datiEsercizio.tipologiaEsercizio(); }
    public String getIndirizzo() { return datiEsercizio == null ? null : datiEsercizio.indirizzoEsercizio(); }
    public String getPartitaIVA() { return datiEsercizio == null ? null : datiEsercizio.partitaIVA(); }
    public String getDescrizioneEsercizio() { return datiEsercizio == null ? null : datiEsercizio.descrizioneEsercizio(); }
    public String getDocumento() { return datiEsercizio == null ? null : datiEsercizio.documentoGestioneEsercizio(); }
}
