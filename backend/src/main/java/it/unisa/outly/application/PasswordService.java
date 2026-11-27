package it.unisa.outly.application;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordService {
    private static final int ITERAZIONI = 600_000;
    private static final int BIT = 256;
    public void validaNuova(char[] password) {
        if (password == null || password.length < 8) { throw new EccezioneApplicativa(CodiceErrore.DATI_NON_VALIDI); }
        boolean maiuscola = false, numero = false, speciale = false;
        for (char c : password) {
            maiuscola |= Character.isUpperCase(c); numero |= Character.isDigit(c);
            speciale |= !Character.isLetterOrDigit(c) && !Character.isWhitespace(c);
        }
        if (!maiuscola || !numero || !speciale) { throw new EccezioneApplicativa(CodiceErrore.DATI_NON_VALIDI); }
    }
    public String codifica(char[] password) {
        validaNuova(password);
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        byte[] hash = deriva(password, salt, ITERAZIONI);
        try {
            return "pbkdf2-sha256$" + ITERAZIONI + "$" + Base64.getEncoder().encodeToString(salt)
                    + "$" + Base64.getEncoder().encodeToString(hash);
        } finally { Arrays.fill(hash, (byte) 0); }
    }
    public boolean verifica(char[] password, String codificata) {
        if (password == null || codificata == null || codificata.length() > 512) { return false; }
        byte[] calcolata = null;
        byte[] attesa = null;
        try {
            String[] parti = codificata.split("\\$", -1);
            if (parti.length != 4 || !parti[0].equals("pbkdf2-sha256")) { return false; }
            int iterazioni = Integer.parseInt(parti[1]);
            if (iterazioni < 100_000 || iterazioni > 2_000_000) { return false; }
            byte[] salt = Base64.getDecoder().decode(parti[2]);
            attesa = Base64.getDecoder().decode(parti[3]);
            if (salt.length < 16 || salt.length > 64 || attesa.length != BIT / 8) { return false; }
            calcolata = deriva(password, salt, iterazioni);
            return MessageDigest.isEqual(attesa, calcolata);
        } catch (IllegalArgumentException ex) { return false; }
        finally {
            if (calcolata != null) { Arrays.fill(calcolata, (byte) 0); }
            if (attesa != null) { Arrays.fill(attesa, (byte) 0); }
        }
    }
    private byte[] deriva(char[] password, byte[] salt, int iterazioni) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterazioni, BIT);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (GeneralSecurityException ex) {
            throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE);
        } finally { spec.clearPassword(); }
    }
}
