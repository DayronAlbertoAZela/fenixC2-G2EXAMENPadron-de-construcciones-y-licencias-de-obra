package pe.edu.upeu.sysobraconstrucionesylicencias.exception;

public class ModelNotFoundException extends RuntimeException{
    public ModelNotFoundException(String message) {
        super(message);
    }
}
