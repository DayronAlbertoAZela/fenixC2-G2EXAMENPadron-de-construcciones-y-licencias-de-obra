package pe.edu.upeu.sysobraconstrucionesylicencias.service;

import pe.edu.upeu.sysobraconstrucionesylicencias.dto.ComboBoxOption;
import pe.edu.upeu.sysobraconstrucionesylicencias.model.LicenciaObra;

import java.util.List;

public interface ILicenciaObraService extends ICrudGenericoService<LicenciaObra, Long>{
    List<ComboBoxOption> listarTipoObra();
}
