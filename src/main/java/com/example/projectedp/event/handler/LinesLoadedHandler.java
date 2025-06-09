package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.Handles;
import com.example.projectedp.event.LinesLoadedEvent;
import com.example.projectedp.model.Line;
import com.example.projectedp.service.ApiService;
import com.example.projectedp.controller.MainController;
import javafx.application.Platform;

import java.util.List;

@Handles(LinesLoadedEvent.class)
public class LinesLoadedHandler implements EventHandler<LinesLoadedEvent> {

    private final ApiService apiService;
    private final MainController controller;

    public LinesLoadedHandler(ApiService apiService, MainController controller) {
        this.apiService = apiService;
        this.controller = controller;
    }

    @Override
    public void handle(LinesLoadedEvent event) {
        List<Line> lines = event.getLines();
        String busstopId = event.getBusstopId();
        String busstopNr = event.getBusstopNr();

        apiService.fetchDeparturesAsync(busstopId, busstopNr, lines);


        System.out.println("IM HERE");
        // Możesz też wyświetlić listę linii w GUI, gdy dodasz widok (opcjonalnie):
        Platform.runLater(() -> controller.updateLineList(lines));
    }
}
