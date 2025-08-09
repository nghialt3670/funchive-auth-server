package com.funchive.authserver.common.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class ResponsePage<D> {
    private List<D> items;
    private int page;
    private int size;
    private long total;

    public static <D> ResponsePage<D> of(Page<D> page) {
        return ResponsePage.<D>builder()
                .items(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .total(page.getTotalElements())
                .build();
    }
}
