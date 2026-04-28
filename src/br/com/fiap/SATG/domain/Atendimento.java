package br.com.fiap.SATG.domain;

public class Atendimento {

    private TrechoRodovia trecho;
    private EquipeManutencao equipe;

    public Atendimento(TrechoRodovia trecho, EquipeManutencao equipe) {
        this.trecho = trecho;
        this.equipe = equipe;

        System.out.println("[ATENDIMENTO CRIADO]");
        System.out.println("  -> Equipe: " + equipe.getNome());
        System.out.println("  -> Trecho: " + trecho.resumo());
    }

    public String resumo() {
        return "Equipe " + equipe.getNome() +
                " atendendo -> " + trecho.resumo();
    }
}
