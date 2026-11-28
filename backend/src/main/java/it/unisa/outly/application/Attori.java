package it.unisa.outly.application;

import it.unisa.outly.model.Amministratore;
import it.unisa.outly.model.Cliente;
import it.unisa.outly.model.Organizzatore;
import it.unisa.outly.model.UtenteRegistrato;

public final class Attori {
    private Attori() { }
    public static UtenteRegistrato utente(ContestoOperazione contesto, Long idUtente) {
        if (idUtente == null || idUtente <= 0) { throw new EccezioneApplicativa(CodiceErrore.NON_AUTENTICATO); }
        return contesto.profilo(idUtente).orElseThrow(() -> new EccezioneApplicativa(CodiceErrore.NON_AUTENTICATO));
    }
    public static Cliente cliente(ContestoOperazione contesto, Long idUtente) {
        if (utente(contesto, idUtente) instanceof Cliente c) { return c; }
        throw new EccezioneApplicativa(CodiceErrore.VIETATO);
    }
    public static Organizzatore organizzatore(ContestoOperazione contesto, Long idUtente) {
        if (utente(contesto, idUtente) instanceof Organizzatore o) { return o; }
        throw new EccezioneApplicativa(CodiceErrore.VIETATO);
    }
    public static Amministratore amministratore(ContestoOperazione contesto, Long idUtente) {
        if (utente(contesto, idUtente) instanceof Amministratore a) { return a; }
        throw new EccezioneApplicativa(CodiceErrore.VIETATO);
    }
}
