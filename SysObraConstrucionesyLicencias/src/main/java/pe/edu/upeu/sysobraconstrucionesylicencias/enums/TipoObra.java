package pe.edu.upeu.sysobraconstrucionesylicencias.enums;

import lombok.Getter;

@Getter
public enum TipoObra {
    NUEVA_CONSTRUCCION("Nueva construccion"),
    AMPLIACION("Ampliacion"),
    REMODELACION("Remodelacion");

    String descripcion;
    TipoObra(String descripcion){
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
