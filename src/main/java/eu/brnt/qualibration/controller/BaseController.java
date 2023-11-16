package eu.brnt.qualibration.controller;

import eu.brnt.qualibration.view.ViewFactory;

public abstract class BaseController {

    protected final ViewFactory viewFactory;
    private final String fxmlName;

    protected BaseController(ViewFactory viewFactory, String fxmlName) {
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }

    public String getFxmlName() {
        return fxmlName;
    }
}
