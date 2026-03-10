package co.edu.uceva.potenciacomparacion;

/**
 * ============================================================================
 * Comparacion de Algoritmos para Calculo de Potencias
 * ============================================================================
 *
 * Estudiantes:
 *   - [Juan Esteban Castañeda Montaño - 230232003]
 *   - [Nombre Estudiante 2]
 *
 *   Este programa implementa y compara cuatro metodos diferentes para
 *   calcular la potencia b^n:
 *
 *   1. Metodo Iterativo Tradicional - O(n)
 *   2. Metodo Divide y Vencerás - O(log n)
 *   3. Metodo usando Logaritmo y Exponencial - O(1)
 *   4. Metodo Nativo de Java (Math.pow)
 *
 * ============================================================================
 */

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class PotenciaComparacionApplication {

    // Tiempo límite para el metodo iterativo (en segundos)
    private static final double TIMEOUT_ITERATIVO = 2.0;

    // Número de repeticiones para medir tiempos más precisos
    private static final int REPETICIONES = 5;

    // ========================================================================
    // METODO 1: ITERATIVO TRADICIONAL - O(n)
    // ========================================================================
    /**
     * Calcula la potencia usando multiplicaciones sucesivas
     *
     * @param base La base de la potencia
     * @param exponente El exponente (debe ser >= 0)
     * @return El resultado de base^exponente
     */
    public static double potenciaIterativa(double base, long exponente) {
        // Casos especiales
        if (exponente == 0) return 1.0;
        if (exponente == 1) return base;
        if (base == 0) return 0.0;
        if (base == 1) return 1.0;

        double resultado = 1.0;
        boolean exponeneteNegativo = exponente < 0;
        long exp = Math.abs(exponente);

        // Multiplicación iterativa n veces
        for (long i = 0; i < exp; i++) {
            resultado *= base;

            // Verificar overflow
            if (Double.isInfinite(resultado) || Double.isNaN(resultado)) {
                return resultado;
            }
        }

        return exponeneteNegativo ? 1.0 / resultado : resultado;
    }

    // ========================================================================
    // METODO 2: DIVIDE Y VENCERÁS - O(log n)
    // ========================================================================
    /**
     * Calcula la potencia usando exponenciacion binaria (divide y venceras)
     *
     * Principio: b^n = (b^(n/2))^2 si n es par
     *            b^n = b * (b^(n/2))^2 si n es impar
     *
     * @param base La base de la potencia
     * @param exponente El exponente (debe ser >= 0)
     * @return El resultado de base^exponente
     */
    public static double potenciaDivideVenceras(double base, long exponente) {
        // Casos especiales
        if (exponente == 0) return 1.0;
        if (exponente == 1) return base;
        if (base == 0) return 0.0;
        if (base == 1) return 1.0;

        boolean exponenteNegativo = exponente < 0;
        long exp = Math.abs(exponente);

        double resultado = 1.0;
        double baseActual = base;

        // Exponenciación binaria iterativa
        while (exp > 0) {
            // Si el bit menos significativo es 1, multiplicar por la base actual
            if ((exp & 1) == 1) {
                resultado *= baseActual;
            }
            // Elevar al cuadrado la base
            baseActual *= baseActual;
            // Desplazar el exponente a la derecha (dividir por 2)
            exp >>= 1;

            // Verificar overflow
            if (Double.isInfinite(resultado) || Double.isNaN(resultado)) {
                return exponenteNegativo ? 0.0 : resultado;
            }
        }

        return exponenteNegativo ? 1.0 / resultado : resultado;
    }

    // ========================================================================
    // METODO 3: LOGARITMO Y EXPONENCIAL - O(1)
    // ========================================================================
    /**
     * Calcula la potencia usando propiedades de logaritmos
     *
     * Principio: b^n = e^(n * ln(b))
     *
     * NOTA: Puede tener errores de precision en punto flotante
     *
     * @param base La base de la potencia (debe ser > 0)
     * @param exponente El exponente
     * @return El resultado de base^exponente
     */
    public static double potenciaLogaritmo(double base, long exponente) {
        // Casos especiales
        if (exponente == 0) return 1.0;
        if (base == 0) return (exponente > 0) ? 0.0 : Double.POSITIVE_INFINITY;
        if (base == 1) return 1.0;
        if (exponente == 1) return base;

        // Para bases negativas, el logaritmo no está definido en los reales
        if (base < 0) {
            // Si el exponente es par, el resultado es positivo
            // Si el exponente es impar, el resultado es negativo
            double resultado = Math.exp(exponente * Math.log(Math.abs(base)));
            return (exponente % 2 == 0) ? resultado : -resultado;
        }

        // Aplicar: b^n = e^(n * ln(b))
        return Math.exp(exponente * Math.log(base));
    }

    // ========================================================================
    // METODO 4: FUNCIÓN NATIVA DE JAVA - Math.pow()
    // ========================================================================
    /**
     * Calcula la potencia usando la funcion nativa de Java
     *
     * @param base La base de la potencia
     * @param exponente El exponente
     * @return El resultado de base^exponente
     */
    public static double potenciaNativa(double base, long exponente) {
        return Math.pow(base, exponente);
    }

    // ========================================================================
    // FUNCIONES DE MEDICIÓN DE TIEMPO
    // ========================================================================

    /**
     * Interfaz funcional para los metodos de potencia
     */
    @FunctionalInterface
    interface MetodoPotencia {
        double calcular(double base, long exponente);
    }

    /**
     * Mide el tiempo de ejecucion de un metodo de potencia
     *
     * @param metodo El metodo a medir
     * @param base La base
     * @param exponente El exponente
     * @param repeticiones Número de repeticiones para promediar
     * @return Tiempo promedio en segundos, o -1 si hay timeout/error
     */
    public static double medirTiempo(MetodoPotencia metodo, double base, long exponente,
                                     int repeticiones, double timeout) {
        double tiempoTotal = 0;

        for (int i = 0; i < repeticiones; i++) {
            long inicio = System.nanoTime();
            double resultado = metodo.calcular(base, exponente);
            long fin = System.nanoTime();

            double tiempoActual = (fin - inicio) / 1_000_000_000.0;
            tiempoTotal += tiempoActual;

            // Verificar timeout después de la primera ejecución
            if (i == 0 && tiempoActual > timeout) {
                return -1; // Indica timeout
            }
        }

        return tiempoTotal / repeticiones;
    }

    /**
     * Formatea el tiempo para mostrar en la tabla
     */
    public static String formatearTiempo(double tiempo) {
        if (tiempo < 0) {
            return "N/A (timeout)";
        } else if (Double.isNaN(tiempo) || Double.isInfinite(tiempo)) {
            return "Error";
        } else {
            return String.format("%.6e s", tiempo);
        }
    }

    /**
     * Formatea un numero grande con separadores de miles
     */
    public static String formatearNumero(long numero) {
        return String.format("%,d", numero);
    }

    // ========================================================================
    // FUNCIÓN PRINCIPAL DE COMPARACIÓN
    // ========================================================================

    /**
     * Clase para almacenar los resultados de una prueba
     */
    static class ResultadoPrueba {
        double base;
        long exponente;
        double tiempoIterativo;
        double tiempoDivideVenceras;
        double tiempoLogaritmo;
        double tiempoNativo;

        public ResultadoPrueba(double base, long exponente) {
            this.base = base;
            this.exponente = exponente;
            this.tiempoIterativo = -1;
            this.tiempoDivideVenceras = -1;
            this.tiempoLogaritmo = -1;
            this.tiempoNativo = -1;
        }
    }

    /**
     * Ejecuta la comparacion de todos los metodos con diferentes exponentes
     */
    public static List<ResultadoPrueba> ejecutarComparacion(double base, long[] exponentes) {
        List<ResultadoPrueba> resultados = new ArrayList<>();

        System.out.println("\nEjecutando pruebas de rendimiento...\n");

        for (long exp : exponentes) {
            System.out.printf("  Probando exponente: %,d%n", exp);
            ResultadoPrueba resultado = new ResultadoPrueba(base, exp);

            // Metodo Iterativo (solo para exponentes pequeños)
            if (exp <= 100_000) {
                resultado.tiempoIterativo = medirTiempo(
                        PotenciaComparacionApplication::potenciaIterativa,
                        base, exp, REPETICIONES, TIMEOUT_ITERATIVO
                );
            }

            // Metodo Divide y Vencerás
            resultado.tiempoDivideVenceras = medirTiempo(
                    PotenciaComparacionApplication::potenciaDivideVenceras,
                    base, exp, REPETICIONES, 10.0
            );

            // Metodo Logaritmo
            resultado.tiempoLogaritmo = medirTiempo(
                    PotenciaComparacionApplication::potenciaLogaritmo,
                    base, exp, REPETICIONES, 10.0
            );

            // Metodo Nativo
            resultado.tiempoNativo = medirTiempo(
                    PotenciaComparacionApplication::potenciaNativa,
                    base, exp, REPETICIONES, 10.0
            );

            resultados.add(resultado);
        }

        return resultados;
    }

    /**
     * Imprime la tabla de tiempos de ejecucion formateada
     */
    public static void imprimirTablaTiempos(List<ResultadoPrueba> resultados) {
        System.out.println("\n" + "=".repeat(110));
        System.out.println("TABLA DE TIEMPOS DE EJECUCION");
        System.out.println("=".repeat(110));

        // Headers
        System.out.printf("%-8s %-14s %-18s %-18s %-18s %-18s%n",
                "Base", "Exponente", "Iterativo O(n)", "Divide/Venceras", "Logaritmo O(1)", "Math.pow()");
        System.out.println("-".repeat(110));

        // Datos
        for (ResultadoPrueba r : resultados) {
            System.out.printf("%-8.1f %-14s %-18s %-18s %-18s %-18s%n",
                    r.base,
                    formatearNumero(r.exponente),
                    formatearTiempo(r.tiempoIterativo),
                    formatearTiempo(r.tiempoDivideVenceras),
                    formatearTiempo(r.tiempoLogaritmo),
                    formatearTiempo(r.tiempoNativo)
            );
        }

        System.out.println("-".repeat(110));
    }

    /**
     * Imprime las conclusiones del analisis de complejidad
     */
    public static void imprimirConclusiones() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONCLUSIONES DEL ANALISIS");
        System.out.println("=".repeat(80));

        System.out.println("""

        1. METODO ITERATIVO - O(n):
           - El tiempo crece linealmente con el exponente
           - Para exponentes grandes (>100,000) se vuelve impractico
           - Simple de implementar pero ineficiente

        2. METODO DIVIDE Y VENCERÁS - O(log n):
           - El tiempo se mantiene casi constante incluso para exponentes muy grandes
           - Solo requiere log2(n) multiplicaciones
           - Excelente balance entre precision y rendimiento
           - RECOMENDADO para calculos con precision exacta

        3. METODO LOGARITMO/EXPONENCIAL - O(1):
           - Tiempo constante independiente del exponente
           - Puede presentar errores de precision en punto flotante
           - Ideal cuando la precision aproximada es aceptable

        4. METODO NATIVO Math.pow():
           - Usa algoritmos similares a Divide y Venceras internamente
           - Puede generar Infinity para resultados muy grandes
           - RECOMENDADO para uso general

        CONCLUSIÓN GENERAL:
           Para exponentes pequeños, cualquier metodo es adecuado
           Para exponentes grandes, usar Divide y Venceras o Math.pow()
           El metodo iterativo debe evitarse para exponentes > 10,000
        """);
    }

    /**
     * Verifica que todos los metodos producen el mismo resultado
     */
    public static void verificarResultados(double base, long exponente) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("VERIFICACIÓN DE RESULTADOS");
        System.out.println("=".repeat(80));
        System.out.printf("Base: %.2f, Exponente: %d%n%n", base, exponente);

        double resIterativo = potenciaIterativa(base, exponente);
        double resDivideVenceras = potenciaDivideVenceras(base, exponente);
        double resLogaritmo = potenciaLogaritmo(base, exponente);
        double resNativo = potenciaNativa(base, exponente);

        System.out.printf("  Iterativo:          %.10f%n", resIterativo);
        System.out.printf("  Divide y Vencerás:  %.10f%n", resDivideVenceras);
        System.out.printf("  Logaritmo:          %.10f%n", resLogaritmo);
        System.out.printf("  Math.pow() Nativo:  %.10f%n", resNativo);

        // Verificar diferencias
        System.out.println("\nDiferencias respecto a Math.pow():");
        System.out.printf("  |Iterativo - Nativo|:         %.2e%n", Math.abs(resIterativo - resNativo));
        System.out.printf("  |Divide/Vencerás - Nativo|:   %.2e%n", Math.abs(resDivideVenceras - resNativo));
        System.out.printf("  |Logaritmo - Nativo|:         %.2e%n", Math.abs(resLogaritmo - resNativo));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Encabezado del programa
        System.out.println("=".repeat(80));
        System.out.println("PROGRAMA DE COMPARACIÓN DE ALGORITMOS PARA CÁLCULO DE POTENCIAS");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("Estudiantes:");
        System.out.println("  - [Juan Esteban Castañeda Montaño - 230232003]");
        System.out.println("  - [Nombre Estudiante 2]");
        System.out.println();
        System.out.println("Este programa implementa y compara cuatro metodos para calcular b^n:");
        System.out.println("  1. Iterativo Tradicional - O(n)");
        System.out.println("  2. Divide y Vencerás - O(log n)");
        System.out.println("  3. Logaritmo y Exponencial - O(1)");
        System.out.println("  4. Math.pow() Nativo de Java");
        System.out.println();

        // Solicitar valores al usuario
        System.out.print("Ingrese la base (b): ");
        double base = scanner.nextDouble();

        System.out.print("Ingrese el exponente (n): ");
        long exponente = scanner.nextLong();

        // Calcular y mostrar resultado con el valor ingresado
        System.out.println("\n" + "-".repeat(80));
        System.out.println("CÁLCULO CON LOS VALORES INGRESADOS");
        System.out.println("-".repeat(80));
        System.out.printf("Calculando: %.2f ^ %d%n%n", base, exponente);

        // Calcular con cada metodo
        long inicio, fin;

        // Iterativo
        if (exponente <= 10_000_000) {
            inicio = System.nanoTime();
            double resIter = potenciaIterativa(base, exponente);
            fin = System.nanoTime();
            System.out.printf("Iterativo:          %.10f (Tiempo: %.6e s)%n",
                    resIter, (fin - inicio) / 1_000_000_000.0);
        } else {
            System.out.println("Iterativo:          Omitido (exponente muy grande)");
        }

        // Divide y Vencerás
        inicio = System.nanoTime();
        double resDV = potenciaDivideVenceras(base, exponente);
        fin = System.nanoTime();
        System.out.printf("Divide y Vencerás:  %.10f (Tiempo: %.6e s)%n",
                resDV, (fin - inicio) / 1_000_000_000.0);

        // Logaritmo
        inicio = System.nanoTime();
        double resLog = potenciaLogaritmo(base, exponente);
        fin = System.nanoTime();
        System.out.printf("Logaritmo:          %.10f (Tiempo: %.6e s)%n",
                resLog, (fin - inicio) / 1_000_000_000.0);

        // Nativo
        inicio = System.nanoTime();
        double resNat = potenciaNativa(base, exponente);
        fin = System.nanoTime();
        System.out.printf("Math.pow():         %.10f (Tiempo: %.6e s)%n",
                resNat, (fin - inicio) / 1_000_000_000.0);

        // Verificar resultados con exponente pequeño
        verificarResultados(base, 20);

        // ====================================================================
        // GENERAR TABLA DE TIEMPOS CON EXPONENTES GRANDES
        // ====================================================================
        System.out.println("\n" + "=".repeat(80));
        System.out.println("GENERANDO TABLA DE TIEMPOS CON EXPONENTES GRANDES");
        System.out.println("=".repeat(80));

        // Exponentes de prueba
        long[] exponentes = {10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000};

        // Ejecutar comparación
        List<ResultadoPrueba> resultados = ejecutarComparacion(base, exponentes);

        // Imprimir tabla de tiempos
        imprimirTablaTiempos(resultados);

        // Imprimir conclusiones
        imprimirConclusiones();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("FIN DEL PROGRAMA");
        System.out.println("=".repeat(80));

        scanner.close();
    }
}

//JECM
