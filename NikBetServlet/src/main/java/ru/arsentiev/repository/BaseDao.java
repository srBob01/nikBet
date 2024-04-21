package ru.arsentiev.repository;

import java.util.List;
import java.util.Optional;

public interface BaseDao<K, E> {
    void insert(E e);

    List<E> selectAll();

    Optional<E> selectById(K id);

    boolean delete(K id);

    boolean update(E e);
}
