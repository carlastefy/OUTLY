package it.unisa.outly.dto;

public final class RegistrationRequest {
    private final String nome;
    private final String cognome;
    private final String email;
    private final String password;
    private final String telefono;

    public RegistrationRequest(String nome, String cognome, String email,
                               String password, String telefono) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
    }
}
