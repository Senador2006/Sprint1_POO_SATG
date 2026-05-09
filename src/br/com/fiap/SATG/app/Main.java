package br.com.fiap.SATG.app;

import br.com.fiap.SATG.config.Configuracao;
import br.com.fiap.SATG.domain.Atendimento;
import br.com.fiap.SATG.domain.EquipeManutencao;
import br.com.fiap.SATG.domain.LimitesVegetacao;
import br.com.fiap.SATG.domain.TrechoRodovia;
import br.com.fiap.SATG.persistencia.PersistenciaMonitoramento;
import br.com.fiap.SATG.service.MonitoramentoService;
import br.com.fiap.SATG.service.RegistroRodada;
import br.com.fiap.SATG.service.RelatorioMonitoramento;
import br.com.fiap.SATG.service.ResultadoMonitoramento;

import java.util.ArrayList;
import java.util.List;

public class Main {

    /**
     * Crescimento (cm) por rodada e por trecho — exemplo didático; altere para simular outros
     * cenários. Se houver mais rodadas em {@code satg.properties} que linhas aqui, o excesso usa
     * 0 cm.
     */
    private static final double[][] CRESCIMENTO_CM_POR_RODADA = {
        {5, 10, 2},
        {3, 2, 8},
        {1, 1, 1}
    };

    public static void main(String[] args) {
        try {
            executar();
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void executar() throws Exception {
        Configuracao cfg = Configuracao.carregar();
        LimitesVegetacao limites = cfg.getLimitesVegetacao();

        System.out.println("===== INÍCIO DO SISTEMA =====\n");
        System.out.println(
                "[CONFIG] Limites (cm): baixa="
                        + limites.getLimiteBaixa()
                        + ", média="
                        + limites.getLimiteMedia()
                        + ", alta="
                        + limites.getLimiteAlta());
        System.out.println(
                "[CONFIG] Rodadas: "
                        + cfg.getRodadasSimulacao()
                        + " | Liberar equipes entre rodadas: "
                        + cfg.isLiberarEquipesEntreRodadas());
        System.out.println("[CONFIG] Dados e relatórios em: " + cfg.getDiretorioDados().toAbsolutePath());

        TrechoRodovia t1 = new TrechoRodovia(limites, 0, 5, 12);
        TrechoRodovia t2 = new TrechoRodovia(limites, 5, 10, 28);
        TrechoRodovia t3 = new TrechoRodovia(limites, 10, 15, 35);

        List<TrechoRodovia> trechos = new ArrayList<>(List.of(t1, t2, t3));

        EquipeManutencao e1 = new EquipeManutencao("Equipe A");
        EquipeManutencao e2 = new EquipeManutencao("Equipe B");
        List<EquipeManutencao> equipes = new ArrayList<>(List.of(e1, e2));

        MonitoramentoService service = new MonitoramentoService();
        PersistenciaMonitoramento persistencia = new PersistenciaMonitoramento();
        List<RegistroRodada> historico = new ArrayList<>();

        for (int r = 1; r <= cfg.getRodadasSimulacao(); r++) {
            System.out.println("\n========== RODADA / DIA " + r + " ==========\n");

            if (r > 1 && cfg.isLiberarEquipesEntreRodadas()) {
                System.out.println("[SIMULAÇÃO] Liberando equipes após ciclo anterior...\n");
                for (EquipeManutencao e : equipes) {
                    e.finalizarAtendimento();
                }
            }

            System.out.println("===== CRESCIMENTO DA VEGETAÇÃO =====\n");
            for (int i = 0; i < trechos.size(); i++) {
                double inc = valorCrescimento(r - 1, i);
                if (inc > 0) {
                    trechos.get(i).registrarCrescimento(inc);
                }
            }

            System.out.println("\n===== EXECUTANDO MONITORAMENTO =====\n");
            ResultadoMonitoramento resultado = service.gerarAtendimentos(trechos, equipes);
            historico.add(new RegistroRodada(r, resultado));

            persistencia.salvarTrechos(r, trechos, cfg.getDiretorioDados());
            persistencia.salvarAtendimentos(r, resultado, cfg.getDiretorioDados());

            System.out.println("\n===== RESUMO DA RODADA " + r + " =====\n");
            for (Atendimento a : resultado.getAtendimentos()) {
                System.out.println(a.resumo());
            }
            if (!resultado.getCriticosSemEquipe().isEmpty()) {
                System.out.println("\n[PENDENTES] Críticos sem equipe nesta rodada:");
                for (TrechoRodovia t : resultado.getCriticosSemEquipe()) {
                    System.out.println("  -> " + t.resumo());
                }
            }
        }

        new RelatorioMonitoramento().escrever(cfg, historico);

        System.out.println("\n===== RELATÓRIOS GERADOS =====");
        System.out.println(cfg.getArquivoRelatorioTxt().toAbsolutePath());
        System.out.println(cfg.getArquivoRelatorioCsv().toAbsolutePath());
        System.out.println("\n===== FIM DO SISTEMA =====");
    }

    private static double valorCrescimento(int indiceRodada, int indiceTrecho) {
        if (indiceRodada < 0
                || indiceRodada >= CRESCIMENTO_CM_POR_RODADA.length
                || indiceTrecho >= CRESCIMENTO_CM_POR_RODADA[indiceRodada].length) {
            return 0;
        }
        return CRESCIMENTO_CM_POR_RODADA[indiceRodada][indiceTrecho];
    }
}
