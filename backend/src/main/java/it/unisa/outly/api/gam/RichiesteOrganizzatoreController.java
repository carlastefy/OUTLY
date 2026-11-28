package it.unisa.outly.api.gam;

import it.unisa.outly.api.common.SupportoApi;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.control.gam.GestioneRichiesteOrganizzatoreControl;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amministrazione/richieste-organizzatore")
public final class RichiesteOrganizzatoreController {
    private final GestioneRichiesteOrganizzatoreControl control = new GestioneRichiesteOrganizzatoreControl();

    @GetMapping
    public List<GamDto.Richiesta> consulta(HttpServletRequest richiesta) {
        ContestoOperazione contesto = SupportoApi.contesto(richiesta);
        Long idAttore = SupportoApi.amministratore(richiesta.getSession(false), contesto).getIdUtente();
        return control.consulta(contesto, idAttore).stream().map(GamDto.Richiesta::da).toList();
    }

    @PostMapping("/{id}/valutazione")
    public GamDto.Richiesta valuta(@PathVariable("id") Long id,
            @RequestBody GamDto.ValutazioneRichiesta dati, HttpServletRequest richiesta) {
        ContestoOperazione contesto = SupportoApi.contesto(richiesta);
        Long idAttore = SupportoApi.amministratore(richiesta.getSession(false), contesto).getIdUtente();
        return GamDto.Richiesta.da(control.valuta(contesto, idAttore, id, dati.decisione(), dati.motivazione()));
    }
}
