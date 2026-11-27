package it.unisa.outly.api.ga;

import it.unisa.outly.api.common.SupportoApi;
import it.unisa.outly.api.ga.RisposteAttivita.Dettaglio;
import it.unisa.outly.api.ga.RisposteAttivita.RisultatoRicerca;
import it.unisa.outly.api.ga.RisposteAttivita.Sintesi;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.control.ga.GestioneAttivitaControl;
import it.unisa.outly.control.ga.RicercaCompatibilitaControl;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attivita")
public final class AttivitaController {
    private final GestioneAttivitaControl gestione = new GestioneAttivitaControl();
    private final RicercaCompatibilitaControl compatibilita = new RicercaCompatibilitaControl();

    @PostMapping
    public ResponseEntity<Dettaglio> inserisci(@RequestBody RichiestaAttivita dati, HttpServletRequest richiesta) {
        Long attore = SupportoApi.idUtente(richiesta.getSession(false));
        ContestoOperazione c = SupportoApi.contesto(richiesta);
        Dettaglio risposta = gestione.inserisci(c, attore, dati.dominio());
        return ResponseEntity.created(URI.create("/api/attivita/" + risposta.idAttivita())).body(risposta);
    }
    @PatchMapping("/{id}")
    public Dettaglio modifica(@PathVariable("id") Long id, @RequestBody RichiestaAttivita dati,
            HttpServletRequest richiesta) {
        Long attore = SupportoApi.idUtente(richiesta.getSession(false));
        return gestione.modifica(SupportoApi.contesto(richiesta), attore, id, dati.dominio());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> elimina(@PathVariable("id") Long id, HttpServletRequest richiesta) {
        Long attore = SupportoApi.idUtente(richiesta.getSession(false));
        gestione.elimina(SupportoApi.contesto(richiesta), attore, id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public List<Sintesi> elenco(HttpServletRequest richiesta) {
        Long attore = SupportoApi.idUtente(richiesta.getSession(false));
        return gestione.elenco(SupportoApi.contesto(richiesta), attore);
    }
    @GetMapping("/{id}")
    public Dettaglio dettaglio(@PathVariable("id") Long id, HttpServletRequest richiesta) {
        Long attore = SupportoApi.idUtente(richiesta.getSession(false));
        return gestione.dettaglio(SupportoApi.contesto(richiesta), attore, id);
    }
    @PostMapping("/ricerca")
    public List<RisultatoRicerca> ricerca(@RequestBody RichiestaRicerca criteri, HttpServletRequest richiesta) {
        Long attore = SupportoApi.idUtente(richiesta.getSession(false));
        return compatibilita.ricerca(SupportoApi.contesto(richiesta), attore, criteri.dominio());
    }
}
