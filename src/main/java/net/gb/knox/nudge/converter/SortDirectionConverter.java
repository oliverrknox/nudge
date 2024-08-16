package net.gb.knox.nudge.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;

public class SortDirectionConverter implements Converter<String, Sort.Direction> {

    @Override
    public Sort.Direction convert(String direction) {
        return Sort.Direction.valueOf(direction.toUpperCase());
    }
}
