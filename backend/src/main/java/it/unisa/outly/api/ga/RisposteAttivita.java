package it.unisa.outly.api.ga;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.unisa.outly.application.CicloVita;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.SintesiAttivita;
import it.unisa.outly.model.Attivita;
import it.unisa.outly.model.Livello;
import it.unisa.outly.model.Luogo;
import it.unisa.outly.model.StatoAttivita;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public final class RisposteAttivita {
    private RisposteAttivita() { }

    public record LuogoRisposta(String indirizzo, String citta, String provincia) {
        static LuogoRisposta da(Luogo luogo) {
            return new LuogoRisposta(luogo.indirizzo(), luogo.citta(), luogo.provincia());
        }
    }
    public record Sintesi(Long idAttivita, String titolo,
            @JsonFormat(pattern = "dd/MM/yyyy") LocalDate data,
            @JsonFormat(pattern = "HH:mm") LocalTime ora,
            LuogoRisposta luogo, BigDecimal costo, StatoAttivita stato) {
        public static Sintesi da(SintesiAttivita a) {
            return new Sintesi(a.idAttivita(), a.titolo(), a.data(), a.ora(),
                    LuogoRisposta.da(a.luogo()), a.costo(), a.stato());
        }
    }
    public record Dettaglio(Long idAttivita, Long idOrganizzatore, String titolo, String foto,
            String descrizione, @JsonFormat(pattern = "dd/MM/yyyy") LocalDate data,
            @JsonFormat(pattern = "HH:mm") LocalTime ora, LuogoRisposta luogo,
            BigDecimal costo, int durataMinuti, Livello preferenzeSocialita,
            Set<String> caratteristicheAccessibilita, String tipologiaAttivita,
            Livello difficoltaTecnica, Livello difficoltaFisica, int numeroMassimoPartecipanti,
            long numeroPrenotazioni, StatoAttivita stato, boolean prenotabile) {
        public Dettaglio { caratteristicheAccessibilita = Set.copyOf(caratteristicheAccessibilita); }
        public static Dettaglio da(Attivita a, ContestoOperazione c, Long idCliente) {
            boolean giaPrenotato = c.prenotazioni().stream().anyMatch(p -> p.getIdCliente().equals(idCliente)
                    && p.attivaPer(a, c.adesso()));
            return new Dettaglio(a.getIdAttivita(), a.getIdOrganizzatore(), a.getTitolo(), a.getFoto(),
                    a.getDescrizione(), a.getData(), a.getOra(), LuogoRisposta.da(a.getLuogo()),
                    a.getCosto(), a.getDurataMinuti(), a.getPreferenzeSocialita(),
                    a.getCaratteristicheAccessibilita(), a.getTipologiaAttivita(), a.getDifficoltaTecnica(),
                    a.getDifficoltaFisica(), a.getNumeroMassimoPartecipanti(),
                    a.getNumeroPrenotazioni(c.prenotazioni()), a.statoAl(c.adesso()),
                    !giaPrenotato && CicloVita.prenotabile(a, c.prenotazioni(), c.adesso()));
        }
    }
    public record RisultatoRicerca(Sintesi attivita, BigDecimal compatibilitaPercentuale) { }
}
