package pruebas;

import java.util.ArrayList;
import java.util.List;

/**
 * Runner y pequeñas utilidades de aserción. No usa JUnit ni ninguna otra
 * dependencia externa: el classpath del proyecto debe seguir vacío.
 */
public final class Pruebas {

    private static int comprobaciones;
    private static int fallos;
    private static final List<String> mensajesDeFallo = new ArrayList<String>();

    private Pruebas() {
    }

    /**
     * Comprueba una condición.
     *
     * @param condicion condición que debe cumplirse
     * @param mensaje   descripción usada si falla
     */
    public static void afirmar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) {
            registrarFallo(mensaje);
        }
    }

    /**
     * Comprueba que dos enteros son iguales.
     *
     * @param esperado valor esperado
     * @param real     valor obtenido
     * @param mensaje  descripción usada si falla
     */
    public static void afirmarIgual(int esperado, int real, String mensaje) {
        afirmar(esperado == real, mensaje + " (esperado=" + esperado + ", real=" + real + ")");
    }

    /**
     * Comprueba que dos objetos son iguales.
     *
     * @param esperado valor esperado
     * @param real     valor obtenido
     * @param mensaje  descripción usada si falla
     */
    public static void afirmarIgual(Object esperado, Object real, String mensaje) {
        boolean iguales = esperado == null ? real == null : esperado.equals(real);
        afirmar(iguales, mensaje + " (esperado=" + esperado + ", real=" + real + ")");
    }

    /**
     * Comprueba que un objeto no es nulo.
     *
     * @param valor   objeto comprobado
     * @param mensaje descripción usada si falla
     */
    public static void afirmarNoNulo(Object valor, String mensaje) {
        afirmar(valor != null, mensaje);
    }

    /**
     * Comprueba que una acción lanza una excepción del tipo indicado.
     *
     * @param tipo    tipo de excepción esperado
     * @param accion  acción que debe fallar
     * @param mensaje descripción usada si falla
     */
    public static void afirmarLanza(Class<? extends Throwable> tipo, Runnable accion,
            String mensaje) {
        comprobaciones++;
        try {
            accion.run();
            registrarFallo(mensaje + " (no se lanzó ninguna excepción)");
        } catch (Throwable error) {
            if (!tipo.isInstance(error)) {
                registrarFallo(mensaje + " (se esperaba " + tipo.getSimpleName()
                        + " y se lanzó " + error.getClass().getSimpleName() + ")");
            }
        }
    }

    /**
     * Ejecuta un bloque de pruebas y captura cualquier excepción inesperada.
     *
     * @param nombre nombre del bloque
     * @param prueba código que se ejecuta
     */
    public static void ejecutar(String nombre, Runnable prueba) {
        System.out.println("== " + nombre);
        try {
            prueba.run();
        } catch (Throwable error) {
            registrarFallo("Excepción inesperada en '" + nombre + "': " + error);
            error.printStackTrace(System.out);
        }
    }

    private static void registrarFallo(String mensaje) {
        fallos++;
        mensajesDeFallo.add(mensaje);
        System.out.println("  FALLO: " + mensaje);
    }

    /**
     * Punto de entrada de la batería de pruebas.
     *
     * @param argumentos no se usan
     */
    public static void main(String[] argumentos) {
        TableroPruebas.ejecutar();
        ArbitroPruebas.ejecutar();
        PartidaPruebas.ejecutar();
        IAPruebas.ejecutar();

        System.out.println();
        if (fallos == 0) {
            System.out.println("OK: " + comprobaciones + " comprobaciones superadas.");
            return;
        }
        System.out.println("FALLARON " + fallos + " de " + comprobaciones + " comprobaciones.");
        for (String mensaje : mensajesDeFallo) {
            System.out.println(" - " + mensaje);
        }
        System.exit(1);
    }
}
