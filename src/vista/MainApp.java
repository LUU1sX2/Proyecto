package vista;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mostrarMenuPrincipal();
    }

  private void mostrarMenuPrincipal() {
    VBox layout = new VBox(20);
    layout.setAlignment(Pos.CENTER);

    Button triangular = new Button("Modo Triangular");
    triangular.setOnAction(e -> {
        TableroVista vista = new TableroVista();
        vista.start(primaryStage); // Modo triangular
    });

    Button cruz = new Button("Modo Cruz");
    cruz.setOnAction(e -> {
      System.out.println("Botón de cruz presionado");
      TableroVista vista = new TableroVista();
      vista.mostrarTableroCruz(primaryStage);
    });

    layout.getChildren().addAll(triangular, cruz);
    Scene scene = new Scene(layout, 300, 200);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Selecciona un modo");
    primaryStage.show();
}
    
    public static void main(String[] args) {
        launch(args);
    }
}
