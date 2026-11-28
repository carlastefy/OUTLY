package it.unisa.outly.application;

import it.unisa.outly.model.Cliente;
import it.unisa.outly.model.GruppoTemporaneo;
import it.unisa.outly.model.Organizzatore;
import it.unisa.outly.model.RichiestaOrganizzatore;
import it.unisa.outly.model.Vincoli;

public final class Collegamenti {
    private Collegamenti() { }
    public static void sostituisciCliente(ContestoOperazione c, Cliente precedente, Organizzatore promosso) {
        if (!precedente.getIdUtente().equals(promosso.getIdUtente())) { throw new IllegalArgumentException("Identita della promozione non corrispondente"); }
        if (!c.utenti().contains(precedente) || c.utenti().stream().anyMatch(u -> u != precedente
                && u.getIdUtente().equals(precedente.getIdUtente()))) { throw new EccezioneApplicativa(CodiceErrore.INTEGRAZIONE_NON_DISPONIBILE); }
        c.utenti().remove(precedente); c.utenti().add(promosso);
    }
    public static void scollegaProfilo(ContestoOperazione c, Long idUtente) {
        Vincoli.id(idUtente);
        c.utenti().removeIf(u -> u.getIdUtente().equals(idUtente));
        c.gruppi().forEach(g -> g.rimuoviPartecipante(idUtente));
        c.richiesteOrganizzatore().stream().filter(r -> r.getIdCliente().equals(idUtente))
                .forEach(RichiestaOrganizzatore::scollegaDatiPersonali);
    }
    public static void scollegaAttivita(ContestoOperazione c, Long idAttivita) {
        Vincoli.id(idAttivita);
        c.attivita().removeIf(a -> a.getIdAttivita().equals(idAttivita));
        c.sintesiAttivita().removeIf(a -> a.idAttivita().equals(idAttivita));
        c.gruppi().stream().filter(g -> g.getIdAttivita().equals(idAttivita)).forEach(GruppoTemporaneo::chiudi);
    }
}
