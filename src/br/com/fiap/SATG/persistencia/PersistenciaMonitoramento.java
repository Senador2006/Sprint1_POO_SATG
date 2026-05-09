package br.com.fiap.SATG.persistencia;

import br.com.fiap.SATG.domain.Atendimento;
import br.com.fiap.SATG.domain.TrechoRodovia;
import br.com.fiap.SATG.service.ResultadoMonitoramento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class PersistenciaMonitoramento {

    public void salvarTrechos(int rodada, List<TrechoRodovia> trechos, Path diretorio)
            throws IOException {
        Files.createDirectories(diretorio);
        Path arquivo = diretorio.resolve("trechos_rodada_" + rodada + ".csv");
        StringBuilder sb = new StringBuilder();
        sb.append("km_inicial,km_final,vegetacao_cm,prioridade\n");
        for (TrechoRodovia t : trechos) {
            sb.append(t.getQuilometroInicial())
                    .append(',')
                    .append(t.getQuilometroFinal())
                    .append(',')
                    .append(t.getNivelVegetacao())
                    .append(',')
                    .append(t.calcularPrioridade())
                    .append('\n');
        }
        Files.writeString(arquivo, sb.toString(), StandardCharsets.UTF_8);
    }

    public void salvarAtendimentos(int rodada, ResultadoMonitoramento resultado, Path diretorio)
            throws IOException {
        Files.createDirectories(diretorio);
        Path arquivo = diretorio.resolve("atendimentos_rodada_" + rodada + ".csv");
        StringBuilder sb = new StringBuilder();
        sb.append("equipe,km_inicial,km_final,vegetacao_cm,prioridade_trecho\n");
        for (Atendimento a : resultado.getAtendimentos()) {
            TrechoRodovia t = a.getTrecho();
            sb.append(csv(a.getEquipe().getNome()))
                    .append(',')
                    .append(t.getQuilometroInicial())
                    .append(',')
                    .append(t.getQuilometroFinal())
                    .append(',')
                    .append(t.getNivelVegetacao())
                    .append(',')
                    .append(t.calcularPrioridade())
                    .append('\n');
        }
        Files.writeString(arquivo, sb.toString(), StandardCharsets.UTF_8);

        Path pendentes = diretorio.resolve("criticos_sem_equipe_rodada_" + rodada + ".csv");
        StringBuilder p = new StringBuilder();
        p.append("km_inicial,km_final,vegetacao_cm,prioridade\n");
        for (TrechoRodovia t : resultado.getCriticosSemEquipe()) {
            p.append(t.getQuilometroInicial())
                    .append(',')
                    .append(t.getQuilometroFinal())
                    .append(',')
                    .append(t.getNivelVegetacao())
                    .append(',')
                    .append(t.calcularPrioridade())
                    .append('\n');
        }
        Files.writeString(pendentes, p.toString(), StandardCharsets.UTF_8);
    }

    private static String csv(String valor) {
        if (valor == null) {
            return "";
        }
        String v = valor.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\"")) {
            return '"' + v + '"';
        }
        return v;
    }
}
