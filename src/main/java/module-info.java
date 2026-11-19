module final_proyect.power_motion {
    requires javafx.controls;
    requires javafx.fxml;

    opens final_proyect.power_motion to javafx.fxml;
    opens vista to javafx.fxml;
    opens controlador to javafx.fxml;
    opens modelo to javafx.fxml, javafx.base;

    exports final_proyect.power_motion;
    exports vista;
    exports controlador;
    exports modelo;
}