# Explicação Completa do Código - Boletim Escolar

## Visão Geral do Projeto

Este é um projeto Gradle/Groovy que calcula a média de alunos a partir de três notas e determina sua situação final (Aprovado, Recuperação ou Reprovado).

## Estrutura de Arquivos

```
boletim-bug/
├── build.gradle          # Configuração do build Gradle
├── settings.gradle       # Nome do projeto
├── gradlew / gradlew.bat # Gradle Wrapper (scripts de execução)
└── src/main/groovy/
    └── Boletim.groovy    # Código principal do boletim
```

---

## 1. Arquivo: `build.gradle`

### Propósito
Configura o build do projeto Gradle, definindo plugins, dependências e configurações de compilação.

### Conteúdo Explicado
```groovy
plugins {
    id 'groovy'           // Plugin para compilar código Groovy
    id 'application'      // Plugin para criar aplicação executável
}

repositories {
    mavenCentral()        // Repositório Maven Central para dependências
}

dependencies {
    implementation 'org.codehaus.groovy:groovy:3.0.18'  // Biblioteca Groovy core
    testImplementation 'org.spockframework:spock-core:2.3-groovy-3.0'  // Framework de testes
}

application {
    mainClass = 'Boletim'  // Classe principal a ser executada
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)  // Requer Java 17+
    }
}
```

### Referências no Código Principal
- O `mainClass = 'Boletim'` aponta para a classe `Boletim` definida em `src/main/groovy/Boletim.groovy`
- O plugin `groovy` permite compilar o arquivo `.groovy`
- O plugin `application` permite executar com `./gradlew run`

---

## 2. Arquivo: `settings.gradle`

### Propósito
Define o nome do projeto Gradle.

### Conteúdo
```groovy
rootProject.name = 'boletim-bug'
```

### Referências
- Nome usado em relatórios de build e publicações

---

## 3. Arquivo: `src/main/groovy/Boletim.groovy`

### Propósito
Código principal que processa os dados dos alunos, calcula médias e imprime o boletim.

### Bibliotecas/Imports Usados
```groovy
// Implícito - Groovy importa automaticamente:
// java.util.*, java.io.*, java.net.*, groovy.lang.*, groovy.util.*
// java.math.BigDecimal, java.math.BigInteger
//
// Usado explicitamente no código:
// Locale.US (java.util.Locale) - para formatação de números com ponto decimal
```

### Estrutura do Código (por seções)

#### Seção 1: Dados da Turma (Linhas 6-11)
```groovy
def alunos = [
    "Ana"  : [8.0, 7.5, 9.0],
    "Bruno": [5.0, 6.0, 7.0],
    "Carla": [10.0, 4.0, 7.0],
    "Diego": [3.0, 9.0, 9.0],
]
```
- **Tipo**: `Map<String, List<Double>>` (Mapa de nome → lista de notas)
- **Estrutura**: 4 alunos, cada um com 3 notas (Double)
- **Referência**: Usado no `each` da linha 47 para iterar sobre cada aluno

#### Seção 2: Regras da Escola (Linhas 16-17)
```groovy
final MEDIA_APROVACAO = 7.0
final MEDIA_RECUPERACAO = 6.5  // BUG: deveria ser 5.0 conforme README
```
- **Tipo**: Constantes `double` (final = imutável)
- **Regra conforme README**:
  - Média >= 7.0 → Aprovado
  - Média >= 5.0 → Recuperação
  - Abaixo → Reprovado
- **Bug identificado**: `MEDIA_RECUPERACAO = 6.5` está incorreto, deveria ser `5.0`
- **Referência**: Passados como parâmetros para função `situacao()` na linha 50

#### Seção 3: Função `calcularMedia(List notas)` (Linhas 22-28)
```groovy
def calcularMedia(List notas) {
    def soma = 0
    for (int i = 1; i < notas.size(); i++) {  // BUG: deveria ser i = 0
        soma += notas[i]
    }
    return soma / notas.size()  // BUG: divide pelo total mas soma apenas n-1 elementos
}
```
- **Entrada**: `List<Double> notas` (ex: [8.0, 7.5, 9.0])
- **Saída**: `double` (média aritmética)
- **Algoritmo**: Soma todas as notas e divide pela quantidade
- **Bugs identificados**:
  1. Loop inicia em `i = 1` → **pula a primeira nota** (índice 0)
  2. Divide por `notas.size()` (3) mas soma apenas 2 elementos → média incorreta
- **Exemplo do bug**: Para Ana [8.0, 7.5, 9.0]:
  - Atual: soma = 7.5 + 9.0 = 16.5, média = 16.5/3 = 5.50 ❌
  - Correto: soma = 8.0 + 7.5 + 9.0 = 24.5, média = 24.5/3 = 8.17 ✓
- **Referência**: Chamada na linha 48 para cada aluno

#### Seção 4: Função `situacao(media, mediaAprovacao, mediaRecuperacao)` (Linhas 33-41)
```groovy
def situacao(media, mediaAprovacao, mediaRecuperacao) {
    if (media > mediaAprovacao) {        // BUG: deveria ser >=
        return "Aprovado"
    } else if (media >= mediaRecuperacao) {
        return "Recuperação"
    } else {
        return "Reprovado"
    }
}
```
- **Entrada**: 3 parâmetros `double` (média do aluno, threshold aprovação, threshold recuperação)
- **Saída**: `String` ("Aprovado", "Recuperação" ou "Reprovado")
- **Lógica**: Cadeia if-else-if para classificar
- **Bug identificado**: Linha 34 usa `>` (maior estrito) ao invés de `>=` (maior ou igual)
  - Aluno com média **exatamente 7.0** cai no `else if` → "Recuperação" ❌
  - Deveria ser "Aprovado" conforme regra "Média >= 7.0: Aprovado" ✓
- **Casos de fronteira afetados**: Carla (7.0) e Diego (7.0)
- **Referência**: Chamada na linha 50 com as constantes da seção 2

#### Seção 5: Impressão do Boletim (Linhas 46-51)
```groovy
println "=== Boletim da turma ==="
alunos.each { nome, notas ->
    def media = calcularMedia(notas)
    def mediaFormatada = String.format(Locale.US, "%.2f", media)
    println "${nome}: média ${mediaFormatada} - ${situacao(media, MEDIA_APROVACAO, MEDIA_RECUPERACAO)}"
}
```
- **`alunos.each`**: Itera sobre o mapa (closure recebe chave= nome, valor= notas)
- **`calcularMedia(notas)`**: Calcula média (com bug)
- **`String.format(Locale.US, "%.2f", media)`**: Formata com 2 casas decimais, usando ponto (.) como separador decimal
  - `Locale.US` garante formato "8.17" e não "8,17" (padrão BR)
- **`println`**: Imprime linha formatada: `Nome: média X.XX - Situação`
- **Referência**: Usa todas as seções anteriores