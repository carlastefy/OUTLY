package it.unisa.outly.api.gam;

import it.unisa.outly.api.common.SupportoApi;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.control.gam.GestioneSegnalazioneControl;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amministrazione/segnalazioni")
public final class SegnalazioniController {
    private final GestioneSegnalazioneControl control = new GestioneSegnalazioneControl();

    @GetMapping
    public List<GamDto.SegnalazioneAmministrativa> consulta(HttpServletRequest richiesta) {
        ContestoOperazione contesto = SupportoApi.contesto(richiesta);
        Long idAttore = SupportoApi.amministratore(richiesta.getSession(false), contesto).getIdUtente();
        return control.consulta(contesto, idAttore).stream().map(GamDto.SegnalazioneAmministrativa::da).toList();
    }

    @PostMapping("/{id}/valutazione")
    public GamDto.SegnalazioneAmministrativa valuta(@PathVariable("id") Long id,
            @RequestBody GamDto.ValutazioneSegnalazione dati, HttpServletRequest richiesta) {
        ContestoOperazione contesto = SupportoApi.contesto(richiesta);
        Long idAttore = SupportoApi.amministratore(richiesta.getSession(false), contesto).getIdUtente();
        return GamDto.SegnalazioneAmministrativa.da(
                control.valuta(contesto, idAttore, id, dati.decisione(), dati.motivazione()));
    }
}
