package it.unisa.outly.control.gp;

import it.unisa.outly.application.Attori;
import it.unisa.outly.application.CicloVita;
import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.application.TipoEntita;
import it.unisa.outly.model.Attivita;
import it.unisa.outly.model.Cliente;
import it.unisa.outly.model.GruppoTemporaneo;
import it.unisa.outly.model.Organizzatore;
import it.unisa.outly.model.Prenotazione;
import it.unisa.outly.model.StatoAttivita;
import it.unisa.outly.model.StatoGruppo;
import it.unisa.outly.model.StatoPrenotazione;
import it.unisa.outly.model.UtenteRegistrato;
import it.unisa.outly.model.Vincoli;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class PrenotazioneControl {
    public EsitoPrenotazione prenota(ContestoOperazione contesto, Long idAttore, Long idAttivita) {
        Cliente cliente = Attori.cliente(contesto, idAttore);
        Attivita attivita = attivita(contesto, idAttivita);
        List<Prenotazione> righe = righeValide(contesto, attivita);
        Optional<GruppoTemporaneo> gruppo = gruppoValido(contesto, attivita);
        Optional<Prenotazione> precedente = righe.stream()
                .filter(p -> p.getIdCliente().equals(cliente.getIdUtente())).findFirst();
        if (precedente.isPresent() && precedente.get().getStato() == StatoPrenotazione.CONFERMATA) {
            throw errore(CodiceErrore.CONFLITTO);
        }
        if (!CicloVita.prenotabile(attivita, righe, contesto.adesso())) {
            throw errore(CodiceErrore.CONFLITTO);
        }
        if (!(contesto.profilo(attivita.getIdOrganizzatore()).orElse(null) instanceof Organizzatore)) {
            throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        if (gruppo.isEmpty() && !righe.isEmpty()) {
            throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        Prenotazione prenotazione = precedente.orElse(null);
        boolean nuova = prenotazione == null;
        if (nuova) {
            Long id = contesto.nuovoId(TipoEntita.PRENOTAZIONE);
            if (contesto.prenotazioni().stream().anyMatch(p -> p.getIdPrenotazione().equals(id))) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
            prenotazione = new Prenotazione(id, cliente.getIdUtente(), idAttivita, contesto.adesso());
        }
        GruppoTemporaneo destinazione = gruppo.orElse(null);
        if (destinazione == null) {
            Long id = contesto.nuovoId(TipoEntita.GRUPPO_TEMPORANEO);
            if (contesto.gruppi().stream().anyMatch(g -> g.getIdGruppoTemporaneo().equals(id))) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
            destinazione = new GruppoTemporaneo(id, idAttivita);
        }
        if (nuova) {
            contesto.prenotazioni().add(prenotazione);
        } else {
            prenotazione.riattiva(contesto.adesso());
        }
        if (gruppo.isEmpty()) {
            contesto.gruppi().add(destinazione);
        }
        CicloVita.sincronizzaGruppo(attivita, destinazione, contesto.prenotazioni(), contesto.adesso());
        return new EsitoPrenotazione(dettaglio(prenotazione), nuova);
    }

    public DettaglioPrenotazione annulla(ContestoOperazione contesto, Long idAttore, Long idPrenotazione) {
        Cliente cliente = Attori.cliente(contesto, idAttore);
        Vincoli.id(idPrenotazione);
        List<Prenotazione> trovate = contesto.prenotazioni().stream()
                .filter(p -> p.getIdPrenotazione().equals(idPrenotazione)).toList();
        if (trovate.isEmpty()) {
            throw errore(CodiceErrore.NON_TROVATO);
        }
        if (trovate.size() != 1) {
            throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        Prenotazione prenotazione = trovate.get(0);
        if (!prenotazione.getIdCliente().equals(cliente.getIdUtente())) {
            throw errore(CodiceErrore.VIETATO);
        }
        Attivita attivita = attivita(contesto, prenotazione.getIdAttivita());
        righeValide(contesto, attivita);
        Optional<GruppoTemporaneo> gruppo = gruppoValido(contesto, attivita);
        if (!CicloVita.annullabile(prenotazione, attivita, contesto.adesso())) {
            throw errore(CodiceErrore.CONFLITTO);
        }
        GruppoTemporaneo destinazione = gruppo.orElseThrow(
                () -> errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE));
        prenotazione.annulla();
        CicloVita.sincronizzaGruppo(attivita, destinazione, contesto.prenotazioni(), contesto.adesso());
        return dettaglio(prenotazione);
    }

    public VistaPrenotati visualizzaPrenotati(ContestoOperazione contesto, Long idAttore, Long idAttivita) {
        Cliente cliente = Attori.cliente(contesto, idAttore);
        Attivita attivita = attivita(contesto, idAttivita);
        List<Prenotazione> righe = righeValide(contesto, attivita);
        boolean titolare = cliente instanceof Organizzatore
                && attivita.getIdOrganizzatore().equals(cliente.getIdUtente());
        if (!titolare && righe.stream().noneMatch(p -> p.getIdCliente().equals(cliente.getIdUtente())
                && p.attivaPer(attivita, contesto.adesso()))) {
            throw errore(CodiceErrore.VIETATO);
        }
        Optional<GruppoTemporaneo> gruppo = gruppoValido(contesto, attivita);
        if (!righe.isEmpty() && gruppo.isEmpty()) {
            throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        sincronizza(contesto, attivita, gruppo);
        if (titolare) {
            return new VistaOrganizzatore(idAttivita, righe.stream()
                    .map(p -> rigaOrganizzatore(contesto, attivita, p)).toList());
        }
        return new VistaCliente(idAttivita, righe.stream()
                .filter(p -> !p.getIdCliente().equals(cliente.getIdUtente()))
                .filter(p -> p.attivaPer(attivita, contesto.adesso()))
                .map(p -> partecipante(contesto, p)).toList());
    }

    public void sincronizzaGruppi(ContestoOperazione contesto) {
        Vincoli.richiesto(contesto);
        Set<Long> ids = new HashSet<>();
        Set<Long> attivitaViste = new HashSet<>();
        for (GruppoTemporaneo gruppo : contesto.gruppi()) {
            if (!ids.add(gruppo.getIdGruppoTemporaneo()) || !attivitaViste.add(gruppo.getIdAttivita())) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
            List<Attivita> corrispondenti = attivitaCorrispondenti(contesto, gruppo.getIdAttivita());
            if (corrispondenti.isEmpty() && gruppo.getStato() == StatoGruppo.CHIUSO) {
                continue;
            }
            if (corrispondenti.size() != 1) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
            Attivita attivita = corrispondenti.get(0);
            righeValide(contesto, attivita);
            gruppoValido(contesto, attivita);
        }
        for (GruppoTemporaneo gruppo : contesto.gruppi()) {
            contesto.attivitaPresente(gruppo.getIdAttivita()).ifPresent(a ->
                    CicloVita.sincronizzaGruppo(a, gruppo, contesto.prenotazioni(), contesto.adesso()));
        }
    }

    private Attivita attivita(ContestoOperazione contesto, Long id) {
        Vincoli.id(id);
        List<Attivita> trovate = attivitaCorrispondenti(contesto, id);
        if (trovate.isEmpty()) {
            throw errore(CodiceErrore.NON_TROVATO);
        }
        if (trovate.size() != 1) {
            throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        return trovate.get(0);
    }

    private List<Attivita> attivitaCorrispondenti(ContestoOperazione contesto, Long id) {
        return contesto.attivita().stream().filter(a -> a.getIdAttivita().equals(id)).toList();
    }

    private List<Prenotazione> righeValide(ContestoOperazione contesto, Attivita attivita) {
        Set<Long> ids = new HashSet<>();
        for (Prenotazione p : contesto.prenotazioni()) {
            if (!ids.add(p.getIdPrenotazione())) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
        }
        List<Prenotazione> righe = contesto.prenotazioni().stream()
                .filter(p -> p.getIdAttivita().equals(attivita.getIdAttivita())).toList();
        Set<Long> clienti = new HashSet<>();
        for (Prenotazione p : righe) {
            if (!clienti.add(p.getIdCliente())) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
        }
        return righe;
    }

    private Optional<GruppoTemporaneo> gruppoValido(ContestoOperazione contesto, Attivita attivita) {
        List<GruppoTemporaneo> trovati = contesto.gruppi().stream()
                .filter(g -> g.getIdAttivita().equals(attivita.getIdAttivita())).toList();
        if (trovati.size() > 1) {
            throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        Optional<GruppoTemporaneo> gruppo = trovati.stream().findFirst();
        if (gruppo.isPresent()) {
            GruppoTemporaneo g = gruppo.get();
            if (contesto.gruppi().stream().filter(x -> x.getIdGruppoTemporaneo()
                    .equals(g.getIdGruppoTemporaneo())).count() != 1) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
            StatoAttivita stato = attivita.statoAl(contesto.adesso());
            if (g.getStato() == StatoGruppo.CHIUSO
                    && stato != StatoAttivita.CONCLUSA && stato != StatoAttivita.ANNULLATA) {
                throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
        }
        return gruppo;
    }

    private void sincronizza(ContestoOperazione contesto, Attivita attivita,
                              Optional<GruppoTemporaneo> gruppo) {
        if (gruppo.isPresent()) {
            CicloVita.sincronizzaGruppo(attivita, gruppo.get(), contesto.prenotazioni(), contesto.adesso());
        } else {
            attivita.aggiornaStato(contesto.adesso());
        }
    }

    private Partecipante partecipante(ContestoOperazione contesto, Prenotazione p) {
        Optional<UtenteRegistrato> profilo = contesto.profilo(p.getIdCliente());
        return new Partecipante(p.getIdCliente(), profilo.map(UtenteRegistrato::getNome).orElse(null),
                profilo.map(UtenteRegistrato::getCognome).orElse(null));
    }

    private RigaOrganizzatore rigaOrganizzatore(ContestoOperazione contesto, Attivita attivita,
                                               Prenotazione p) {
        Optional<UtenteRegistrato> profilo = contesto.profilo(p.getIdCliente());
        Integer affidabilita = p.attivaPer(attivita, contesto.adesso())
                ? profilo.filter(Cliente.class::isInstance).map(Cliente.class::cast)
                        .map(Cliente::getGradoAffidabilita).orElse(null) : null;
        return new RigaOrganizzatore(p.getIdPrenotazione(), p.getIdCliente(),
                profilo.map(UtenteRegistrato::getNome).orElse(null),
                profilo.map(UtenteRegistrato::getCognome).orElse(null),
                profilo.map(UtenteRegistrato::getEmail).orElse(null),
                p.getDataOraPrenotazione(), p.getStato(), affidabilita);
    }

    private DettaglioPrenotazione dettaglio(Prenotazione p) {
        return new DettaglioPrenotazione(p.getIdPrenotazione(), p.getIdAttivita(),
                p.getDataOraPrenotazione(), p.getStato());
    }

    private EccezioneApplicativa errore(CodiceErrore codice) {
        return new EccezioneApplicativa(codice);
    }

    public record DettaglioPrenotazione(Long idPrenotazione, Long idAttivita,
                                       LocalDateTime dataOraPrenotazione, StatoPrenotazione stato) { }
    public record EsitoPrenotazione(DettaglioPrenotazione prenotazione, boolean nuova) { }
    public sealed interface VistaPrenotati permits VistaCliente, VistaOrganizzatore { }
    public record Partecipante(Long idCliente, String nome, String cognome) { }
    public record RigaOrganizzatore(Long idPrenotazione, Long idCliente, String nome, String cognome,
                                   String email, LocalDateTime dataOraPrenotazione,
                                   StatoPrenotazione stato, Integer gradoAffidabilita) { }
    public record VistaCliente(Long idAttivita, List<Partecipante> partecipanti) implements VistaPrenotati {
        public VistaCliente { partecipanti = List.copyOf(partecipanti); }
    }
    public record VistaOrganizzatore(Long idAttivita, List<RigaOrganizzatore> prenotazioni)
            implements VistaPrenotati {
        public VistaOrganizzatore { prenotazioni = List.copyOf(prenotazioni); }
    }
}
