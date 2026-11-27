package it.unisa.outly.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class GruppoTemporaneo {
    private final Long idGruppoTemporaneo;
    private final Long idAttivita;
    private final Set<Long> idPartecipanti;
    private StatoGruppo stato;
    public GruppoTemporaneo(Long id, Long attivita) { this(id, attivita, StatoGruppo.APERTO, Set.of()); }
    public GruppoTemporaneo(Long id, Long attivita, StatoGruppo stato, Collection<Long> partecipanti) {
        idGruppoTemporaneo = Vincoli.id(id); idAttivita = Vincoli.id(attivita);
        this.stato = Vincoli.richiesto(stato);
        idPartecipanti = new LinkedHashSet<>();
        partecipanti.forEach(p -> idPartecipanti.add(Vincoli.id(p)));
        if (stato == StatoGruppo.CHIUSO && !idPartecipanti.isEmpty()) { throw new IllegalArgumentException("Un gruppo chiuso non ha partecipanti attivi"); }
    }
    public Long getIdGruppoTemporaneo() { return idGruppoTemporaneo; }
    public Long getIdAttivita() { return idAttivita; }
    public StatoGruppo getStato() { return stato; }
    public Set<Long> getIdPartecipanti() { return Set.copyOf(idPartecipanti); }
    public void sincronizzaPartecipanti(Collection<Long> ids) {
        Vincoli.stato(stato == StatoGruppo.APERTO);
        Set<Long> nuovi = new LinkedHashSet<>(); ids.forEach(id -> nuovi.add(Vincoli.id(id)));
        idPartecipanti.clear(); idPartecipanti.addAll(nuovi);
    }
    public void rimuoviPartecipante(Long id) { idPartecipanti.remove(Vincoli.id(id)); }
    public void chiudi() { idPartecipanti.clear(); stato = StatoGruppo.CHIUSO; }
}
