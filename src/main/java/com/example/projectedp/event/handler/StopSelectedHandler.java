package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopSelectedEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.service.ApiService;
import com.example.projectedp.controller.MainController;

public class StopSelectedHandler implements EventHandler<StopSelectedEvent> {

    private final ApiService apiService;
    private final MainController controller;

    public StopSelectedHandler(ApiService apiService, MainController controller) {
        this.apiService = apiService;
        this.controller = controller;
    }

    @Override
    public void handle(StopSelectedEvent event) {
        Stop selected = event.getSelectedStop();

        // 1) Zaznacz w GUI wybrany przystanek (highlight w stopList, jeżeli chcesz)
//        controller.highlightStopInList(selected);

        // 2) Pobierz odjazdy z API asynchronicznie
//        apiService.fetchDeparturesAsync(selected.getId());
    }
}
