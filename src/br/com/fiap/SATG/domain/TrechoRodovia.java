package br.com.fiap.SATG.domain;

public class TrechoRodovia {

    // Constantes que definem os limites de prioridade (evita números mágicos)
    private static final double LIMITE_BAIXA = 10;
    private static final double LIMITE_MEDIA = 20;
    private static final double LIMITE_ALTA = 30;

    private double quilometroInicial;
    private double quilometroFinal;
    private double nivelVegetacao;

    // Construtor: garante que o objeto já nasce válido
    public TrechoRodovia(double quilometroInicial, double quilometroFinal, double nivelVegetacao) {
        validarQuilometragem(quilometroInicial, quilometroFinal);
        setNivelVegetacao(nivelVegetacao);

        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;

        System.out.println("[INIT] Trecho criado: " + resumo());
    }

    // Simula crescimento da vegetação
    public void registrarCrescimento(double crescimentoEmCm) {
        System.out.println("[AÇÃO] Registrando crescimento no trecho " + intervalo());

        if (crescimentoEmCm < 0) {
            throw new IllegalArgumentException("Crescimento não pode ser negativo.");
        }

        System.out.println("  - Valor atual: " + nivelVegetacao + " cm");
        System.out.println("  - Crescimento aplicado: +" + crescimentoEmCm + " cm");

        this.nivelVegetacao += crescimentoEmCm;

        System.out.println("  - Novo valor: " + nivelVegetacao + " cm");
    }

    // Define prioridade com base na regra de negócio
    public Prioridade calcularPrioridade() {
        if (nivelVegetacao > LIMITE_ALTA) return Prioridade.CRITICA;
        if (nivelVegetacao > LIMITE_MEDIA) return Prioridade.ALTA;
        if (nivelVegetacao > LIMITE_BAIXA) return Prioridade.MEDIA;
        return Prioridade.BAIXA;
    }

    // Atalho semântico
    public boolean ehCritico() {
        return calcularPrioridade() == Prioridade.CRITICA;
    }

    public String intervalo() {
        return "KM " + quilometroInicial + " ao " + quilometroFinal;
    }

    // Método de resumo (usado para debug/console)
    public String resumo() {
        return intervalo() +
                " | Vegetação: " + nivelVegetacao +
                " cm | Prioridade: " + calcularPrioridade();
    }

    private void setNivelVegetacao(double nivelVegetacao) {
        if (nivelVegetacao < 0) {
            throw new IllegalArgumentException("Vegetação não pode ser negativa.");
        }
        this.nivelVegetacao = nivelVegetacao;
    }

    private void validarQuilometragem(double inicio, double fim) {
        if (inicio >= fim) {
            throw new IllegalArgumentException("KM inicial deve ser menor que KM final.");
        }
    }
}