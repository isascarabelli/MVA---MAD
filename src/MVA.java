import java.util.Scanner;

public class MVA {
    // Classe para armazenar as métricas de cada recurso para cada número médio de clientes
    static class Metricas {
        // Variáveis intermediárias
        float[] Q;                      // Número Médio de Clientes presentes em cada recurso
        float[] R;                      // Tempo de resposta de cada dispositivo
        float tempoRespostaSistema;     // Tempo de resposta do sistema
        float vazaoSistema;             // Vazão do sistema
        float[] vazao;                  // Vazão de cada recurso
        float[] utilizacao;             // Utilização de cada recurso

        void initialize(int K) {
            Q = new float[K];
            R = new float[K];
            utilizacao = new float[K];
            vazao = new float[K];
            tempoRespostaSistema = 0;

            // Inicializa todos os tempo médio de cliente de cada recurso com 0
            for (int i = 0; i < K; i++) {
                Q[i] = 0;
            }
        }

        float[] calculoTempoMedioResposta(float[] S, Metricas metricaAnterior) {
            for (int i = 0; i < R.length; i++) {
                // Usa o Q da métrica anterior (n-1 clientes)
                R[i] = S[i] * (1 + metricaAnterior.Q[i]);
            }
            return R;
        }

        float calculoTempoRespostaSistema(float[] visitas) {
            tempoRespostaSistema = 0;
            for (int i = 0; i < R.length; i++) {
                tempoRespostaSistema += R[i] * visitas[i];
            }
            return tempoRespostaSistema;
        }

        float calculoVazaoSistema(int n) {
            vazaoSistema = n / tempoRespostaSistema;
            return vazaoSistema;
        }

        float[] calculoVazao(float[] visitas) {
            for (int i = 0; i < vazao.length; i++) {
                vazao[i] = visitas[i] * vazaoSistema;
            }
            return vazao;
        }

        float[] calculoUtilizacao(float[] S) {
            for (int i = 0; i < utilizacao.length; i++) {
                utilizacao[i] = S[i] * vazao[i];
            }
            return utilizacao;
        }

        float[] calculoTempoMedioEspera() {
            for (int i = 0; i < Q.length; i++) {
                Q[i] = R[i] * vazao[i];
            }
            return Q;
        }
    }

    public static void main(String[] args) {
        System.out.println("\n========================================");
        System.out.println("ALGORITMO MVA - ANÁLISE DE VALOR MÉDIO");
        System.out.println("Amanda, Isabela e Pedro");
        System.out.println("Modelagem e Avaliação de Desempenho");
        System.out.println("========================================\n");

        Scanner sc = new Scanner(System.in);

        // Entradas
        System.out.println("Digite a quantidade de recursos do sistema: ");
        int K = sc.nextInt();

        System.out.println("Digite a quantidade de clientes do sistema: ");
        int N = sc.nextInt();

        // Informações gerais do sistema
        float lambda = 0;                  // Taxa de chegada
        float[] visitas = new float[K];    // Visitas (Xi/Xo)
        float[] Si = new float[K];         // Tempo de serviço de cada recurso

        // Vetor para cada iteração de clientes (0 a N)
        Metricas[] metricas = new Metricas[N + 1];

        System.out.println("Digite a taxa de chegada do sistema (lambda):");
        lambda = sc.nextFloat();

        for (int i = 0; i < K; i++) {
            System.out.printf("Digite o tempo de serviço (Si) do recurso %d, em segundos:\n", i);
            Si[i] = sc.nextFloat();

            System.out.printf("Digite o número médio de visitas no recurso %d: \n", i);
            visitas[i] = sc.nextFloat();
        }

        // Inicializa a métrica para 0 clientes
        metricas[0] = new Metricas();
        metricas[0].initialize(K);

        // Iteração sobre número de clientes (1 até N)
        for (int n = 1; n <= N; n++) {
            metricas[n] = new Metricas();
            metricas[n].initialize(K);

            // Calcula usando os valores da iteração anterior (n-1)
            // A ordem que as funções são chamadas são importantes para preencher os valores
            // que serão utilizados na próxima chamada de método
            metricas[n].R = metricas[n].calculoTempoMedioResposta(Si, metricas[n - 1]);
            metricas[n].tempoRespostaSistema = metricas[n].calculoTempoRespostaSistema(visitas);
            metricas[n].vazaoSistema = metricas[n].calculoVazaoSistema(n);
            metricas[n].vazao = metricas[n].calculoVazao(visitas);
            metricas[n].utilizacao = metricas[n].calculoUtilizacao(Si);
            metricas[n].Q = metricas[n].calculoTempoMedioEspera();

            // Realiza o print apenas dos passos intermediários, ou seja, sem ser o final, pois esse já é mostrado com os valores totais calculados
            if(n != N){
                // Print dos passos intermediários para acompanhamento das contas feitas
                System.out.println("\n============================================");
                System.out.printf("PASSOS INTERMEDIÁRIOS: Nº CLIENTES = %d", n);
                System.out.println("\n============================================\n");
                System.out.printf("Tempo de Resposta do Sistema (Ro): %.4f segundos\n", metricas[n].tempoRespostaSistema);
                System.out.printf("Vazão do Sistema (Xo): %.4f clientes/segundo\n\n", metricas[n].vazaoSistema);

                for (int i = 0; i < K; i++) {
                    System.out.println("--- Recurso " + i + " ---");
                    System.out.printf("  R[%d] = %.4f segundos\n", i, metricas[n].R[i]);
                    System.out.printf("  Q[%d] = %.4f clientes\n", i, metricas[n].Q[i]);
                    System.out.printf("  X[%d] = %.4f req/seg\n", i, metricas[n].vazao[i]);
                    System.out.printf("  U[%d] = %.4f\n", i, metricas[n].utilizacao[i]);
                }
            }
        }

        // Resultado final para N clientes
        System.out.println("\n========================================");
        System.out.println("RESULTADOS FINAIS PARA " + N + " CLIENTES");
        System.out.println("========================================\n");

        System.out.printf("Tempo Médio de Resposta do Sistema: %.4f segundos\n", metricas[N].tempoRespostaSistema);
        System.out.printf("Vazão Média do Sistema: %.4f clientes/segundo\n\n", metricas[N].vazaoSistema);

        for (int i = 0; i < K; i++) {
            System.out.println("--- Recurso " + i + " ---");
            System.out.printf("Tempo Médio de Resposta (R): %.4f segundos\n", metricas[N].R[i]);
            System.out.printf("Tempo Médio de Espera (W): %.4f segundos\n", metricas[N].R[i] - Si[i]);
            System.out.printf("Número Médio de Clientes (Q): %.4f clientes/segundo\n", metricas[N].Q[i]);
            System.out.printf("Vazão: %.4f requisições/segundo\n", metricas[N].vazao[i]);
            System.out.printf("Utilização (U): %.4f%% (%.4f)\n\n", metricas[N].utilizacao[i] * 100, metricas[N].utilizacao[i]);
        }

        sc.close();
    }
}