package it.unisa.outly.control.ga;

import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.application.SintesiAttivita;
import it.unisa.outly.model.Attivita;
import it.unisa.outly.model.Vincoli;
import java.util.Optional;
import java.util.function.Supplier;

public final class AttivitaProxy {
    private final SintesiAttivita sintesi;
    private final Supplier<Optional<Attivita>> caricamento;
    private Attivita dettaglio;

    public AttivitaProxy(SintesiAttivita sintesi, Supplier<Optional<Attivita>> caricamento) {
        this.sintesi = Vincoli.richiesto(sintesi);
        this.caricamento = Vincoli.richiesto(caricamento);
    }
    public SintesiAttivita sintesi() { return sintesi; }

    public Attivita dettaglio() {
        if (dettaglio == null) {
            Optional<Attivita> caricato = caricamento.get();
            if (caricato == null) {
                throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
            Attivita valore = caricato.orElseThrow(() -> new EccezioneApplicativa(CodiceErrore.NON_TROVATO));
            if (!valore.getIdAttivita().equals(sintesi.idAttivita())) {
                throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
            }
            dettaglio = valore;
        }
        return dettaglio;
    }
}
