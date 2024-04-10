package ru.arsentiev.mapper;

public interface Mapper<S, D> {
    D map(S obj);
}
