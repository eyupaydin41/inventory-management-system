package com.inventorymanagementsystem.composite;

import java.util.ArrayList;
import java.util.List;

public class DashboardComposite implements DashboardComponent {

    private final List<DashboardComponent> components = new ArrayList<>();

    public void add(DashboardComponent component) {
        components.add(component);
    }

    @Override
    public void update() {
        for (DashboardComponent component : components) {
            component.update();
        }
    }
}
