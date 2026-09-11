package Exceptions;

/*
    Se lanza cuando el texto que el usuario pego en la GUI no respeta el
    formato de entrada del enunciado: faltan datos, sobra basura, un token
    no es un numero, o un valor esta fuera del rango permitido.

    Es una excepcion CHEQUEADA (extends Exception) a proposito, igual que
    ENumeroNegativo: obliga a quien llame al parser a decidir que hacer con
    el error. La GUI la atrapa y muestra el mensaje en pantalla, que es justo
    lo que pide la seccion 2.2 del enunciado: un mensaje legible y nunca un
    stack trace ni un fallo silencioso.
*/
public class EEntradaInvalida extends Exception {

    public EEntradaInvalida(String mensaje) {
        super(mensaje);
    }
}