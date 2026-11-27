package it.unisa.outly.model;

public final class Vincoli {
    private Vincoli() { }
    public static Long id(Long valore) {
        if (valore == null || valore <= 0) { throw new IllegalArgumentException("Identificatore non valido"); }
        return valore;
    }
    public static String testo(String valore, int minimo, int massimo) {
        if (valore == null || valore.isBlank() || valore.length() < minimo || valore.length() > massimo) { throw new IllegalArgumentException("Campo testuale non valido"); }
        return valore;
    }
    public static <T> T richiesto(T valore) {
        if (valore == null) { throw new IllegalArgumentException("Campo obbligatorio"); }
        return valore;
    }
    public static void stato(boolean valido) {
        if (!valido) { throw new IllegalStateException("Operazione incompatibile con lo stato corrente"); }
    }
}
