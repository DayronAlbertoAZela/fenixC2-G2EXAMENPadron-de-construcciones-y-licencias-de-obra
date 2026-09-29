package pe.edu.upeu.sysobraconstrucionesylicencias.config;

import pe.edu.upeu.sysobraconstrucionesylicencias.controller.*;
import pe.edu.upeu.sysobraconstrucionesylicencias.repository.*;
import pe.edu.upeu.sysobraconstrucionesylicencias.service.*;
import pe.edu.upeu.sysobraconstrucionesylicencias.service.impl.*;
import java.util.HashMap;
import java.util.Map;

public class AppContext {
    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    private void registrarRepositorios() {
        registrar(LicenciaObraRepository.class, new LicenciaObraRepository());
    }

    private void registrarServicios() {
        registrar(ILicenciaObraService.class, new LicenciaObraServiceImp(getBean(LicenciaObraRepository.class)));
    }

    private void registrarControladores() {
        registrar(MainGuiController.class, new MainGuiController());
        registrar(LicenciaObraController.class,
                new LicenciaObraController(getBean(ILicenciaObraService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Bean no encontrado: " + tipo.getName() +
                                    "\n-> ¿Lo registraste en AppContext?"));
        }
        return (T) bean;
    }
}
