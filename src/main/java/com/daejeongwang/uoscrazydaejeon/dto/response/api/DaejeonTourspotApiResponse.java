package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaejeonTourspotApiResponse {

    private Response response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private List<DaejeonTourspotItemResponse> items;
        private Integer totalCount;
    }
}
