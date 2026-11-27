package it.unisa.outly.model;

import java.time.LocalDateTime;
import java.util.Optional;

public final class Segnalazione {
    private final Long idSegnalazione;
    private final Long idSegnalante;
    private final Long idSegnalato;
    private final Long idAttivita;
    private final String descrizione;
    private final LocalDateTime dataOraSegnalazione;
    private StatoSegnalazione stato;
    private DecisioneAmministrativa decisione;
    public Segnalazione(Long id, Long segnalante, Long segnalato, Long attivita,
                        String descrizione, LocalDateTime dataOra) {
        this(id, segnalante, segnalato, attivita, descrizione, dataOra, StatoSegnalazione.DA_ESAMINARE, null);
    }
    public Segnalazione(Long id, Long segnalante, Long segnalato, Long attivita, String descrizione,
                        LocalDateTime dataOra, StatoSegnalazione stato, DecisioneAmministrativa decisione) {
        idSegnalazione = Vincoli.id(id); idSegnalante = Vincoli.id(segnalante);
        idSegnalato = Vincoli.id(segnalato); idAttivita = Vincoli.id(attivita);
        if (segnalante.equals(segnalato)) { throw new IllegalArgumentException("Non puoi segnalare te stesso"); }
        this.descrizione = Vincoli.testo(descrizione, 1, Integer.MAX_VALUE);
        dataOraSegnalazione = Vincoli.richiesto(dataOra); this.stato = Vincoli.richiesto(stato);
        if ((stato == StatoSegnalazione.DA_ESAMINARE) != (decisione == null)) { throw new IllegalArgumentException("Esito segnalazione incoerente"); }
        this.decisione = decisione;
    }
    public Long getIdSegnalazione() { return idSegnalazione; }
    public Long getIdSegnalante() { return idSegnalante; }
    public Long getIdSegnalato() { return idSegnalato; }
    public Long getIdAttivita() { return idAttivita; }
    public String getDescrizione() { return descrizione; }
    public LocalDateTime getDataOraSegnalazione() { return dataOraSegnalazione; }
    public StatoSegnalazione getStato() { return stato; }
    public Optional<DecisioneAmministrativa> getDecisione() { return Optional.ofNullable(decisione); }
    public void conferma(DecisioneAmministrativa esito, Optional<Cliente> cliente) {
        Vincoli.stato(stato == StatoSegnalazione.DA_ESAMINARE);
        Vincoli.richiesto(esito); Vincoli.richiesto(cliente);
        cliente.ifPresent(c -> { if (!idSegnalato.equals(c.getIdUtente())) { throw new IllegalArgumentException("Destinatario non corrispondente"); } });
        cliente.ifPresent(Cliente::applicaMalusSegnalazione);
        decisione = esito; stato = StatoSegnalazione.CONFERMATA;
    }
    public void archivia(DecisioneAmministrativa esito) {
        Vincoli.stato(stato == StatoSegnalazione.DA_ESAMINARE);
        decisione = Vincoli.richiesto(esito); stato = StatoSegnalazione.ARCHIVIATA;
    }
}
