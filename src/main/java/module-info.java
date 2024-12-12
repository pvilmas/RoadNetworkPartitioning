module bp.roadnetworkpartitioning {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.dataformat.xml;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    exports bp.roadnetworkpartitioning;
    opens bp.roadnetworkpartitioning to javafx.fxml;
}