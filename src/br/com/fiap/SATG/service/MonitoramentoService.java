package br.com.fiap.SATG.service;

import br.com.fiap.SATG.domain.Atendimento;
import br.com.fiap.SATG.domain.EquipeManutencao;
import br.com.fiap.SATG.domain.TrechoRodovia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MonitoramentoService {

    public ResultadoMonitoramento gerarAtendimentos(
            List<TrechoRodovia> trechos, List<EquipeManutencao> equipes) {

        System.out.println("\n[PROCESSO] Iniciando análise de trechos...\n");

        List<TrechoRodovia> criticos =
                trechos.stream().filter(TrechoRodovia::ehCritico).collect(Collectors.toList());

        System.out.println("[INFO] Trechos críticos encontrados: " + criticos.size());

        for (TrechoRodovia t : criticos) {
            System.out.println("  -> " + t.resumo());
        }

        List<TrechoRodovia> ordenados = criticos.stream()
                .sorted(Comparator.comparing(TrechoRodovia::calcularPrioridade).reversed())
                .collect(Collectors.toList());

        System.out.println("\n[PROCESSO] Iniciando alocação de equipes...\n");

        List<Atendimento> atendimentos = new ArrayList<>();
        List<TrechoRodovia> criticosSemEquipe = new ArrayList<>();

        for (TrechoRodovia trecho : ordenados) {

            Optional<EquipeManutencao> equipeDisponivel =
                    equipes.stream().filter(EquipeManutencao::estaDisponivel).findFirst();

            if (equipeDisponivel.isPresent()) {

                EquipeManutencao equipe = equipeDisponivel.get();

                System.out.println("[ALOCANDO]");
                System.out.println("  -> Equipe: " + equipe.getNome());
                System.out.println("  -> Trecho: " + trecho.intervalo());

                equipe.iniciarAtendimento();

                atendimentos.add(new Atendimento(trecho, equipe));

            } else {
                System.out.println("[AVISO] Nenhuma equipe disponível para:");
                System.out.println("  -> " + trecho.resumo());
                criticosSemEquipe.add(trecho);
            }
        }

        return new ResultadoMonitoramento(criticos, atendimentos, criticosSemEquipe);
    }
}
