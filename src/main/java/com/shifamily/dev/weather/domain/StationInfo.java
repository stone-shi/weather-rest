package com.shifamily.dev.weather.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationInfo {

    private String model;
    private boolean pingOK;
}
