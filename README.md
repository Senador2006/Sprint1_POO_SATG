# SATG — Sistema de monitoramento de vegetação em rodovias

Projeto acadêmico em Java que simula **trechos de rodovia**, **nível de vegetação**, **prioridades de atendimento**, **equipes de manutenção** e **alocação** de equipes aos trechos críticos, com **várias rodadas (dias)**, **persistência em CSV**, **relatórios** (TXT e CSV) e **parâmetros externos** em arquivo `.properties`.

---

## Requisitos

- **Java 11+** (uso de `String.isBlank()` em `EquipeManutencao`).
- IDE (ex.: IntelliJ) com o diretório `src` marcado como pasta de código-fonte, ou compilação manual com `javac` apontando para `src`.

---

## Como executar

1. Abra o módulo `Sprint1_POO_SATG` na IDE ou use o diretório `src` como raiz de compilação.
2. Execute a classe **`br.com.fiap.SATG.app.Main`**.
3. O **diretório de trabalho** (working directory) da execução costuma ser a pasta do módulo no IntelliJ. Nela pode existir um `satg.properties` opcional que **sobrescreve** o arquivo padrão do classpath (veja [Configuração](#configuração)).

Saídas de **dados** e **relatórios** são gravadas na pasta definida por `diretorio.dados` (padrão: `data/` relativa ao diretório de trabalho).

---

## Estrutura de pacotes e arquivos

### Raiz do módulo

| Arquivo | Função |
|--------|--------|
| `satg.properties` | Cópia **opcional** da configuração no diretório de trabalho. Se existir aqui (ou em `{user.dir}/satg.properties`), tem prioridade sobre o arquivo embutido em `src/.../config/`. |
| `Sprint1_POO_SATG.iml` | Metadados do módulo IntelliJ. |
| `.idea/` | Configurações do projeto na IDE (opcional para quem só usa linha de comando). |

### `src/br/com/fiap/SATG/app/`

| Arquivo | Função |
|--------|--------|
| **`Main.java`** | Ponto de entrada. Carrega `Configuracao`, cria trechos e equipes, executa o **loop de rodadas** (liberação opcional de equipes → crescimento da vegetação → monitoramento → persistência CSV) e, ao final, gera os relatórios TXT/CSV. A matriz **`CRESCIMENTO_CM_POR_RODADA`** define quantos centímetros cada trecho “cresce” por rodada (cenário de demonstração). |

### `src/br/com/fiap/SATG/config/`

| Arquivo | Função |
|--------|--------|
| **`Configuracao.java`** | Lê o `satg.properties` (arquivo externo ao classpath ou recurso `satg.properties` ao lado desta classe), valida valores e expõe limites de vegetação, número de rodadas, flag de liberação de equipes e caminhos da pasta de dados e dos relatórios. |
| **`satg.properties`** | Valores padrão empacotados no classpath: limites cm, simulação, pastas e nome base dos relatórios. |

### `src/br/com/fiap/SATG/domain/`

| Arquivo | Função |
|--------|--------|
| **`TrechoRodovia.java`** | Representa um trecho (km inicial, km final, vegetação em cm). Usa `LimitesVegetacao` para calcular `Prioridade`, expõe `ehCritico()`, `registrarCrescimento()`, `resumo()`, getters para persistência. Valida quilometragem e valores não negativos. |
| **`LimitesVegetacao.java`** | Três limites (baixa, média, alta) em centímetros com regra **baixa &lt; média &lt; alta**. |
| **`Prioridade.java`** | Enum: `BAIXA`, `MEDIA`, `ALTA`, `CRITICA`. |
| **`EquipeManutencao.java`** | Equipe com nome, disponível ou ocupada. `iniciarAtendimento()` / `finalizarAtendimento()` alteram a disponibilidade; nome obrigatório (não nulo nem em branco). |
| **`Atendimento.java`** | Associa um `TrechoRodovia` a uma `EquipeManutencao`; getters e `resumo()` para exibição e relatórios. |

### `src/br/com/fiap/SATG/service/`

| Arquivo | Função |
|--------|--------|
| **`MonitoramentoService.java`** | Filtra trechos **críticos**, ordena por prioridade, aloca a **primeira equipe disponível** a cada trecho crítico e monta o **`ResultadoMonitoramento`** (inclui lista de críticos **sem** equipe). |
| **`ResultadoMonitoramento.java`** | Agregado imutável: todos os críticos da rodada, atendimentos formados e críticos que ficaram **sem** equipe. |
| **`RegistroRodada.java`** | Registro (`record`) com número da rodada e `ResultadoMonitoramento` — usado para histórico e relatório final. |
| **`RelatorioMonitoramento.java`** | Escreve **`{relatorio.base}.txt`** (legível) e **`{relatorio.base}.csv`** (uma linha por atendimento ou pendência), consolidando todas as rodadas. |

### `src/br/com/fiap/SATG/persistencia/`

| Arquivo | Função |
|--------|--------|
| **`PersistenciaMonitoramento.java`** | Por rodada, grava CSVs UTF-8: estado de **todos os trechos**, **atendimentos** alocados e trechos críticos **sem equipe**. |

### Pacotes por responsabilidade

| Pacote | Papel |
|--------|--------|
| `app` | Orquestração da simulação e console. |
| `config` | Leitura de parâmetros externos. |
| `domain` | Entidades e regras básicas dos objetos de negócio. |
| `service` | Casos de uso (monitoramento, relatório). |
| `persistencia` | Gravação de estado em disco (CSV). |

---

## Configuração (`satg.properties`)

O carregamento segue esta ordem:

1. Se existir **`{user.dir}/satg.properties`** (diretório de trabalho do processo), esse arquivo é usado.
2. Caso contrário, usa o recurso **`satg.properties`** junto de `Configuracao.class` no classpath (`src/.../config/`).

| Chave | Significado | Exemplo |
|--------|-------------|---------|
| `limite.baixa` | Até esse valor (exclusive acima), prioridade não passa de baixa/média conforme regra | `10` |
| `limite.media` | Limiar entre média e alta | `20` |
| `limite.alta` | Acima disto até o próximo nível vira crítica pela regra em `TrechoRodovia` | `30` |
| `simulacao.rodadas` | Quantas rodadas (dias) executar | `3` |
| `simulacao.liberarEquipesEntreRodadas` | Se `true`, no início da rodada 2+ as equipes ocupadas são liberadas antes do novo crescimento | `true` |
| `diretorio.dados` | Subpasta (relativa ao `user.dir`) onde CSVs e relatórios são gravados | `data` |
| `relatorio.base` | Nome base dos arquivos `relatorio_monitoramento.txt` e `.csv` | `relatorio_monitoramento` |

**Regra de prioridade (vegetação em cm):** acima de `limite.alta` → `CRITICA`; acima de `limite.media` → `ALTA`; acima de `limite.baixa` → `MEDIA`; caso contrário → `BAIXA`. Trecho **crítico** = prioridade `CRITICA`.

---

## Como o sistema funciona (fluxo)

```text
                    ┌─────────────────────┐
                    │  Main: carrega cfg  │
                    └──────────┬──────────┘
                               │
         ┌─────────────────────▼─────────────────────┐
         │  Para cada rodada r = 1 .. N               │
         │  ┌──────────────────────────────────────┐ │
         │  │ r > 1 e liberar equipes?             │ │
         │  │  → finalizarAtendimento() em equipes  │ │
         │  └──────────────────────────────────────┘ │
         │  ┌──────────────────────────────────────┐ │
         │  │ Crescimento: registrarCrescimento()   │ │
         │  │ (valores da matriz em Main)           │ │
         │  └──────────────────────────────────────┘ │
         │  ┌──────────────────────────────────────┐ │
         │  │ MonitoramentoService.gerarAtendimentos│ │
         │  │  → críticos → ordem → aloca equipes  │ │
         │  └──────────────────────────────────────┘ │
         │  ┌──────────────────────────────────────┐ │
         │  │ PersistenciaMonitoramento (CSVs)      │ │
         │  └──────────────────────────────────────┘ │
         └─────────────────────┬─────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │ RelatorioMonitoramento │
                    │  .txt + .csv           │
                    └───────────────────────┘
```

1. **Configuração** define limites de vegetação e quantas rodadas rodar.
2. Em cada **rodada**, opcionalmente **libera** equipes (simula fim do atendimento anterior).
3. Aplica **crescimento** simulado em cada trecho.
4. O **serviço de monitoramento** lista trechos em situação **crítica**, ordena por prioridade e tenta alocar a primeira equipe **disponível** a cada um. Trechos críticos que não recebem equipe entram em **`criticosSemEquipe`**.
5. **Persistência** salva instantâneos da rodada em CSV.
6. Ao final de todas as rodadas, o **relatório** consolida tudo em um TXT legível e um CSV tabular.

---

## Arquivos gerados em `diretorio.dados` (padrão `data/`)

Por rodada `N`:

| Arquivo | Conteúdo |
|---------|----------|
| `trechos_rodada_N.csv` | Todos os trechos: km inicial, km final, vegetação, prioridade. |
| `atendimentos_rodada_N.csv` | Cada alocação: equipe, km, vegetação, prioridade. |
| `criticos_sem_equipe_rodada_N.csv` | Trechos críticos sem equipe naquela rodada. |

Consolidado:

| Arquivo | Conteúdo |
|---------|----------|
| `{relatorio.base}.txt` | Relatório em texto por rodada: críticos, alocados, pendentes. |
| `{relatorio.base}.csv` | Linhas com tipo `CRITICO_ATENDIDO` ou `CRITICO_SEM_EQUIPE`. |

---

## Observações

- A **matriz de crescimento** está em código (`Main.CRESCIMENTO_CM_POR_RODADA`). Se houver **mais rodadas** na config do que **linhas** na matriz, o crescimento extra é **0** para todos os trechos.
- Não há banco de dados neste projeto: persistência é apenas **CSV** (simples e sem dependências extras).
- Mensagens de log no **console** também existem em classes de domínio (`TrechoRodovia`, `EquipeManutencao`, etc.) para acompanhamento durante a demo; relatórios formais estão nos arquivos acima.

## Resposta da Pergunta Reflexão
- Tornar `nivelVegetacao` público abre caminho para estados inválidos e para “mentir” no nível de vegetação, o que corrompe a previsão de criticidade, a alocação de equipes e a fidelidade dos artefatos persistidos (CSVs/relatórios), tudo isso sem passar pelos pontos únicos onde a regra de negócio deveria ser aplicada.
