module com.softcaretech {
    requires javafx.controls;
    requires javafx.fxml; 
    requires java.desktop; 
    requires com.auth0.jwt;
    requires com.google.zxing;
    requires com.google.zxing.javase; 
    opens com.softcaretech to javafx.fxml;
    exports com.softcaretech;
}
