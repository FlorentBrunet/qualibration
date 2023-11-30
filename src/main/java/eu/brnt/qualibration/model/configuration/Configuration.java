package eu.brnt.qualibration.model.configuration;

import lombok.Data;

@Data
public class Configuration {

    private ObservationPointConfig observationPointConfig = new ObservationPointConfig();
    private UndistGridConfig undistGridConfig = new UndistGridConfig();
}
