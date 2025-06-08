package com.example.projectedp.service;

import com.example.projectedp.event.*;
import com.example.projectedp.model.*;


import com.google.gson.*;
import java.net.http.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class ApiService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final EventBus eventBus;
    // Wszystkie klucze i resource'y pobieramy z configu
    private final String baseUrl;
    private final String resourceStops;
    private final String resourceLines;
    private final String resourceDepartures;
    private final String apiKey;

    public ApiService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.baseUrl = com.example.projectedp.config.AppConfig.get("api.baseUrl");
        this.resourceStops = com.example.projectedp.config.AppConfig.get("resource.stops");
        this.resourceLines = com.example.projectedp.config.AppConfig.get("resource.lines");
        this.resourceDepartures = com.example.projectedp.config.AppConfig.get("resource.departures");
        this.apiKey = com.example.projectedp.config.AppConfig.get("api.key");
    }

    /** Asynchronicznie szukaj przystanków po nazwie (lub fragmencie) */
    public void fetchStopsAsync() {
        // URL = baseUrl + "/" + resourceStops + "&apikey=" + apiKey
        String uri = String.format("%s/%s&apikey=%s", baseUrl, resourceStops, apiKey);
        System.out.println("FetchStopsURL: " + uri);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        System.out.println("Request: " + request);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

//        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                .thenApply(HttpResponse::body)
//                .thenAccept(System.out::println);

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<Stop> stops = parseStopsJson(response.body());
                            eventBus.post(new StopsLoadedEvent(stops));
                        } catch (JsonSyntaxException ex) {
                            eventBus.post(new ApiErrorEvent("Błąd parsowania JSON: " + ex.getMessage()));
                        }
                    } else {
                        eventBus.post(new ApiErrorEvent("Błąd HTTP: " + response.statusCode()));
                    }
                })
                .exceptionally(ex -> {
                    eventBus.post(new ApiErrorEvent("Wyjątek podczas fetchStops: " + ex.getMessage()));
                    return null;
                });
    }

    public void fetchLinesAsync(String busstopId, String busstopNr) {
        // URL = baseUrl + "/" + resourceLines + "&busstopId=" + busstopId + "&busstopNr=" + busstopNr + "&apikey=" + apiKey
        String uri = String.format("%s/%s&busstopId=%s&busstopNr=%s&apikey=%s",
                baseUrl, resourceLines, busstopId, busstopNr, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        System.out.println("Request: " + request);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<Line> lines = parseLinesJson(response.body());
                            System.out.println("We got a line");
                            eventBus.post(new LinesLoadedEvent(lines, busstopId, busstopNr));
                        } catch (JsonSyntaxException ex) {
                            eventBus.post(new ApiErrorEvent("Błąd parsowania JSON linii: " + ex.getMessage()));
                        }
                    } else {
                        eventBus.post(new ApiErrorEvent("Błąd HTTP przy fetchLines: " + response.statusCode()));
                    }
                })
                .exceptionally(ex -> {
                    eventBus.post(new ApiErrorEvent("Wyjątek podczas fetchLines: " + ex.getMessage()));
                    return null;
                });
    }

    /** Asynchronicznie pobierz odjazdy dla danego przystanku (stopId) */
    public void fetchDeparturesAsync(String busstopId, String busstopNr, String line) {
        String uri = String.format("%s/%s&busstopId=%s&busstopNr=%s&line=%s&apikey=%s",
                baseUrl, resourceDepartures, busstopId, busstopNr, line, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();

        System.out.println("Request: " + request);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<Departure> deps = parseDeparturesJson(response.body());
                            eventBus.post(new DeparturesLoadedEvent(deps));
                        } catch (JsonSyntaxException ex) {
                            eventBus.post(new ApiErrorEvent("Błąd parsowania JSON odjazdów: " + ex.getMessage()));
                        }
                    } else {
                        eventBus.post(new ApiErrorEvent("Błąd HTTP przy fetchDepartures: " + response.statusCode()));
                    }
                })
                .exceptionally(ex -> {
                    eventBus.post(new ApiErrorEvent("Wyjątek podczas fetchDepartures: " + ex.getMessage()));
                    return null;
                });
    }

    /** Parsuje JSON z dbstore_get (lista przystanków). */
    private List<Stop> parseStopsJson(String json) {
        JsonArray result = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonArray("result");

        return result.asList().stream()
                .map(el -> {
                    JsonObject obj = el.getAsJsonObject();
                    // "values" to tablica par { key:"...", value:"..." }
                    JsonArray vals = obj.getAsJsonArray("values");

                    // Wyciągamy z niej odpowiednie pola:
                    String zespol   = vals.get(0).getAsJsonObject().get("value").getAsString();   // np. "1001"
                    String nazwa    = vals.get(2).getAsJsonObject().get("value").getAsString();   // np. "Kijowska"
                    String szerGeo  = vals.get(4).getAsJsonObject().get("value").getAsString();   // np. "52.248455"
                    String dlugGeo  = vals.get(5).getAsJsonObject().get("value").getAsString();   // np. "21.044827"
                    String slupek   = vals.get(1).getAsJsonObject().get("value").getAsString();   // np. "01"

                    double lat = Double.parseDouble(szerGeo);
                    double lon = Double.parseDouble(dlugGeo);

                    return new Stop(zespol, nazwa, lat, lon, slupek);
                })
                .collect(Collectors.toList());
    }

    /** Parsuje JSON z dbtimetable_get (lista numerów linii). */
    private List<Line> parseLinesJson(String json) {
        JsonArray result = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonArray("result");

        return result.asList().stream()
                .map(el -> {
                    JsonObject obj = el.getAsJsonObject();
                    JsonArray values = obj.getAsJsonArray("values");

                    // Szukamy w values pary, gdzie key == "linia"
                    return values.asList().stream()
                            .map(JsonElement::getAsJsonObject)
                            .filter(valueObj -> "linia".equals(valueObj.get("key").getAsString()))
                            .findFirst()
                            .map(valueObj -> new Line(valueObj.get("value").getAsString()))
                            .orElse(null); // albo możesz użyć Optional::orElseThrow jeśli ma być obowiązkowe
                })
                .collect(Collectors.toList());
    }



    /** Parsuje JSON z dbtimetable_get (lista odjazdów dla konkretnej linii). */
    private List<Departure> parseDeparturesJson(String json) {
        JsonArray result = JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonArray("result");

        return result.asList().stream()
                .map(el -> {
                    JsonArray valuesArray = el.getAsJsonArray();  // <- to jest tablica obiektów, nie obiekt z "values"
                    Map<String, String> valueMap = valuesArray.asList().stream()
                            .map(JsonElement::getAsJsonObject)
                            .filter(obj -> obj.has("key") && obj.has("value"))
                            .collect(Collectors.toMap(
                                    obj -> obj.get("key").getAsString(),
                                    obj -> obj.get("value").isJsonNull() ? "" : obj.get("value").getAsString()
                            ));

                    String line = valueMap.getOrDefault("linia", "");
                    String kierunek = valueMap.getOrDefault("kierunek", "");
                    String czasStr = valueMap.getOrDefault("czas", "00:00:00");

                    LocalDateTime time;
                    try {
                        time = LocalDateTime.parse(LocalDateTime.now().toLocalDate() + "T" + czasStr);
                    } catch (Exception e) {
                        time = LocalDateTime.now(); // fallback w razie błędu parsowania
                    }

                    String id = line + "_" + czasStr.replace(":", "");

                    return new Departure(id, line, kierunek, time);
                })
                .collect(Collectors.toList());
    }

}
