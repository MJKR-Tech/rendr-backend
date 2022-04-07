package com.mjkrt.rendr.entity.helper;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ColumnDataType {
        @JsonProperty("string") STRING,
        @JsonProperty("decimal") DECIMAL,
        @JsonProperty("date") DATE,
        @JsonProperty("double") DOUBLE,
        @JsonProperty("mock") MOCK
}
