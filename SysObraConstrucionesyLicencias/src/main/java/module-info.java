module pe.edu.upeu.sysobraconstrucionesylicencias {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;

    opens pe.edu.upeu.sysobraconstrucionesylicencias to javafx.fxml;
    opens pe.edu.upeu.sysobraconstrucionesylicencias.controller to javafx.fxml;
    opens pe.edu.upeu.sysobraconstrucionesylicencias.model;
    exports pe.edu.upeu.sysobraconstrucionesylicencias;
}
