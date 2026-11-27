package it.unisa.outly.model;

import java.time.LocalDateTime;

public final class Prenotazione {
    private final Long idPrenotazione;
    private final Long idCliente;
    private final Long idAttivita;
    private LocalDateTime dataOraPrenotazione;
    private StatoPrenotazione stato;
    public Prenotazione(Long idPrenotazione, Long idCliente, Long idAttivita, LocalDateTime dataOra) {
        this(idPrenotazione, idCliente, idAttivita, dataOra, StatoPrenotazione.CONFERMATA);
    }
    public Prenotazione(Long idPrenotazione, Long idCliente, Long idAttivita,
                         LocalDateTime dataOra, StatoPrenotazione stato) {
        this.idPrenotazione = Vincoli.id(idPrenotazione); this.idCliente = Vincoli.id(idCliente);
        this.idAttivita = Vincoli.id(idAttivita); this.dataOraPrenotazione = Vincoli.richiesto(dataOra);
        this.stato = Vincoli.richiesto(stato);
    }
    public Long getIdPrenotazione() { return idPrenotazione; }
    public Long getIdCliente() { return idCliente; }
    public Long getIdAttivita() { return idAttivita; }
    public LocalDateTime getDataOraPrenotazione() { return dataOraPrenotazione; }
    public StatoPrenotazione getStato() { return stato; }
    public void annulla() { Vincoli.stato(stato == StatoPrenotazione.CONFERMATA); stato = StatoPrenotazione.ANNULLATA; }
    public void riattiva(LocalDateTime adesso) {
        Vincoli.stato(stato == StatoPrenotazione.ANNULLATA);
        dataOraPrenotazione = Vincoli.richiesto(adesso); stato = StatoPrenotazione.CONFERMATA;
    }
    public boolean attivaPer(Attivita attivita, LocalDateTime adesso) {
        if (!idAttivita.equals(attivita.getIdAttivita())) { return false; }
        StatoAttivita effettivo = attivita.statoAl(adesso);
        return stato == StatoPrenotazione.CONFERMATA && effettivo != StatoAttivita.CONCLUSA
                && effettivo != StatoAttivita.ANNULLATA;
    }
}
