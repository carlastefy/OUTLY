package it.unisa.outly.application;

import it.unisa.outly.model.Attivita;
import it.unisa.outly.model.GruppoTemporaneo;
import it.unisa.outly.model.Prenotazione;
import it.unisa.outly.model.RichiestaOrganizzatore;
import it.unisa.outly.model.Segnalazione;
import it.unisa.outly.model.UtenteRegistrato;
import it.unisa.outly.model.Vincoli;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record ContestoOperazione(Collection<UtenteRegistrato> utenti, Collection<Attivita> attivita,
        Collection<Prenotazione> prenotazioni, Collection<RichiestaOrganizzatore> richiesteOrganizzatore,
        Collection<Segnalazione> segnalazioni, Collection<GruppoTemporaneo> gruppi,
        Map<TipoEntita, Long> nuoviIdentificatori, LocalDateTime adesso, Cataloghi cataloghi,
        Collection<SintesiAttivita> sintesiAttivita,
        Function<Long, Optional<Attivita>> dettagliAttivita) {
    public ContestoOperazione {
        Vincoli.richiesto(utenti); Vincoli.richiesto(attivita); Vincoli.richiesto(prenotazioni);
        Vincoli.richiesto(richiesteOrganizzatore); Vincoli.richiesto(segnalazioni); Vincoli.richiesto(gruppi);
        nuoviIdentificatori = Map.copyOf(nuoviIdentificatori);
        nuoviIdentificatori.values().forEach(Vincoli::id);
        Vincoli.richiesto(adesso); Vincoli.richiesto(cataloghi);
        Vincoli.richiesto(sintesiAttivita); Vincoli.richiesto(dettagliAttivita);
    }
    public Long nuovoId(TipoEntita tipo) {
        Long id = nuoviIdentificatori.get(Vincoli.richiesto(tipo));
        if (id == null) { throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE); }
        return id;
    }
    public Optional<UtenteRegistrato> profilo(Long id) {
        Vincoli.id(id);
        return utenti.stream().filter(u -> u.getIdUtente().equals(id)).findFirst();
    }
    public Optional<Attivita> attivitaPresente(Long id) {
        Vincoli.id(id);
        return attivita.stream().filter(a -> a.getIdAttivita().equals(id)).findFirst();
    }
    public Optional<Attivita> caricaDettaglio(Long id) {
        Vincoli.id(id);
        Optional<Attivita> risultato = dettagliAttivita.apply(id);
        if (risultato == null || (risultato.isPresent() && !risultato.get().getIdAttivita().equals(id))) { throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE); }
        return risultato;
    }
}
