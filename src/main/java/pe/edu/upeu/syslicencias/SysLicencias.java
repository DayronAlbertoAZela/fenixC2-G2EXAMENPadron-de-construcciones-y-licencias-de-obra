package pe.edu.upeu.syslicencias;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.syslicencias.config.AppContext;

public class SysLicencias extends Application {
    private Parent parent;

    @Override
    public void init() throws Exception {
        AppContext context = AppContext.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/maingui.fxml"));
        loader.setControllerFactory(context::getBean);
        parent = loader.load();
    }

    @Override
    public void start(Stage stage) {
        Rectangle2D b = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(parent, b.getWidth(), b.getHeight() - 100);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Padron de Licencias de Obra");
        stage.show();
    }
}
