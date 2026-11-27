package it.unisa.outly.model;

import java.util.Set;

public record PreferenzeCliente(Set<String> interessi, Livello preferenzeSocialita,
                                Set<String> caratteristicheAccessibilita) {
    public PreferenzeCliente {
        Vincoli.richiesto(interessi).forEach(s -> Vincoli.testo(s, 1, Integer.MAX_VALUE));
        Vincoli.richiesto(caratteristicheAccessibilita)
                .forEach(s -> Vincoli.testo(s, 1, Integer.MAX_VALUE));
        interessi = Set.copyOf(interessi);
        caratteristicheAccessibilita = Set.copyOf(caratteristicheAccessibilita);
    }
    public static PreferenzeCliente nonSelezionate() {
        return new PreferenzeCliente(Set.of(), null, Set.of());
    }
}
