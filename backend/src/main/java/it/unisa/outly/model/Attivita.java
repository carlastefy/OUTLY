package it.unisa.outly.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public final class Attivita {
    private final Long idAttivita;
    private final Long idOrganizzatore;
    private DatiAttivita dati;
    private StatoAttivita stato;
    public Attivita(Long idAttivita, Long idOrganizzatore, DatiAttivita dati) {
        this(idAttivita, idOrganizzatore, dati, StatoAttivita.PUBBLICATA);
    }
    public Attivita(Long idAttivita, Long idOrganizzatore, DatiAttivita dati, StatoAttivita stato) {
        this.idAttivita = Vincoli.id(idAttivita); this.idOrganizzatore = Vincoli.id(idOrganizzatore);
        this.dati = Vincoli.richiesto(dati); this.stato = Vincoli.richiesto(stato);
    }
    public Long getIdAttivita() { return idAttivita; }
    public Long getIdOrganizzatore() { return idOrganizzatore; }
    public DatiAttivita getDati() { return dati; }
    public StatoAttivita getStato() { return stato; }
    public LocalDateTime getInizio() { return dati.data().atTime(dati.ora()); }
    public LocalDateTime getFine() { return getInizio().plusMinutes(dati.durataMinuti()); }
    public StatoAttivita statoAl(LocalDateTime adesso) {
        Vincoli.richiesto(adesso);
        if (stato == StatoAttivita.ANNULLATA || stato == StatoAttivita.CONCLUSA) { return stato; }
        if (!adesso.isBefore(getFine())) { return StatoAttivita.CONCLUSA; }
        if (!adesso.isBefore(getInizio()) || stato == StatoAttivita.IN_CORSO) { return StatoAttivita.IN_CORSO; }
        return StatoAttivita.PUBBLICATA;
    }
    public void aggiornaStato(LocalDateTime adesso) { stato = statoAl(adesso); }
    public void aggiornaDati(DatiAttivita nuoviDati) {
        Vincoli.stato(stato == StatoAttivita.PUBBLICATA);
        dati = Vincoli.richiesto(nuoviDati);
    }
    public void annulla(LocalDateTime adesso) {
        Vincoli.stato(statoAl(adesso) == StatoAttivita.PUBBLICATA); stato = StatoAttivita.ANNULLATA;
    }
    public long getNumeroPrenotazioni(Collection<Prenotazione> prenotazioni) {
        return prenotazioni.stream().filter(p -> p.getIdAttivita().equals(idAttivita)
                && p.getStato() == StatoPrenotazione.CONFERMATA).count();
    }
    public String getTitolo() { return dati.titolo(); }
    public String getFoto() { return dati.foto(); }
    public String getDescrizione() { return dati.descrizione(); }
    public LocalDate getData() { return dati.data(); }
    public LocalTime getOra() { return dati.ora(); }
    public Luogo getLuogo() { return dati.luogo(); }
    public BigDecimal getCosto() { return dati.costo(); }
    public int getDurataMinuti() { return dati.durataMinuti(); }
    public Livello getPreferenzeSocialita() { return dati.preferenzeSocialita(); }
    public Set<String> getCaratteristicheAccessibilita() { return dati.caratteristicheAccessibilita(); }
    public String getTipologiaAttivita() { return dati.tipologiaAttivita(); }
    public Livello getDifficoltaTecnica() { return dati.difficoltaTecnica(); }
    public Livello getDifficoltaFisica() { return dati.difficoltaFisica(); }
    public int getNumeroMassimoPartecipanti() { return dati.numeroMassimoPartecipanti(); }
}
