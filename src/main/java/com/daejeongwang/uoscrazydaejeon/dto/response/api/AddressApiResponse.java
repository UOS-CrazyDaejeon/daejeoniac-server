package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressApiResponse {
    private List<Document> documents;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {

        @JsonProperty("x")
        private String longitude;

        @JsonProperty("y")
        private String latitude;
    }
}
