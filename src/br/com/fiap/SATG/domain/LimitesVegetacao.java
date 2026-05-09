package br.com.fiap.SATG.domain;

/**
 * Faixas para cálculo de {@link Prioridade} a partir do nível de vegetação (cm).
 */
public final class LimitesVegetacao {

    private final double limiteBaixa;
    private final double limiteMedia;
    private final double limiteAlta;

    public LimitesVegetacao(double limiteBaixa, double limiteMedia, double limiteAlta) {
        if (limiteBaixa < 0 || limiteMedia < 0 || limiteAlta < 0) {
            throw new IllegalArgumentException("Limites não podem ser negativos.");
        }
        if (!(limiteBaixa < limiteMedia && limiteMedia < limiteAlta)) {
            throw new IllegalArgumentException(
                    "É necessário limiteBaixa < limiteMedia < limiteAlta.");
        }
        this.limiteBaixa = limiteBaixa;
        this.limiteMedia = limiteMedia;
        this.limiteAlta = limiteAlta;
    }

    public double getLimiteBaixa() {
        return limiteBaixa;
    }

    public double getLimiteMedia() {
        return limiteMedia;
    }

    public double getLimiteAlta() {
        return limiteAlta;
    }
}
