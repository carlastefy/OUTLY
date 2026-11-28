package it.unisa.outly.dto;

public final class ErrorResponse {
    private String codice;
    private String messaggio;
    private String azione;

    public ErrorResponse() {
    }

    public String getCodice() {
        return codice;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public String getAzione() {
        return azione;
    }
}
