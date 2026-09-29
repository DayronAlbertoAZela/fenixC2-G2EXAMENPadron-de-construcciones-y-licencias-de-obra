package pe.edu.upeu.sysobraconstrucionesylicencias.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysobraconstrucionesylicencias.dto.ComboBoxOption;
import pe.edu.upeu.sysobraconstrucionesylicencias.enums.TipoObra;
import pe.edu.upeu.sysobraconstrucionesylicencias.exception.LicenciaDuplicadaException;
import pe.edu.upeu.sysobraconstrucionesylicencias.model.LicenciaObra;
import pe.edu.upeu.sysobraconstrucionesylicencias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysobraconstrucionesylicencias.repository.LicenciaObraRepository;
import pe.edu.upeu.sysobraconstrucionesylicencias.service.ILicenciaObraService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LicenciaObraServiceImp extends CrudGenericoServiceImp<LicenciaObra, Long> implements ILicenciaObraService {
    private final LicenciaObraRepository licenciaObraRepository;

    @Override
    protected ICrudGenericoRepository<LicenciaObra, Long> getRepo() {
        return licenciaObraRepository;
    }

    @Override
    public List<ComboBoxOption> listarTipoObra() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (TipoObra to : TipoObra.values()) {
            ComboBoxOption cb = new ComboBoxOption();
            cb.setKey(to.name());
            cb.setValue(to.getDescripcion());
            listar.add(cb);
        }
        return listar;
    }

    @Override
    public LicenciaObra save(LicenciaObra licencia) {
        validarReglas(licencia);
        return super.save(licencia);
    }

    @Override
    public LicenciaObra update(Long id, LicenciaObra licencia) {
        validarReglas(licencia);
        return super.update(id, licencia);
    }

    private void validarReglas(LicenciaObra l) {
        if (licenciaObraRepository.existsByNumero(l.getNumeroLicencia().trim(), l.getIdLicencia())) {
            throw new LicenciaDuplicadaException("Ya existe una licencia con el numero: " + l.getNumeroLicencia());
        }
        if (l.getFechaFin().isBefore(l.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de terminacion no puede ser anterior a la fecha de inicio");
        }
    }
}
