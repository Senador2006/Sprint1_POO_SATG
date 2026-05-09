package br.com.fiap.SATG.domain;

public class EquipeManutencao {

    private String nome;
    private boolean disponivel = true;

    public EquipeManutencao(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da equipe é obrigatório.");
        }
        this.nome = nome;
        System.out.println("[INIT] Equipe criada: " + nome);
    }

    public boolean estaDisponivel() {
        return disponivel;
    }

    public void iniciarAtendimento() {
        if (!disponivel) {
            throw new IllegalStateException("Equipe já está ocupada.");
        }

        System.out.println("[EQUIPE] " + nome + " iniciando atendimento...");
        this.disponivel = false;
    }

    public void finalizarAtendimento() {
        if (!disponivel) {
            System.out.println("[EQUIPE] " + nome + " finalizou atendimento.");
        }
        this.disponivel = true;
    }

    public String getNome() {
        return nome;
    }
}