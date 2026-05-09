package br.com.fiap.SATG.service;

import br.com.fiap.SATG.domain.Atendimento;
import br.com.fiap.SATG.domain.TrechoRodovia;

import java.util.List;

public final class ResultadoMonitoramento {

    private final List<TrechoRodovia> trechosCriticos;
    private final List<Atendimento> atendimentos;
    private final List<TrechoRodovia> criticosSemEquipe;

    public ResultadoMonitoramento(
            List<TrechoRodovia> trechosCriticos,
            List<Atendimento> atendimentos,
            List<TrechoRodovia> criticosSemEquipe) {
        this.trechosCriticos = List.copyOf(trechosCriticos);
        this.atendimentos = List.copyOf(atendimentos);
        this.criticosSemEquipe = List.copyOf(criticosSemEquipe);
    }

    public List<TrechoRodovia> getTrechosCriticos() {
        return trechosCriticos;
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public List<TrechoRodovia> getCriticosSemEquipe() {
        return criticosSemEquipe;
    }
}
