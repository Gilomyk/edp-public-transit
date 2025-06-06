package com.example.projectedp.event;

import com.example.projectedp.model.Line;

import java.util.List;

/** Zdarzenie sygnalizujące, że została załadowana lista numerów linii (String) dla przystanku. */
public class LinesLoadedEvent {
    private final List<Line> lines;
    private final String busstopId;
    private final String busstopNr;

    public LinesLoadedEvent(List<Line> lines, String busstopId, String busstopNr) {
        this.lines = lines;
        this.busstopId = busstopId;
        this.busstopNr = busstopNr;
    }

    public List<Line> getLines() {
        return lines;
    }

    public String getBusstopId() {
        return busstopId;
    }

    public String getBusstopNr() {
        return busstopNr;
    }
}

