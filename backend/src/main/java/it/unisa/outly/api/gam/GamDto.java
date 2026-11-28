package it.unisa.outly.api.gam;

import it.unisa.outly.control.gam.GestioneRichiesteOrganizzatoreControl;
import it.unisa.outly.control.gam.GestioneSegnalazioneControl;
import it.unisa.outly.model.DatiEsercizio;
import it.unisa.outly.model.DecisioneAmministrativa;
import it.unisa.outly.model.RichiestaOrganizzatore;
import it.unisa.outly.model.Segnalazione;
import it.unisa.outly.model.StatoRichiestaOrganizzatore;
import it.unisa.outly.model.StatoSegnalazione;
import it.unisa.outly.model.Vincoli;
import java.time.LocalDateTime;

public final class GamDto {
    private GamDto() { }

    public record ValutazioneRichiesta(GestioneRichiesteOrganizzatoreControl.Decisione decisione,
            String motivazione) {
        public ValutazioneRichiesta {
            Vincoli.richiesto(decisione);
            Vincoli.testo(motivazione, 1, Integer.MAX_VALUE);
        }
    }

    public record ValutazioneSegnalazione(GestioneSegnalazioneControl.Decisione decisione,
            String motivazione) {
        public ValutazioneSegnalazione {
            Vincoli.richiesto(decisione);
            Vincoli.testo(motivazione, 1, Integer.MAX_VALUE);
        }
    }

    public record Esito(Long idAmministratore, String motivazione, LocalDateTime dataOra) {
        static Esito da(DecisioneAmministrativa decisione) {
            return new Esito(decisione.idAmministratore(), decisione.motivazione(), decisione.dataOra());
        }
    }

    public record Esercizio(String partitaIVA, String nomeEsercizio, String tipologiaEsercizio,
            String indirizzoEsercizio, String descrizioneEsercizio, String documentoGestioneEsercizio) {
        static Esercizio da(DatiEsercizio dati) {
            return new Esercizio(dati.partitaIVA(), dati.nomeEsercizio(), dati.tipologiaEsercizio(),
                    dati.indirizzoEsercizio(), dati.descrizioneEsercizio(), dati.documentoGestioneEsercizio());
        }
    }

    public record Richiesta(Long idRichiestaOrganizzatore, Long idCliente,
            LocalDateTime dataOraRichiesta, StatoRichiestaOrganizzatore stato,
            Esercizio datiEsercizio, Esito decisione) {
        static Richiesta da(RichiestaOrganizzatore richiesta) {
            return new Richiesta(richiesta.getIdRichiestaOrganizzatore(), richiesta.getIdCliente(),
                    richiesta.getDataOraRichiesta(), richiesta.getStato(),
                    richiesta.getDatiEsercizio().map(Esercizio::da).orElse(null),
                    richiesta.getDecisione().map(Esito::da).orElse(null));
        }
    }

    public record SegnalazioneAmministrativa(Long idSegnalazione, Long idSegnalante,
            Long idSegnalato, Long idAttivita, String descrizione,
            LocalDateTime dataOraSegnalazione, StatoSegnalazione stato, Esito decisione) {
        static SegnalazioneAmministrativa da(Segnalazione segnalazione) {
            return new SegnalazioneAmministrativa(segnalazione.getIdSegnalazione(),
                    segnalazione.getIdSegnalante(), segnalazione.getIdSegnalato(),
                    segnalazione.getIdAttivita(), segnalazione.getDescrizione(),
                    segnalazione.getDataOraSegnalazione(), segnalazione.getStato(),
                    segnalazione.getDecisione().map(Esito::da).orElse(null));
        }
    }
}
