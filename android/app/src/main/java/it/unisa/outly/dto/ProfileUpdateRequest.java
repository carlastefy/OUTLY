package it.unisa.outly.dto;

public final class ProfileUpdateRequest {
    public final String nome;
    public final String cognome;
    public final String email;
    public final String telefono;

    public ProfileUpdateRequest(String nome, String cognome, String email, String telefono) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.telefono = telefono;
    }
}
