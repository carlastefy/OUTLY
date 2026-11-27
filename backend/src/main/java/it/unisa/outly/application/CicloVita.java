package it.unisa.outly.application;

import it.unisa.outly.model.Attivita;
import it.unisa.outly.model.GruppoTemporaneo;
import it.unisa.outly.model.Prenotazione;
import it.unisa.outly.model.StatoAttivita;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class CicloVita {
    private CicloVita() { }
    public static boolean catalogabile(Attivita a, LocalDateTime adesso) {
        return a.statoAl(adesso) == StatoAttivita.PUBBLICATA && a.getInizio().isAfter(adesso);
    }
    public static boolean entroAnticipo(Attivita a, LocalDateTime adesso, long ore) {
        if (ore < 0) { throw new IllegalArgumentException("Anticipo non valido"); }
        return !adesso.isAfter(a.getInizio().minusHours(ore));
    }
    public static boolean prenotabile(Attivita a, Collection<Prenotazione> prenotazioni, LocalDateTime adesso) {
        return catalogabile(a, adesso) && entroAnticipo(a, adesso, 48)
                && a.getNumeroMassimoPartecipanti() > a.getNumeroPrenotazioni(prenotazioni);
    }
    public static boolean modificabile(Attivita a, LocalDateTime adesso) {
        return catalogabile(a, adesso) && entroAnticipo(a, adesso, 48);
    }
    public static boolean annullabile(Prenotazione p, Attivita a, LocalDateTime adesso) {
        return p.attivaPer(a, adesso) && entroAnticipo(a, adesso, 24);
    }
    public static void sincronizzaGruppo(Attivita a, GruppoTemporaneo gruppo,
                                        Collection<Prenotazione> prenotazioni, LocalDateTime adesso) {
        if (!a.getIdAttivita().equals(gruppo.getIdAttivita())) { throw new IllegalArgumentException("Gruppo non corrispondente"); }
        a.aggiornaStato(adesso);
        if (a.getStato() == StatoAttivita.CONCLUSA || a.getStato() == StatoAttivita.ANNULLATA) {
            gruppo.chiudi(); return;
        }
        Set<Long> ids = prenotazioni.stream().filter(p -> p.attivaPer(a, adesso))
                .map(Prenotazione::getIdCliente).collect(Collectors.toSet());
        gruppo.sincronizzaPartecipanti(ids);
    }
}
