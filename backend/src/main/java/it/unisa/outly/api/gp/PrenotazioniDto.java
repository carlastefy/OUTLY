package it.unisa.outly.api.gp;

import it.unisa.outly.control.gp.PrenotazioneControl;
import it.unisa.outly.model.StatoPrenotazione;
import java.time.LocalDateTime;
import java.util.List;

public final class PrenotazioniDto {
    private PrenotazioniDto() { }

    public record PrenotazioneRisposta(Long idPrenotazione, Long idAttivita,
                                      LocalDateTime dataOraPrenotazione, StatoPrenotazione stato) {
        public static PrenotazioneRisposta da(PrenotazioneControl.DettaglioPrenotazione p) {
            return new PrenotazioneRisposta(p.idPrenotazione(), p.idAttivita(),
                    p.dataOraPrenotazione(), p.stato());
        }
    }

    public sealed interface ElencoRisposta permits ElencoCliente, ElencoOrganizzatore { }

    public record PartecipanteCliente(Long idCliente, String nome, String cognome) { }

    public record PrenotatoOrganizzatore(Long idPrenotazione, Long idCliente, String nome,
                                        String cognome, String email, LocalDateTime dataOraPrenotazione,
                                        StatoPrenotazione stato, Integer gradoAffidabilita) { }

    public record ElencoCliente(Long idAttivita, List<PartecipanteCliente> partecipanti)
            implements ElencoRisposta {
        public ElencoCliente { partecipanti = List.copyOf(partecipanti); }
    }

    public record ElencoOrganizzatore(Long idAttivita, List<PrenotatoOrganizzatore> prenotazioni)
            implements ElencoRisposta {
        public ElencoOrganizzatore { prenotazioni = List.copyOf(prenotazioni); }
    }

    public static ElencoRisposta da(PrenotazioneControl.VistaPrenotati vista) {
        if (vista instanceof PrenotazioneControl.VistaCliente cliente) {
            return new ElencoCliente(cliente.idAttivita(), cliente.partecipanti().stream()
                    .map(p -> new PartecipanteCliente(p.idCliente(), p.nome(), p.cognome())).toList());
        }
        if (vista instanceof PrenotazioneControl.VistaOrganizzatore organizzatore) {
            return new ElencoOrganizzatore(organizzatore.idAttivita(), organizzatore.prenotazioni().stream()
                    .map(p -> new PrenotatoOrganizzatore(p.idPrenotazione(), p.idCliente(), p.nome(),
                            p.cognome(), p.email(), p.dataOraPrenotazione(), p.stato(),
                            p.gradoAffidabilita())).toList());
        }
        throw new IllegalArgumentException("Vista prenotati non valida");
    }
}
