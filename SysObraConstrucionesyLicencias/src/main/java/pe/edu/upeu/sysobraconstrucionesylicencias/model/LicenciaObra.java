package pe.edu.upeu.sysobraconstrucionesylicencias.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysobraconstrucionesylicencias.enums.TipoObra;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenciaObra {
    private Long idLicencia;
    @NotBlank(message = "El numero de licencia es obligatorio")
    private String numeroLicencia;
    @NotNull(message = "El tipo de obra es obligatorio")
    private TipoObra tipoObra;
    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;
    @NotBlank(message = "El nombre del propietario es obligatorio")
    private String propietario;
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;
    @NotNull(message = "La fecha de terminacion prevista es obligatoria")
    private LocalDate fechaFin;
}
