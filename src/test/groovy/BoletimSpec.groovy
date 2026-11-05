package boletim

import spock.lang.Specification
import spock.lang.Unroll

import static boletim.Boletim.calcularMedia
import static boletim.Boletim.situacao

class BoletimSpec extends Specification {

    // ============================================================
    // BUG 1 - Cálculo da média
    // ============================================================

    @Unroll
    def "Bug 1: calcularMedia deve somar todas as #qtd notas e dividir pela quantidade correta"() {
        given:
        def notas = notasList
        def mediaEsperada = somaEsperada / notasList.size()

        when:
        def media = calcularMedia(notas)

        then:
        media == mediaEsperada

        where:
        notasList              | somaEsperada | qtd
        [8.0, 7.5, 9.0]        | 24.5         | 3
        [5.0, 6.0, 7.0]        | 18.0         | 3
        [10.0, 4.0, 7.0]       | 21.0         | 3
        [3.0, 9.0, 9.0]        | 21.0         | 3
        [10.0, 10.0, 10.0]     | 30.0         | 3
        [0.0, 0.0, 0.0]        | 0.0          | 3
        [7.0, 7.0, 7.0]        | 21.0         | 3
        [6.5, 6.5, 6.5]        | 19.5         | 3
    }

    def "Bug 1 - Regressão: loop não deve começar em i=1 (pular primeira nota)"() {
        given:
        def notas = [8.0, 7.5, 9.0]

        when:
        def media = calcularMedia(notas)

        then:
        media > 8.16
        media < 8.17
        media != 5.50
    }

    // ============================================================
    // BUG 2 - Threshold de recuperação (MEDIA_RECUPERACAO = 5.0)
@Unroll
    def "Bug 2: situacao deve retornar 'Recuperação' para média #mediaValor (>= 5.0 e < 7.0)"() {
        given:
        def media = mediaValor

        when:
        def resultado = situacao(media, 7.0, 5.0)

        then:
        resultado == "Recuperação"

        where:
        mediaValor << [5.0, 5.5, 6.0, 6.99]
    }

    def "Bug 2: situacao deve retornar 'Reprovado' para média 4.99 (< 5.0)"() {
        given:
        def media = 4.99

        when:
        def resultado = situacao(media, 7.0, 5.0)

        then:
        resultado == "Reprovado"
    }

    // ============================================================
    // BUG 3 - Condição de aprovação (>= ao invés de >)
    // ============================================================

    @Unroll
    def "Bug 3: situacao deve retornar 'Aprovado' para média #mediaValor (>= 7.0)"() {
        given:
        def media = mediaValor

        when:
        def resultado = situacao(media, 7.0, 5.0)

        then:
        resultado == "Aprovado"

        where:
        mediaValor << [7.0, 7.01, 8.17, 10.0]
    }

    def "Bug 3: situacao deve retornar 'Recuperação' para média 6.99 (< 7.0)"() {
        given:
        def media = 6.99

        when:
        def resultado = situacao(media, 7.0, 5.0)

        then:
        resultado == "Recuperação"
    }

    // ============================================================
    // TESTES DE INTEGRAÇÃO - Cenários completos dos 4 alunos
    // ============================================================

    def "Integração: Ana - média 8.17, Aprovado"() {
        given:
        def notas = [8.0, 7.5, 9.0]

        when:
        def media = calcularMedia(notas)
        def resultado = situacao(media, 7.0, 5.0)

        then:
        Math.abs(media - 8.166666666666666) < 0.0000001
        String.format(Locale.US, "%.2f", media) == "8.17"
        resultado == "Aprovado"
    }

    def "Integração: Bruno - média 6.00, Recuperação"() {
        given:
        def notas = [5.0, 6.0, 7.0]

        when:
        def media = calcularMedia(notas)
        def resultado = situacao(media, 7.0, 5.0)

        then:
        media == 6.0
        String.format(Locale.US, "%.2f", media) == "6.00"
        resultado == "Recuperação"
    }

    def "Integração: Carla - média 7.00, Aprovado"() {
        given:
        def notas = [10.0, 4.0, 7.0]

        when:
        def media = calcularMedia(notas)
        def resultado = situacao(media, 7.0, 5.0)

        then:
        media == 7.0
        String.format(Locale.US, "%.2f", media) == "7.00"
        resultado == "Aprovado"
    }

    def "Integração: Diego - média 7.00, Aprovado"() {
        given:
        def notas = [3.0, 9.0, 9.0]

        when:
        def media = calcularMedia(notas)
        def resultado = situacao(media, 7.0, 5.0)

        then:
        media == 7.0
        String.format(Locale.US, "%.2f", media) == "7.00"
        resultado == "Aprovado"
    }

    // ============================================================
    // TESTES DE FRONTEIRA (Boundary Cases)
    // ============================================================

    @Unroll
    def "Fronteira: média exatamente #mediaValor deve ter situação #situacaoEsperada"() {
        given:
        def media = mediaValor

        when:
        def resultado = situacao(media, 7.0, 5.0)

        then:
        resultado == situacaoEsperada

        where:
        mediaValor       | situacaoEsperada
        5.0              | "Recuperação"
        4.999            | "Reprovado"
        7.0              | "Aprovado"
        6.999            | "Recuperação"
        0.0              | "Reprovado"
    }

    def "Fronteira: calcularMedia com notas negativas"() {
        given:
        def notas = [-1.0, 5.0, 10.0]

        when:
        def media = calcularMedia(notas)

        then:
        media == 14.0 / 3.0
    }

    def "Fronteira: calcularMedia com uma única nota"() {
        given:
        def notas = [7.5]

        when:
        def media = calcularMedia(notas)

        then:
        media == 7.5
    }

    def "Fronteira: calcularMedia com duas notas"() {
        given:
        def notas = [6.0, 8.0]

        when:
        def media = calcularMedia(notas)

        then:
        media == 7.0
    }

    def "Fronteira: situacao com MEDIA_RECUPERACAO igual a MEDIA_APROVACAO"() {
        given:
        def media = 7.0

        when:
        def resultado = situacao(media, 7.0, 7.0)

        then:
        resultado == "Aprovado"
    }

    // ============================================================
    // TESTES ADICIONAIS DE VALIDAÇÃO
    // ============================================================

    def "Validar: turma completa deve ter 4 alunos"() {
        given:
        def alunos = [
            "Ana"  : [8.0, 7.5, 9.0],
            "Bruno": [5.0, 6.0, 7.0],
            "Carla": [10.0, 4.0, 7.0],
            "Diego": [3.0, 9.0, 9.0],
        ]

        expect:
        alunos.size() == 4
    }

    def "Validar: todas as notas dos alunos devem ter 3 elementos"() {
        given:
        def alunos = [
            "Ana"  : [8.0, 7.5, 9.0],
            "Bruno": [5.0, 6.0, 7.0],
            "Carla": [10.0, 4.0, 7.0],
            "Diego": [3.0, 9.0, 9.0],
        ]

        expect:
        alunos.values().every { it.size() == 3 }
    }
}
    // ============================================================