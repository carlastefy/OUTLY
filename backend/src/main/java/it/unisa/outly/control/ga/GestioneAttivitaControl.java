package it.unisa.outly.control.ga;

import it.unisa.outly.api.ga.RisposteAttivita.Dettaglio;
import it.unisa.outly.api.ga.RisposteAttivita.Sintesi;
import it.unisa.outly.application.Attori;
import it.unisa.outly.application.CicloVita;
import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.Collegamenti;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.application.SintesiAttivita;
import it.unisa.outly.application.TipoEntita;
import it.unisa.outly.model.Attivita;
import it.unisa.outly.model.DatiAttivita;
import it.unisa.outly.model.Organizzatore;
import it.unisa.outly.model.Vincoli;
import java.util.List;

public final class GestioneAttivitaControl {
    public Dettaglio inserisci(ContestoOperazione c, Long idAttore, DatiAttivita dati) {
        Organizzatore autore = Attori.organizzatore(c, idAttore);
        c.cataloghi().valida(Vincoli.richiesto(dati));
        if (!dati.data().atTime(dati.ora()).isAfter(c.adesso())) {
            throw errore(CodiceErrore.DATI_NON_VALIDI);
        }
        Long id = c.nuovoId(TipoEntita.ATTIVITA);
        if (c.attivitaPresente(id).isPresent()
                || c.sintesiAttivita().stream().anyMatch(s -> s.idAttivita().equals(id))
                || c.prenotazioni().stream().anyMatch(p -> p.getIdAttivita().equals(id))
                || c.gruppi().stream().anyMatch(g -> g.getIdAttivita().equals(id))
                || c.segnalazioni().stream().anyMatch(s -> s.getIdAttivita().equals(id))) {
            throw errore(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        Attivita nuova = new Attivita(id, autore.getIdUtente(), dati);
        c.attivita().add(nuova);
        aggiornaSintesi(c, nuova);
        return Dettaglio.da(nuova, c, idAttore);
    }

    public Dettaglio modifica(ContestoOperazione c, Long idAttore, Long id, DatiAttivita dati) {
        Attori.organizzatore(c, idAttore);
        Attivita a = posseduta(c, idAttore, id);
        if (!CicloVita.modificabile(a, c.adesso())) {
            throw errore(CodiceErrore.CONFLITTO);
        }
        c.cataloghi().valida(Vincoli.richiesto(dati));
        if (c.adesso().isAfter(dati.data().atTime(dati.ora()).minusHours(48))) {
            throw errore(CodiceErrore.CONFLITTO);
        }
        if (dati.numeroMassimoPartecipanti() < a.getNumeroPrenotazioni(c.prenotazioni())) {
            throw errore(CodiceErrore.CONFLITTO);
        }
        a.aggiornaDati(dati);
        sincronizzaGruppi(c, a);
        aggiornaSintesi(c, a);
        return Dettaglio.da(a, c, idAttore);
    }

    public void elimina(ContestoOperazione c, Long idAttore, Long id) {
        Attori.organizzatore(c, idAttore);
        Attivita a = posseduta(c, idAttore, id);
        if (c.prenotazioni().stream().anyMatch(p -> p.attivaPer(a, c.adesso()))) {
            throw errore(CodiceErrore.CONFLITTO);
        }
        Collegamenti.scollegaAttivita(c, id);
    }

    public List<Sintesi> elenco(ContestoOperazione c, Long idAttore) {
        Attori.cliente(c, idAttore);
        return c.sintesiAttivita().stream().filter(s -> s.visibileAl(c.adesso()))
                .map(s -> new AttivitaProxy(s, () -> c.caricaDettaglio(s.idAttivita())))
                .map(p -> Sintesi.da(p.sintesi())).toList();
    }

    public Dettaglio dettaglio(ContestoOperazione c, Long idAttore, Long id) {
        Attori.cliente(c, idAttore);
        Vincoli.id(id);
        SintesiAttivita s = c.sintesiAttivita().stream().filter(x -> x.idAttivita().equals(id)).findFirst()
                .orElseThrow(() -> errore(CodiceErrore.NON_TROVATO));
        Attivita a = new AttivitaProxy(s, () -> c.caricaDettaglio(id)).dettaglio();
        sincronizzaGruppi(c, a);
        return Dettaglio.da(a, c, idAttore);
    }

    private Attivita posseduta(ContestoOperazione c, Long idAttore, Long id) {
        Attivita a = c.attivitaPresente(Vincoli.id(id))
                .orElseThrow(() -> errore(CodiceErrore.NON_TROVATO));
        if (!a.getIdOrganizzatore().equals(idAttore)) {
            throw errore(CodiceErrore.VIETATO);
        }
        return a;
    }
    private void sincronizzaGruppi(ContestoOperazione c, Attivita a) {
        a.aggiornaStato(c.adesso());
        c.gruppi().stream().filter(g -> g.getIdAttivita().equals(a.getIdAttivita()))
                .forEach(g -> CicloVita.sincronizzaGruppo(a, g, c.prenotazioni(), c.adesso()));
    }
    private void aggiornaSintesi(ContestoOperazione c, Attivita a) {
        c.sintesiAttivita().removeIf(s -> s.idAttivita().equals(a.getIdAttivita()));
        c.sintesiAttivita().add(new SintesiAttivita(a.getIdAttivita(), a.getTitolo(), a.getData(),
                a.getOra(), a.getLuogo(), a.getCosto(), a.statoAl(c.adesso())));
    }
    private static EccezioneApplicativa errore(CodiceErrore codice) { return new EccezioneApplicativa(codice); }
}
