module bp.roadnetworkpartitioning {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens bp.roadnetworkpartitioning to javafx.fxml;
    exports bp.roadnetworkpartitioning;
}