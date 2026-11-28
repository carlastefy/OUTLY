package it.unisa.outly.api.common;

import it.unisa.outly.application.Attori;
import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.ContestoOperazione;
import it.unisa.outly.application.EccezioneApplicativa;
import it.unisa.outly.model.Amministratore;
import it.unisa.outly.model.Cliente;
import it.unisa.outly.model.Organizzatore;
import it.unisa.outly.model.UtenteRegistrato;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SupportoApi {
    public static final String ATTRIBUTO_CONTESTO = "outly.contesto";
    public static final String ATTRIBUTO_ID_UTENTE = "outly.idUtente";
    private SupportoApi() { }
    public static ContestoOperazione contesto(HttpServletRequest richiesta) {
        if (richiesta.getAttribute(ATTRIBUTO_CONTESTO) instanceof ContestoOperazione c) { return c; }
        throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
    }
    public static Long idUtente(HttpSession sessione) {
        if (sessione != null && sessione.getAttribute(ATTRIBUTO_ID_UTENTE) instanceof Long id && id > 0) { return id; }
        throw new EccezioneApplicativa(CodiceErrore.NON_AUTENTICATO);
    }
    public static UtenteRegistrato attore(HttpSession sessione, ContestoOperazione contesto) {
        return Attori.utente(contesto, idUtente(sessione));
    }
    public static Cliente cliente(HttpSession sessione, ContestoOperazione contesto) {
        return Attori.cliente(contesto, idUtente(sessione));
    }
    public static Organizzatore organizzatore(HttpSession sessione, ContestoOperazione contesto) {
        return Attori.organizzatore(contesto, idUtente(sessione));
    }
    public static Amministratore amministratore(HttpSession sessione, ContestoOperazione contesto) {
        return Attori.amministratore(contesto, idUtente(sessione));
    }
}
