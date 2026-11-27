package it.unisa.outly.dto;

public final class LoginResponse {
    private Long idUtente;
    private String ruolo;

    public LoginResponse() {
    }

    public Long getIdUtente() {
        return idUtente;
    }

    public String getRuolo() {
        return ruolo;
    }
}
