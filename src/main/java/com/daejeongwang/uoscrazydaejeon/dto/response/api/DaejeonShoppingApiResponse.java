package com.daejeongwang.uoscrazydaejeon.dto.response.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DaejeonShoppingApiResponse {

    private Body body;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Items items;
        private String totalCount;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<DaejeonShoppingItemResponse> item;
    }
}
