package it.unisa.outly.control.ga;

import it.unisa.outly.api.ga.RisposteAttivita.RisultatoRicerca;
import it.unisa.outly.api.ga.RisposteAttivita.Sintesi;
import it.unisa.outly.application.Attori;
import it.unisa.outly.application.Cataloghi;
import it.unisa.outly.application.CicloVita;
import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.application.SintesiAttivita;
import it.unisa.outly.model.Attivita;
import it.unisa.outly.model.Vincoli;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public final class RicercaCompatibilitaControl {
    public List<RisultatoRicerca> ricerca(ContestoOperazione c, Long idAttore, CriteriRicerca criteri) {
        Attori.cliente(c, idAttore);
        Vincoli.richiesto(criteri);
        validaCataloghi(c.cataloghi(), criteri);
        return c.attivita().stream().filter(a -> CicloVita.catalogabile(a, c.adesso()))
                .filter(a -> ammessa(a, criteri))
                .map(a -> new RisultatoRicerca(Sintesi.da(new SintesiAttivita(a.getIdAttivita(),
                        a.getTitolo(), a.getData(), a.getOra(), a.getLuogo(), a.getCosto(),
                        a.statoAl(c.adesso()))), compatibilita(a, criteri)))
                .sorted(Comparator.comparing(RisultatoRicerca::compatibilitaPercentuale).reversed())
                .toList();
    }

    public BigDecimal compatibilita(Attivita a, CriteriRicerca r) {
        Vincoli.richiesto(a); Vincoli.richiesto(r);
        int selezionate = 0;
        int soddisfatte = 0;
        if (r.preferenzeSocialita() != null) {
            selezionate++;
            if (a.getPreferenzeSocialita() == r.preferenzeSocialita()) {
                soddisfatte++;
            }
        }
        if (r.difficoltaTecnica() != null) {
            selezionate++;
            if (a.getDifficoltaTecnica().compareTo(r.difficoltaTecnica()) <= 0) {
                soddisfatte++;
            }
        }
        if (r.difficoltaFisica() != null) {
            selezionate++;
            if (a.getDifficoltaFisica().compareTo(r.difficoltaFisica()) <= 0) {
                soddisfatte++;
            }
        }
        return selezionate == 0 ? BigDecimal.valueOf(100)
                : BigDecimal.valueOf(soddisfatte).multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(selezionate), 2, RoundingMode.HALF_UP);
    }
    private boolean ammessa(Attivita a, CriteriRicerca r) {
        return (r.budgetMax() == null || a.getCosto().compareTo(r.budgetMax()) <= 0)
                && (r.citta() == null || a.getLuogo().citta().equals(r.citta()))
                && (r.provincia() == null || a.getLuogo().provincia().equals(r.provincia()))
                && (r.tipologiaAttivita() == null || a.getTipologiaAttivita().equals(r.tipologiaAttivita()))
                && a.getCaratteristicheAccessibilita().containsAll(r.caratteristicheAccessibilita());
    }
    private void validaCataloghi(Cataloghi cataloghi, CriteriRicerca r) {
        if ((r.provincia() != null && !cataloghi.province().contains(r.provincia()))
                || (r.tipologiaAttivita() != null && !cataloghi.tipologieAttivita().contains(r.tipologiaAttivita()))
                || !cataloghi.caratteristicheAccessibilita().containsAll(r.caratteristicheAccessibilita())) {
            throw new EccezioneApplicativa(CodiceErrore.DATI_NON_VALIDI);
        }
    }
}
