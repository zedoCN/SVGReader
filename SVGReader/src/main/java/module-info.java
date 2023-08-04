module com.zedo.svgreader {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens com.zedo.svgreader to javafx.fxml;
    exports com.zedo.svgreader;
}