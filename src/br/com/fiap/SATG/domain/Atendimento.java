package br.com.fiap.SATG.domain;

public class Atendimento {

    private final TrechoRodovia trecho;
    private final EquipeManutencao equipe;

    public Atendimento(TrechoRodovia trecho, EquipeManutencao equipe) {
        this.trecho = trecho;
        this.equipe = equipe;

        System.out.println("[ATENDIMENTO CRIADO]");
        System.out.println("  -> Equipe: " + equipe.getNome());
        System.out.println("  -> Trecho: " + trecho.resumo());
    }

    public TrechoRodovia getTrecho() {
        return trecho;
    }

    public EquipeManutencao getEquipe() {
        return equipe;
    }

    public String resumo() {
        return "Equipe " + equipe.getNome() + " atendendo -> " + trecho.resumo();
    }
}
