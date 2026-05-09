package br.com.fiap.SATG.service;

import br.com.fiap.SATG.config.Configuracao;
import br.com.fiap.SATG.domain.Atendimento;
import br.com.fiap.SATG.domain.TrechoRodovia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class RelatorioMonitoramento {

    public void escrever(Configuracao cfg, List<RegistroRodada> rodadas) throws IOException {
        Files.createDirectories(cfg.getDiretorioDados());
        escreverTxt(cfg, rodadas);
        escreverCsv(cfg, rodadas);
    }

    private void escreverTxt(Configuracao cfg, List<RegistroRodada> rodadas) throws IOException {
        StringBuilder txt = new StringBuilder();
        txt.append("RELATÓRIO DE MONITORAMENTO\n");
        txt.append("==========================\n\n");
        for (RegistroRodada reg : rodadas) {
            ResultadoMonitoramento r = reg.resultado();
            txt.append("--- Rodada ").append(reg.numero()).append(" ---\n");
            txt.append("Trechos críticos (").append(r.getTrechosCriticos().size()).append("):\n");
            for (TrechoRodovia t : r.getTrechosCriticos()) {
                txt.append("  • ").append(t.resumo()).append('\n');
            }
            txt.append("Equipes alocadas (").append(r.getAtendimentos().size()).append("):\n");
            for (Atendimento a : r.getAtendimentos()) {
                txt.append("  • ").append(a.resumo()).append('\n');
            }
            txt.append("Trechos críticos SEM equipe (")
                    .append(r.getCriticosSemEquipe().size())
                    .append("):\n");
            if (r.getCriticosSemEquipe().isEmpty()) {
                txt.append("  (nenhum)\n");
            } else {
                for (TrechoRodovia t : r.getCriticosSemEquipe()) {
                    txt.append("  • ").append(t.resumo()).append('\n');
                }
            }
            txt.append('\n');
        }
        Files.writeString(cfg.getArquivoRelatorioTxt(), txt.toString(), StandardCharsets.UTF_8);
    }

    private void escreverCsv(Configuracao cfg, List<RegistroRodada> rodadas) throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("rodada,tipo,intervalo_km,vegetacao_cm,prioridade,equipe");
        for (RegistroRodada reg : rodadas) {
            int n = reg.numero();
            ResultadoMonitoramento r = reg.resultado();
            for (Atendimento a : r.getAtendimentos()) {
                TrechoRodovia t = a.getTrecho();
                linhas.add(linhaCsv(
                        n,
                        "CRITICO_ATENDIDO",
                        t.intervalo(),
                        t.getNivelVegetacao(),
                        t.calcularPrioridade().name(),
                        a.getEquipe().getNome()));
            }
            for (TrechoRodovia t : r.getCriticosSemEquipe()) {
                linhas.add(linhaCsv(
                        n,
                        "CRITICO_SEM_EQUIPE",
                        t.intervalo(),
                        t.getNivelVegetacao(),
                        t.calcularPrioridade().name(),
                        ""));
            }
        }
        Files.write(cfg.getArquivoRelatorioCsv(), linhas, StandardCharsets.UTF_8);
    }

    private static String linhaCsv(
            int rodada, String tipo, String intervalo, double veg, String prioridade, String equipe) {
        return String.join(
                ",",
                String.valueOf(rodada),
                campoCsv(tipo),
                campoCsv(intervalo),
                String.format(Locale.ROOT, "%.2f", veg),
                campoCsv(prioridade),
                campoCsv(equipe));
    }

    private static String campoCsv(String valor) {
        if (valor == null) {
            return "\"\"";
        }
        return '"' + valor.replace("\"", "\"\"") + '"';
    }
}
