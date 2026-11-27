package it.unisa.outly.api.common;

import it.unisa.outly.application.CodiceErrore;
import it.unisa.outly.application.EccezioneApplicativa;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(basePackages = "it.unisa.outly.api")
public final class GestoreErroriApi {
    @ExceptionHandler(EccezioneApplicativa.class)
    public ResponseEntity<ErroreApi> applicativa(EccezioneApplicativa errore) {
        return switch (errore.getCodice()) {
            case DATI_NON_VALIDI -> risposta(400, errore.getCodice(), "Dati non validi.", "Controlla i campi inseriti.");
            case NON_AUTENTICATO -> risposta(401, errore.getCodice(), "Accesso richiesto.", "Accedi nuovamente.");
            case VIETATO -> risposta(403, errore.getCodice(), "Operazione non consentita.", "Verifica i permessi del tuo account.");
            case NON_TROVATO -> risposta(404, errore.getCodice(), "Risorsa non disponibile.", "Aggiorna la schermata.");
            case CONFLITTO -> risposta(409, errore.getCodice(), "Operazione incompatibile con lo stato corrente.", "Aggiorna i dati e verifica i vincoli.");
            case INTEGRAZIONE_NON_DISPONIBILE -> risposta(503, errore.getCodice(), "Servizio dati non disponibile.", "Riprova piu tardi.");
        };
    }
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class, MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErroreApi> datiNonValidi(Exception errore) {
        return applicativa(new EccezioneApplicativa(CodiceErrore.DATI_NON_VALIDI));
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroreApi> conflitto(IllegalStateException errore) {
        return applicativa(new EccezioneApplicativa(CodiceErrore.CONFLITTO));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroreApi> imprevisto(Exception errore) {
        return ResponseEntity.status(500).body(new ErroreApi("ERRORE_INTERNO", "Operazione non completata.", "Riprova piu tardi."));
    }
    private ResponseEntity<ErroreApi> risposta(int stato, CodiceErrore codice, String messaggio, String azione) {
        return ResponseEntity.status(stato).body(new ErroreApi(codice.name(), messaggio, azione));
    }
}
