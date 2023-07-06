package com.example.restaurant.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchImageReq {
    private String query = "";
    private int display = 1;
    private int start = 1;
    private String sort = "sim";
    private String filter = "all";

    // UriComponentsBuilder의 queryparams()가 매개변수로 MultiValueMap 타입을 받기 때문에 만든 메서드
    public MultiValueMap<String, String> toMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();

        map.add("query", query);
        map.add("display", String.valueOf(display));
        map.add("start", String.valueOf(start));
        map.add("sort", sort);
        map.add("filter", filter);

        return map;
    }
}
