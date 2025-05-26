package com.inventorymanagementsystem.composite;

import javafx.scene.control.Label;
import java.util.function.Supplier;

public class DashboardLeaf implements DashboardComponent {

    private final Label label;
    private final Supplier<String> dataSupplier;

    public DashboardLeaf(Label label, Supplier<String> dataSupplier) {
        this.label = label;
        this.dataSupplier = dataSupplier;
    }

    @Override
    public void update() {
        label.setText(dataSupplier.get());
    }
}
