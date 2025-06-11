package com.example.projectedp.event.handler;

import com.example.projectedp.dao.SearchHistoryDao;
import com.example.projectedp.dao.SearchHistoryDaoImpl;
import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.Handles;
import com.example.projectedp.event.StopSearchRequestedEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.controller.MainController;
import com.example.projectedp.service.ApiService;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;

@Handles(StopSearchRequestedEvent.class)
public class StopSearchRequestedHandler implements EventHandler<StopSearchRequestedEvent>{
    private SearchHistoryDao searchHistoryDao;
    private final MainController controller;

    public StopSearchRequestedHandler(MainController controller, ApiService apiService) {
//        this.databaseService = databaseService;
        this.controller = controller;
        this.searchHistoryDao = SearchHistoryDaoImpl.getInstance();
    }
    @Override
    public void handle(StopSearchRequestedEvent event) {
        String query = event.getQuery().trim().toLowerCase();

        if (!query.isEmpty() && !query.isBlank()) {
            try {
                searchHistoryDao.saveQuery(query);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (query.isEmpty() || query.isBlank()) {
            controller.updateStopList(controller.getAllStops());
            controller.plotStopsOnMap(controller.getAllStops());
        } else {
            List<Stop> filtered = controller.getAllStops().stream()
                    .filter(stop -> stop.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());

            controller.updateStopList(filtered);
            controller.plotStopsOnMap(filtered);
            controller.addRecentSearch(event.getQuery());
        }
    }
}
