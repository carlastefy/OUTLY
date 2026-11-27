package it.unisa.outly.model;

public record DatiEsercizio(String partitaIVA, String nomeEsercizio, String tipologiaEsercizio,
                            String indirizzoEsercizio, String descrizioneEsercizio,
                            String documentoGestioneEsercizio) {
    public DatiEsercizio {
        Vincoli.testo(partitaIVA, 1, Integer.MAX_VALUE);
        Vincoli.testo(nomeEsercizio, 1, Integer.MAX_VALUE);
        Vincoli.testo(tipologiaEsercizio, 1, Integer.MAX_VALUE);
        Vincoli.testo(indirizzoEsercizio, 1, Integer.MAX_VALUE);
        Vincoli.testo(descrizioneEsercizio, 1, Integer.MAX_VALUE);
        Vincoli.testo(documentoGestioneEsercizio, 1, Integer.MAX_VALUE);
    }
    @Override public String toString() { return "DatiEsercizio[riservati]"; }
}
