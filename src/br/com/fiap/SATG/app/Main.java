package br.com.fiap.SATG.app;

import br.com.fiap.SATG.domain.Atendimento;
import br.com.fiap.SATG.domain.EquipeManutencao;
import br.com.fiap.SATG.domain.TrechoRodovia;
import br.com.fiap.SATG.service.MonitoramentoService;

import java.util.Arrays;
import java.util.List;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("===== INÍCIO DO SISTEMA =====\n");

        // Criação dos trechos
        TrechoRodovia t1 = new TrechoRodovia(0, 5, 12);
        TrechoRodovia t2 = new TrechoRodovia(5, 10, 28);
        TrechoRodovia t3 = new TrechoRodovia(10, 15, 35);

        System.out.println("\n===== SIMULAÇÃO DE CRESCIMENTO =====\n");

        t1.registrarCrescimento(5);
        t2.registrarCrescimento(10);
        t3.registrarCrescimento(2);

        List<TrechoRodovia> trechos = Arrays.asList(t1, t2, t3);

        System.out.println("\n===== CRIAÇÃO DE EQUIPES =====\n");

        EquipeManutencao e1 = new EquipeManutencao("Equipe A");
        EquipeManutencao e2 = new EquipeManutencao("Equipe B");

        List<EquipeManutencao> equipes = Arrays.asList(e1, e2);

        System.out.println("\n===== EXECUTANDO MONITORAMENTO =====\n");

        MonitoramentoService service = new MonitoramentoService();
        List<Atendimento> atendimentos = service.gerarAtendimentos(trechos, equipes);

        System.out.println("\n===== RESULTADO FINAL =====\n");

        for (Atendimento atendimento : atendimentos) {
            System.out.println(atendimento.resumo());
        }

        System.out.println("\n===== FIM DO SISTEMA =====");
    }
}