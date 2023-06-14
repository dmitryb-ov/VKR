module com.desctopbpmn.desctopbpmn {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.desctopbpmn.desctopbpmn to javafx.fxml;
    exports com.desctopbpmn.desctopbpmn;
}