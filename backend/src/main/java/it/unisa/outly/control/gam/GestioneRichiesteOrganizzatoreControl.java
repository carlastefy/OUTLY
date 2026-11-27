package it.unisa.outly.control.gam;

import it.unisa.outly.application.Attori;
import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.Collegamenti;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.application.TipoEntita;
import it.unisa.outly.model.Amministratore;
import it.unisa.outly.model.Cliente;
import it.unisa.outly.model.DatiEsercizio;
import it.unisa.outly.model.DecisioneAmministrativa;
import it.unisa.outly.model.Organizzatore;
import it.unisa.outly.model.RichiestaOrganizzatore;
import it.unisa.outly.model.StatoRichiestaOrganizzatore;
import it.unisa.outly.model.UtenteRegistrato;
import it.unisa.outly.model.Vincoli;
import java.util.Comparator;
import java.util.List;

public final class GestioneRichiesteOrganizzatoreControl {
    public enum Decisione { APPROVA, RIFIUTA }

    public List<RichiestaOrganizzatore> consulta(ContestoOperazione contesto, Long idAttore) {
        Attori.amministratore(contesto, idAttore);
        return contesto.richiesteOrganizzatore().stream()
                .sorted(Comparator.comparing(RichiestaOrganizzatore::getIdRichiestaOrganizzatore)).toList();
    }

    public RichiestaOrganizzatore valuta(ContestoOperazione contesto, Long idAttore,
            Long idRichiesta, Decisione decisione, String motivazione) {
        Amministratore amministratore = Attori.amministratore(contesto, idAttore);
        Vincoli.id(idRichiesta);
        Vincoli.richiesto(decisione);
        DecisioneAmministrativa esito = new DecisioneAmministrativa(
                amministratore.getIdUtente(), motivazione, contesto.adesso());
        RichiestaOrganizzatore richiesta = contesto.richiesteOrganizzatore().stream()
                .filter(r -> r.getIdRichiestaOrganizzatore().equals(idRichiesta)).findFirst()
                .orElseThrow(() -> new EccezioneApplicativa(CodiceErrore.NON_TROVATO));
        if (richiesta.getStato() != StatoRichiestaOrganizzatore.IN_ATTESA_DI_VERIFICA) {
            throw new EccezioneApplicativa(CodiceErrore.CONFLITTO);
        }
        if (decisione == Decisione.RIFIUTA) {
            richiesta.rifiuta(esito);
            return richiesta;
        }
        UtenteRegistrato profilo = contesto.profilo(richiesta.getIdCliente())
                .orElseThrow(() -> new EccezioneApplicativa(CodiceErrore.CONFLITTO));
        if (!(profilo instanceof Cliente cliente) || cliente instanceof Organizzatore) {
            throw new EccezioneApplicativa(CodiceErrore.CONFLITTO);
        }
        DatiEsercizio dati = richiesta.getDatiEsercizio()
                .orElseThrow(() -> new EccezioneApplicativa(CodiceErrore.CONFLITTO));
        contesto.cataloghi().valida(dati);
        Long idAbilitazione = contesto.nuovoId(TipoEntita.ABILITAZIONE);
        if (contesto.utenti().stream().filter(Organizzatore.class::isInstance)
                .map(Organizzatore.class::cast)
                .anyMatch(o -> o.getIdAbilitazione().equals(idAbilitazione))) {
            throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        Organizzatore promosso = Organizzatore.daCliente(cliente, idAbilitazione, dati);
        Collegamenti.sostituisciCliente(contesto, cliente, promosso);
        richiesta.approva(esito);
        return richiesta;
    }
}
