package pe.edu.upeu.syslicencias.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pe.edu.upeu.syslicencias.components.ColumnInfo;
import pe.edu.upeu.syslicencias.components.ComboBoxAutoComplete;
import pe.edu.upeu.syslicencias.components.TableViewHelper;
import pe.edu.upeu.syslicencias.components.Toast;
import pe.edu.upeu.syslicencias.components.ToltipCustom;
import pe.edu.upeu.syslicencias.dto.ComboBoxOption;
import pe.edu.upeu.syslicencias.enums.TipoObra;
import pe.edu.upeu.syslicencias.exception.LicenciaDuplicadaException;
import pe.edu.upeu.syslicencias.model.LicenciaObra;
import pe.edu.upeu.syslicencias.service.ILicenciaObraService;

import java.util.*;
import java.util.function.Consumer;

public class LicenciaObraController {

    @FXML private AnchorPane miContenedor;
    @FXML private TextField txtNumeroLicencia, txtDireccion, txtPropietario, txtFiltroDato;
    @FXML private ComboBox<ComboBoxOption> cbxTipoObra;
    @FXML private DatePicker dpFechaInicio, dpFechaFin;
    @FXML private TableView<LicenciaObra> tableView;
    @FXML private Label lbnMsg;

    private final ILicenciaObraService ls;          // depende de la INTERFAZ
    private final ToltipCustom ttc = new ToltipCustom();
    private Validator validator;
    private ObservableList<LicenciaObra> listaLicencias;
    private LicenciaObra formulario;
    private Long idLicenciaCE = 0L;                  // 0 = nuevo, >0 = editando

    public LicenciaObraController(ILicenciaObraService ls) {
        this.ls = ls;
    }

    @FXML
    public void initialize() {
        cbxTipoObra.getItems().setAll(ls.listarTipoObra());
        new ComboBoxAutoComplete<>(cbxTipoObra);

        validator = Validation.buildDefaultValidatorFactory().getValidator();

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idLicencia", 50.0));
        columns.put("N. Licencia", new ColumnInfo("numeroLicencia", 110.0));
        columns.put("Tipo de Obra", new ColumnInfo("tipoObra.descripcion", 150.0));
        columns.put("Direccion", new ColumnInfo("direccion", 200.0));
        columns.put("Propietario", new ColumnInfo("propietario", 180.0));
        columns.put("F. Inicio", new ColumnInfo("fechaInicio", 100.0));
        columns.put("F. Fin Prevista", new ColumnInfo("fechaFin", 110.0));

        Consumer<LicenciaObra> updateAction = this::editForm;
        Consumer<LicenciaObra> deleteAction = this::eliminar;
        new TableViewHelper<LicenciaObra>()
                .addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);

        txtFiltroDato.textProperty().addListener((obs, o, n) -> filtrar(n));
        listar();
    }

    // ---------------- READ ----------------
    public void listar() {
        listaLicencias = FXCollections.observableArrayList(ls.findAll());
        filtrar(txtFiltroDato.getText());
    }

    private void filtrar(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            tableView.getItems().setAll(listaLicencias);
            return;
        }
        String f = filtro.toLowerCase();
        tableView.getItems().setAll(listaLicencias.stream().filter(l ->
                l.getNumeroLicencia().toLowerCase().contains(f)
                || l.getPropietario().toLowerCase().contains(f)
                || l.getDireccion().toLowerCase().contains(f)
                || l.getTipoObra().getDescripcion().toLowerCase().contains(f)).toList());
    }

    // ---------------- CREATE / UPDATE ----------------
    @FXML
    public void validarFormulario() {
        formulario = new LicenciaObra();
        formulario.setNumeroLicencia(txtNumeroLicencia.getText().trim());
        formulario.setDireccion(txtDireccion.getText().trim());
        formulario.setPropietario(txtPropietario.getText().trim());
        formulario.setFechaInicio(dpFechaInicio.getValue());
        formulario.setFechaFin(dpFechaFin.getValue());
        ComboBoxOption op = cbxTipoObra.getSelectionModel().getSelectedItem();
        formulario.setTipoObra(op == null ? null : TipoObra.valueOf(op.getKey()));

        List<ConstraintViolation<LicenciaObra>> errores = validator.validate(formulario)
                .stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
                .toList();

        if (errores.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(errores);
        }
    }

    private void procesarFormulario() {
        limpiarError();
        try {
            if (idLicenciaCE > 0L) {
                formulario.setIdLicencia(idLicenciaCE);
                ls.update(idLicenciaCE, formulario);
                mostrarToast("Se actualizo correctamente");
            } else {
                ls.save(formulario);
                mostrarToast("Se guardo correctamente");
            }
            clearForm();
            listar();
            mensaje("Operacion exitosa", "green");
        } catch (LicenciaDuplicadaException e) {
            ttc.marcarError(txtNumeroLicencia, e.getMessage());
            mensaje(e.getMessage(), "red");
        } catch (IllegalArgumentException e) {
            ttc.marcarError(dpFechaFin, e.getMessage());
            mensaje(e.getMessage(), "red");
        }
    }

    private void mostrarErroresValidacion(List<ConstraintViolation<LicenciaObra>> errores) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("numeroLicencia", txtNumeroLicencia);
        campos.put("tipoObra", cbxTipoObra);
        campos.put("direccion", txtDireccion);
        campos.put("propietario", txtPropietario);
        campos.put("fechaInicio", dpFechaInicio);
        campos.put("fechaFin", dpFechaFin);

        Control primero = null;
        String primerMensaje = null;
        for (Map.Entry<String, Control> e : campos.entrySet()) {
            for (ConstraintViolation<LicenciaObra> v : errores) {
                if (v.getPropertyPath().toString().equals(e.getKey())) {
                    ttc.marcarError(e.getValue(), v.getMessage());
                    if (primero == null) {
                        primero = e.getValue();
                        primerMensaje = v.getMessage();
                    }
                }
            }
        }
        mensaje(primerMensaje, "red");
        if (primero != null) {
            final Control foco = primero;
            Platform.runLater(foco::requestFocus);
        }
    }

    // ---------------- EDIT ----------------
    public void editForm(LicenciaObra l) {
        txtNumeroLicencia.setText(l.getNumeroLicencia());
        txtNumeroLicencia.setEditable(false);        // la clave de negocio no se cambia
        txtDireccion.setText(l.getDireccion());
        txtPropietario.setText(l.getPropietario());
        dpFechaInicio.setValue(l.getFechaInicio());
        dpFechaFin.setValue(l.getFechaFin());
        cbxTipoObra.getSelectionModel().select(
                cbxTipoObra.getItems().stream()
                        .filter(o -> o.getKey().equals(l.getTipoObra().name()))
                        .findFirst().orElse(null));
        idLicenciaCE = l.getIdLicencia();
        limpiarError();
        mensaje("Editando licencia " + l.getNumeroLicencia(), "blue");
    }

    // ---------------- DELETE ----------------
    private void eliminar(LicenciaObra l) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Eliminar la licencia " + l.getNumeroLicencia() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait()
                .filter(b -> b == ButtonType.YES)
                .ifPresent(b -> {
                    ls.delete(l.getIdLicencia());
                    mostrarToast("Se elimino correctamente");
                    clearForm();
                    listar();
                });
    }

    // ---------------- UTILIDADES ----------------
    @FXML
    public void clearForm() {
        txtNumeroLicencia.clear();
        txtNumeroLicencia.setEditable(true);
        txtDireccion.clear();
        txtPropietario.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
        cbxTipoObra.getSelectionModel().clearSelection();
        idLicenciaCE = 0L;
        limpiarError();
        mensaje("", "black");
    }

    private void limpiarError() {
        List.of(txtNumeroLicencia, cbxTipoObra, txtDireccion, txtPropietario,
                dpFechaInicio, dpFechaFin).forEach(ttc::limpiarCampo);
    }

    private void mensaje(String texto, String color) {
        lbnMsg.setText(texto == null ? "" : texto);
        lbnMsg.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px;");
    }

    private void mostrarToast(String msg) {
        Stage stage = (Stage) miContenedor.getScene().getWindow();
        Toast.showToast(stage, msg, 2000, stage.getWidth() / 1.5, stage.getHeight() / 2);
    }
}
