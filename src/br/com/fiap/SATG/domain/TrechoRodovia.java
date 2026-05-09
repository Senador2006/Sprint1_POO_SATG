package br.com.fiap.SATG.domain;

import java.util.Objects;

public class TrechoRodovia {

    private final LimitesVegetacao limitesVegetacao;
    private final double quilometroInicial;
    private final double quilometroFinal;
    private double nivelVegetacao;

    public TrechoRodovia(
            LimitesVegetacao limitesVegetacao,
            double quilometroInicial,
            double quilometroFinal,
            double nivelVegetacao) {
        this.limitesVegetacao = Objects.requireNonNull(limitesVegetacao, "limitesVegetacao");
        validarQuilometragem(quilometroInicial, quilometroFinal);
        setNivelVegetacao(nivelVegetacao);
        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;
        System.out.println("[INIT] Trecho criado: " + resumo());
    }

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

    public Prioridade calcularPrioridade() {
        if (nivelVegetacao > limitesVegetacao.getLimiteAlta()) {
            return Prioridade.CRITICA;
        }
        if (nivelVegetacao > limitesVegetacao.getLimiteMedia()) {
            return Prioridade.ALTA;
        }
        if (nivelVegetacao > limitesVegetacao.getLimiteBaixa()) {
            return Prioridade.MEDIA;
        }
        return Prioridade.BAIXA;
    }

    public boolean ehCritico() {
        return calcularPrioridade() == Prioridade.CRITICA;
    }

    public double getQuilometroInicial() {
        return quilometroInicial;
    }

    public double getQuilometroFinal() {
        return quilometroFinal;
    }

    public double getNivelVegetacao() {
        return nivelVegetacao;
    }

    public String intervalo() {
        return "KM " + quilometroInicial + " ao " + quilometroFinal;
    }

    public String resumo() {
        return intervalo()
                + " | Vegetação: "
                + nivelVegetacao
                + " cm | Prioridade: "
                + calcularPrioridade();
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
