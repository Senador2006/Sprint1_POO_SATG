package br.com.fiap.SATG.config;

import br.com.fiap.SATG.domain.LimitesVegetacao;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public final class Configuracao {

    private final LimitesVegetacao limitesVegetacao;
    private final int rodadasSimulacao;
    private final boolean liberarEquipesEntreRodadas;
    private final Path diretorioDados;
    private final String relatorioBaseNome;

    private Configuracao(
            LimitesVegetacao limitesVegetacao,
            int rodadasSimulacao,
            boolean liberarEquipesEntreRodadas,
            Path diretorioDados,
            String relatorioBaseNome) {
        this.limitesVegetacao = limitesVegetacao;
        this.rodadasSimulacao = rodadasSimulacao;
        this.liberarEquipesEntreRodadas = liberarEquipesEntreRodadas;
        this.diretorioDados = diretorioDados;
        this.relatorioBaseNome = relatorioBaseNome;
    }

    public static Configuracao carregar() {
        Properties p = new Properties();
        Path externo = Paths.get(System.getProperty("user.dir"), "satg.properties");
        try {
            if (Files.isRegularFile(externo)) {
                try (InputStream in = Files.newInputStream(externo)) {
                    p.load(in);
                }
            } else {
                try (InputStream in =
                        Configuracao.class.getResourceAsStream("satg.properties")) {
                    if (in == null) {
                        throw new IllegalStateException(
                                "satg.properties não encontrado (nem em "
                                        + externo.toAbsolutePath()
                                        + " nem no classpath com br/com/fiap/SATG/config/).");
                    }
                    p.load(in);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao carregar configuração.", e);
        }

        double baixa = Double.parseDouble(p.getProperty("limite.baixa", "10"));
        double media = Double.parseDouble(p.getProperty("limite.media", "20"));
        double alta = Double.parseDouble(p.getProperty("limite.alta", "30"));
        int rodadas = Integer.parseInt(p.getProperty("simulacao.rodadas", "3"));
        if (rodadas < 1) {
            throw new IllegalArgumentException("simulacao.rodadas deve ser >= 1.");
        }
        boolean liberar = Boolean.parseBoolean(
                p.getProperty("simulacao.liberarEquipesEntreRodadas", "true"));
        String dirDados = Objects.requireNonNullElse(
                p.getProperty("diretorio.dados"), "data");
        String relBase = Objects.requireNonNullElse(
                p.getProperty("relatorio.base"), "relatorio_monitoramento");

        Path pastaDados = Paths.get(System.getProperty("user.dir")).resolve(dirDados);
        return new Configuracao(
                new LimitesVegetacao(baixa, media, alta),
                rodadas,
                liberar,
                pastaDados,
                relBase);
    }

    public LimitesVegetacao getLimitesVegetacao() {
        return limitesVegetacao;
    }

    public int getRodadasSimulacao() {
        return rodadasSimulacao;
    }

    public boolean isLiberarEquipesEntreRodadas() {
        return liberarEquipesEntreRodadas;
    }

    public Path getDiretorioDados() {
        return diretorioDados;
    }

    public Path getArquivoRelatorioTxt() {
        return diretorioDados.resolve(relatorioBaseNome + ".txt");
    }

    public Path getArquivoRelatorioCsv() {
        return diretorioDados.resolve(relatorioBaseNome + ".csv");
    }
}
