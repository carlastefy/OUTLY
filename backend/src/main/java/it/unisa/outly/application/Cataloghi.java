package it.unisa.outly.application;

import it.unisa.outly.model.DatiAttivita;
import it.unisa.outly.model.DatiEsercizio;
import it.unisa.outly.model.PreferenzeCliente;
import java.util.Set;

public record Cataloghi(Set<String> tipologieAttivita, Set<String> province,
        Set<String> interessi, Set<String> caratteristicheAccessibilita, Set<String> tipologieEsercizio) {
    public Cataloghi {
        tipologieAttivita = Set.copyOf(tipologieAttivita); province = Set.copyOf(province);
        interessi = Set.copyOf(interessi); caratteristicheAccessibilita = Set.copyOf(caratteristicheAccessibilita);
        tipologieEsercizio = Set.copyOf(tipologieEsercizio);
    }
    public void valida(DatiAttivita dati) {
        verifica(tipologieAttivita.contains(dati.tipologiaAttivita()) && province.contains(dati.luogo().provincia())
                && caratteristicheAccessibilita.containsAll(dati.caratteristicheAccessibilita()));
    }
    public void valida(PreferenzeCliente dati) {
        verifica(interessi.containsAll(dati.interessi())
                && caratteristicheAccessibilita.containsAll(dati.caratteristicheAccessibilita()));
    }
    public void valida(DatiEsercizio dati) { verifica(tipologieEsercizio.contains(dati.tipologiaEsercizio())); }
    private static void verifica(boolean valore) {
        if (!valore) { throw new EccezioneApplicativa(CodiceErrore.DATI_NON_VALIDI); }
    }
}
