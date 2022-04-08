package com.mjkrt.rendr.entity.helper;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ColumnDataType.
 *
 * This enum represents the datatypes of the data that a column holds
 */
public enum ColumnDataType {
        @JsonProperty("string") STRING,
        @JsonProperty("decimal") DECIMAL,
        @JsonProperty("date") DATE,
        @JsonProperty("double") DOUBLE,
        @JsonProperty("mock") MOCK
}
