module final_proyect.power_motion {
    requires javafx.controls;
    requires javafx.fxml;

    opens final_proyect.power_motion to javafx.fxml;
    exports final_proyect.power_motion;
}
