module bp.roadnetworkpartitioning {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    opens bp.roadnetworkpartitioning to javafx.fxml;
    exports bp.roadnetworkpartitioning;
}