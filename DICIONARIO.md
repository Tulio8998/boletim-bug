# Dicionário de Alterações - Boletim Escolar

## Contexto
Script para gerar boletim da turma com cálculo de médias e situação dos alunos. Professores relataram que as médias estão saindo erradas.

## Tarefa 1 - Execução Inicial e Análise

### Resultado Atual (script original):
```
Ana: média 5.50 - Reprovado
Bruno: média 4.33 - Reprovado
Carla: média 3.67 - Reprovado
Diego: média 6.00 - Reprovado
```

### Resultado Esperado (conforme README):
```
Ana: média 8.17 - Aprovado
Bruno: média 6.00 - Recuperação
Carla: média 7.00 - Aprovado
Diego: média 7.00 - Aprovado
```

### Bugs Identificados Inicialmente:

1. **Bug no cálculo da média (`calcularMedia` - linha 24)**: Loop inicia em `i = 1` ao invés de `i = 0`, pulando a primeira nota. Além disso, divide por `notas.size()` mas soma apenas `notas.size() - 1` elementos.

2. **Bug no threshold de recuperação (`MEDIA_RECUPERACAO` - linha 17)**: Valor está 6.5, mas conforme README deveria ser 5.0 (Média >= 5.0: Recuperação).

3. **Bug na condição de aprovação (`situacao` - linha 34)**: Usa `media > mediaAprovacao` ao invés de `media >= mediaAprovacao`. Alunos com média exatamente 7.0 deveriam ser "Aprovado" (ex: Carla e Diego).

## Tarefa 2 - Explicação Completa do Código

### Arquivo Criado: `EXPLICACAO.md`

Documentação completa explicando:
- **Estrutura do projeto**: 4 arquivos principais (build.gradle, settings.gradle, gradlew, Boletim.groovy)
- **build.gradle**: Configuração Gradle com plugins groovy/application, dependências, mainClass
- **settings.gradle**: Nome do projeto (boletim-bug)
- **Boletim.groovy** (código principal) - 5 seções explicadas:
  1. Dados da turma (Map com 4 alunos, 3 notas cada)
  2. Regras da escola (constantes MEDIA_APROVACAO=7.0, MEDIA_RECUPERACAO=6.5 com bug)
  3. Função calcularMedia - bugs no loop (i=1) e divisão
  4. Função situacao - bug na condição (> ao invés de >=)
  5. Impressão do boletim - formatação com Locale.US
- **Gradle Wrapper**: gradlew/gradlew.bat e gradle-wrapper.properties
- **Fluxo de execução**: Diagrama passo a passo
- **Tabela de bugs conhecidos**: 3 bugs documentados com local e correção
- **Referências cruzadas**: README.md, DICIONARIO.md, Boletim.groovy, build.gradle

## Tarefa 3 - Identificação de Saídas Incorretas e Bugs Causadores

### Comparação: Atual vs Esperado

| Aluno | Atual (Incorreto) | Esperado | Diferenças |
|-------|-------------------|----------|------------|
| **Ana** | média 5.50 - Reprovado | média 8.17 - Aprovado | Média errada (5.50 vs 8.17), Situação errada (Reprovado vs Aprovado) |
| **Bruno** | média 4.33 - Reprovado | média 6.00 - Recuperação | Média errada (4.33 vs 6.00), Situação errada (Reprovado vs Recuperação) |
| **Carla** | média 3.67 - Reprovado | média 7.00 - Aprovado | Média errada (3.67 vs 7.00), Situação errada (Reprovado vs Aprovado) |
| **Diego** | média 6.00 - Reprovado | média 7.00 - Aprovado | Média errada (6.00 vs 7.00), Situação errada (Reprovado vs Aprovado) |

### Bugs Causadores por Aluno

#### Ana [8.0, 7.5, 9.0] → Média correta: 8.17
- **Bug 1 (Cálculo)**: Loop `i=1` pula nota 8.0 → soma apenas 7.5+9.0=16.5 → 16.5/3=5.50 ❌
- **Bug 3 (Aprovação)**: Mesmo se média fosse 8.17, condição `>` falharia para 7.0 exato (não afeta Ana, mas afeta outros)

#### Bruno [5.0, 6.0, 7.0] → Média correta: 6.00
- **Bug 1 (Cálculo)**: Loop `i=1` pula nota 5.0 → soma apenas 6.0+7.0=13.0 → 13.0/3=4.33 ❌
- **Bug 2 (Recuperação)**: Threshold 6.5 faz 6.00 cair em Reprovado (deveria ser 5.0 → Recuperação)

#### Carla [10.0, 4.0, 7.0] → Média correta: 7.00
- **Bug 1 (Cálculo)**: Loop `i=1` pula nota 10.0 → soma apenas 4.0+7.0=11.0 → 11.0/3=3.67 ❌
- **Bug 3 (Aprovação)**: Se média fosse 7.00, condição `media > 7.0` → falso → cai em Recuperação ❌ (deveria ser Aprovado com `>=`)

#### Diego [3.0, 9.0, 9.0] → Média correta: 7.00
- **Bug 1 (Cálculo)**: Loop `i=1` pula nota 3.0 → soma apenas 9.0+9.0=18.0 → 18.0/3=6.00 ❌
- **Bug 3 (Aprovação)**: Se média fosse 7.00, condição `media > 7.0` → falso → cai em Recuperação ❌ (deveria ser Aprovado com `>=`)

### Resumo dos 3 Bugs Raiz

| # | Bug | Local | Impacto |
|---|-----|-------|---------|
| **1** | Loop inicia em `i=1` | Linha 24 (`for (int i = 1;`) | **Todas as médias erradas** - pula primeira nota de cada aluno |
| **2** | `MEDIA_RECUPERACAO = 6.5` | Linha 17 | Bruno classificado como Reprovado em vez de Recuperação |
| **3** | `media > mediaAprovacao` | Linha 34 | Carla e Diego (média 7.0 exata) classificados como Recuperação em vez de Aprovado |

**Nota**: Bug 1 afeta TODOS os alunos. Bugs 2 e 3 afetam casos específicos de fronteira.

---

## Tarefa 4 - Correção dos Bugs (um por vez)

### Bug 1 - CORRIGIDO: Loop do cálculo da média (linha 24)
**Alteração**: `for (int i = 1;` → `for (int i = 0;`

**Resultado após correção do Bug 1:**
```
Ana: média 8.17 - Aprovado      ✓ (esperado: 8.17 - Aprovado)
Bruno: média 6.00 - Reprovado   ✗ (esperado: 6.00 - Recuperação)
Carla: média 7.00 - Recuperação ✗ (esperado: 7.00 - Aprovado)
Diego: média 7.00 - Recuperação ✗ (esperado: 7.00 - Aprovado)
```

**Análise**: 
- ✅ Ana: Correta (média 8.17, Aprovado)
- ❌ Bruno: Média correta (6.00), mas situação errada (Reprovado vs Recuperação) → **Bug 2**
- ❌ Carla: Média correta (7.00), mas situação errada (Recuperação vs Aprovado) → **Bug 3**
- ❌ Diego: Média correta (7.00), mas situação errada (Recuperação vs Aprovado) → **Bug 3**

---

### Bug 2 - CORRIGIDO: Threshold de recuperação (linha 17)
**Alteração**: `MEDIA_RECUPERACAO = 6.5` → `MEDIA_RECUPERACAO = 5.0`

**Resultado após correção do Bug 2:**
```
Ana: média 8.17 - Aprovado      ✓ (esperado: 8.17 - Aprovado)
Bruno: média 6.00 - Recuperação ✓ (esperado: 6.00 - Recuperação)
Carla: média 7.00 - Recuperação ✗ (esperado: 7.00 - Aprovado)
Diego: média 7.00 - Recuperação ✗ (esperado: 7.00 - Aprovado)
```

**Análise**: 
- ✅ Ana: Correta
- ✅ Bruno: Correta (média 6.00 ≥ 5.0 → Recuperação)
- ❌ Carla: Média correta (7.00), mas situação errada (Recuperação vs Aprovado) → **Bug 3**
- ❌ Diego: Média correta (7.00), mas situação errada (Recuperação vs Aprovado) → **Bug 3**

---

### Bug 3 - CORRIGIDO: Condição de aprovação (linha 34)
**Alteração**: `if (media > mediaAprovacao)` → `if (media >= mediaAprovacao)`

**Resultado FINAL após correção do Bug 3:**
```
Ana: média 8.17 - Aprovado      ✓ (esperado: 8.17 - Aprovado)
Bruno: média 6.00 - Recuperação ✓ (esperado: 6.00 - Recuperação)
Carla: média 7.00 - Aprovado    ✓ (esperado: 7.00 - Aprovado)
Diego: média 7.00 - Aprovado    ✓ (esperado: 7.00 - Aprovado)
```

**Análise Final**: 
- ✅ Ana: Correta
- ✅ Bruno: Correta
- ✅ Carla: Correta (média 7.00 ≥ 7.0 → Aprovado)
- ✅ Diego: Correta (média 7.00 ≥ 7.0 → Aprovado)

---

### ✅ TODOS OS 3 BUGS CORRIGIDOS - SAÍDA IDÊNTICA AO ESPERADO

**Resumo das 3 correções feitas:**

| # | Bug | Linha | Antes | Depois |
|---|-----|-------|-------|--------|
| 1 | Loop cálculo média | 24 | `for (int i = 1;` | `for (int i = 0;` |
| 2 | Threshold recuperação | 17 | `MEDIA_RECUPERACAO = 6.5` | `MEDIA_RECUPERACAO = 5.0` |
| 3 | Condição aprovação | 34 | `media > mediaAprovacao` | `media >= mediaAprovacao` |

---

## Tarefa 5 - Atualização do README

### Arquivo Atualizado: `README.md`

Adicionada seção **"Sobre o bug"** explicando cada um dos 3 bugs:

- **Bug 1** - Cálculo da média incorreto (linha 24): Loop pulava primeira nota, divisão errada
- **Bug 2** - Threshold de recuperação errado (linha 17): Valor 6.5 em vez de 5.0
- **Bug 3** - Condição de aprovação não incluía nota de corte (linha 34): Operador `>` em vez de `>=`

Cada bug documentado com: **Problema**, **Exemplo** (quando aplicável) e **Correção**.

Também marcado o **Checklist** como completo ([x] nos 3 itens).

---

## Tarefa 6 - Testes Unitários com Spock

### Arquivo Criado: `src/test/groovy/BoletimSpec.groovy`

Testes unitários cobrindo cada bug corrigido:

**Cobertura dos Bugs:**
1. **Bug 1 (Cálculo da média)**: 8 testes - médias corretas, soma de todas 3 notas, regressão loop i=1
2. **Bug 2 (Threshold recuperação)**: 5 testes - limites 5.0, 5.5, 6.0, 6.99, 4.99
3. **Bug 3 (Condição aprovação)**: 5 testes - limites 7.0, 7.01, 8.17, 10.0, 6.99

**Testes adicionais:**
- 4 testes de integração (cenários completos dos 4 alunos)
- 5 testes de fronteira (boundary cases)
- 1 teste de regressão específico para Bug 1

**Total: 28 testes** - Todos passando ✅

### Configuração: `build.gradle`
- Adicionado `testImplementation 'org.spockframework:spock-core:2.3-groovy-4.0'`
- Adicionado `testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.10.0'`
- Configurado `useJUnitPlatform()` para rodar testes Spock

### Validação:
```
./gradlew test --rerun-tasks
BUILD SUCCESSFUL
28 tests completed, 0 failed, 100% success
```