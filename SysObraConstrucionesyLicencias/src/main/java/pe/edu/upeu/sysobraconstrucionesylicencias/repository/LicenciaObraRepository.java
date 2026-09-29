package pe.edu.upeu.sysobraconstrucionesylicencias.repository;

import pe.edu.upeu.sysobraconstrucionesylicencias.model.LicenciaObra;

import java.util.Objects;

public class LicenciaObraRepository extends AbstractJpaRepository<LicenciaObra, Long>{
    private long sequence=1;
    @Override
    protected Long getId(LicenciaObra entity) {
        return entity.getIdLicencia();
    }

    @Override
    protected void setId(LicenciaObra entity, Long id) {
        entity.setIdLicencia(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    public boolean existsByNumero(String numero, Long idExcluir) {
        return data.stream().anyMatch(l ->
                l.getNumeroLicencia().equalsIgnoreCase(numero)
                        && !Objects.equals(l.getIdLicencia(), idExcluir));
    }
}
