package it.unisa.outly.control.gam;

import it.unisa.outly.application.Attori;
import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.model.Amministratore;
import it.unisa.outly.model.Cliente;
import it.unisa.outly.model.DecisioneAmministrativa;
import it.unisa.outly.model.Segnalazione;
import it.unisa.outly.model.StatoSegnalazione;
import it.unisa.outly.model.UtenteRegistrato;
import it.unisa.outly.model.Vincoli;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class GestioneSegnalazioneControl {
    public enum Decisione { CONFERMA, ARCHIVIA }

    public List<Segnalazione> consulta(ContestoOperazione contesto, Long idAttore) {
        Attori.amministratore(contesto, idAttore);
        return contesto.segnalazioni().stream()
                .sorted(Comparator.comparing(Segnalazione::getIdSegnalazione)).toList();
    }

    public Segnalazione valuta(ContestoOperazione contesto, Long idAttore,
            Long idSegnalazione, Decisione decisione, String motivazione) {
        Amministratore amministratore = Attori.amministratore(contesto, idAttore);
        Vincoli.id(idSegnalazione);
        Vincoli.richiesto(decisione);
        DecisioneAmministrativa esito = new DecisioneAmministrativa(
                amministratore.getIdUtente(), motivazione, contesto.adesso());
        Segnalazione segnalazione = contesto.segnalazioni().stream()
                .filter(s -> s.getIdSegnalazione().equals(idSegnalazione)).findFirst()
                .orElseThrow(() -> new EccezioneApplicativa(CodiceErrore.NON_TROVATO));
        if (segnalazione.getStato() != StatoSegnalazione.DA_ESAMINARE) {
            throw new EccezioneApplicativa(CodiceErrore.CONFLITTO);
        }
        Optional<UtenteRegistrato> profilo = contesto.profilo(segnalazione.getIdSegnalato());
        if (profilo.isPresent() && !(profilo.get() instanceof Cliente)) {
            throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        }
        Optional<Cliente> destinatario = profilo.map(Cliente.class::cast);
        if (decisione == Decisione.CONFERMA) {
            segnalazione.conferma(esito, destinatario);
        } else {
            segnalazione.archivia(esito);
        }
        return segnalazione;
    }
}
