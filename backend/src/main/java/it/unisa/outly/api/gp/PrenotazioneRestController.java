package it.unisa.outly.api.gp;

import it.unisa.outly.api.common.SupportoApi;
import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.control.gp.PrenotazioneControl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public final class PrenotazioneRestController {
    private final PrenotazioneControl control = new PrenotazioneControl();

    @PostMapping("/attivita/{id}/prenotazioni")
    public ResponseEntity<PrenotazioniDto.PrenotazioneRisposta> prenota(
            @PathVariable("id") Long idAttivita, @RequestBody(required = false) String corpo,
            HttpServletRequest richiesta) {
        ContestoOperazione contesto = SupportoApi.contesto(richiesta);
        Long idAttore = SupportoApi.idUtente(richiesta.getSession(false));
        senzaCorpo(corpo);
        PrenotazioneControl.EsitoPrenotazione esito = control.prenota(contesto, idAttore, idAttivita);
        return ResponseEntity.status(esito.nuova() ? 201 : 200)
                .body(PrenotazioniDto.PrenotazioneRisposta.da(esito.prenotazione()));
    }

    @DeleteMapping("/prenotazioni/{id}")
    public PrenotazioniDto.PrenotazioneRisposta annulla(
            @PathVariable("id") Long idPrenotazione, @RequestBody(required = false) String corpo,
            HttpServletRequest richiesta) {
        ContestoOperazione contesto = SupportoApi.contesto(richiesta);
        Long idAttore = SupportoApi.idUtente(richiesta.getSession(false));
        senzaCorpo(corpo);
        return PrenotazioniDto.PrenotazioneRisposta.da(control.annulla(contesto, idAttore, idPrenotazione));
    }

    @GetMapping("/attivita/{id}/prenotazioni")
    public PrenotazioniDto.ElencoRisposta visualizzaPrenotati(
            @PathVariable("id") Long idAttivita, HttpServletRequest richiesta) {
        ContestoOperazione contesto = SupportoApi.contesto(richiesta);
        Long idAttore = SupportoApi.idUtente(richiesta.getSession(false));
        return PrenotazioniDto.da(control.visualizzaPrenotati(contesto, idAttore, idAttivita));
    }

    private void senzaCorpo(String corpo) {
        if (corpo != null && !corpo.isBlank()) {
            throw new EccezioneApplicativa(CodiceErrore.DATI_NON_VALIDI);
        }
    }
}
