package ru.arsentiev.mapper;

import java.util.function.Function;

public interface MapperWithHash<S, D> {
    D map(S obj, Function<String, String> hashFunction);
}
