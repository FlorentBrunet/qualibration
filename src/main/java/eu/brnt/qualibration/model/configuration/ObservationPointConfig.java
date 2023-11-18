package eu.brnt.qualibration.model.configuration;

import lombok.Data;

@Data
public class ObservationPointConfig {

    private ObservationPointDisplayType observationPointDisplayType = ObservationPointDisplayType.RAINBOW;

    private int fontSize = 10;
    private boolean showPointIndexes = true;

    private ObservationPointRainbowConfig observationPointRainbowConfig = new ObservationPointRainbowConfig();
}
