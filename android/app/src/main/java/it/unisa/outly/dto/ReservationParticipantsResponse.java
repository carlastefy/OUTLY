package it.unisa.outly.dto;

import java.util.List;

public final class ReservationParticipantsResponse {
    public Long idAttivita;
    public List<Participant> partecipanti;
    public List<Reservation> prenotazioni;

    public static final class Participant {
        public Long idCliente;
        public String nome;
        public String cognome;
    }

    public static final class Reservation {
        public Long idPrenotazione;
        public Long idCliente;
        public String nome;
        public String cognome;
        public String email;
        public String dataOraPrenotazione;
        public String stato;
        public Integer gradoAffidabilita;
    }
}
