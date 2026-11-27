package it.unisa.outly.application;

import java.util.Objects;

public final class EccezioneApplicativa extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final CodiceErrore codice;
    public EccezioneApplicativa(CodiceErrore codice) {
        super(Objects.requireNonNull(codice).name()); this.codice = codice;
    }
    public CodiceErrore getCodice() { return codice; }
}
