package com.emaf.service.common.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CommonBuilder
 *
 * @author KhaL
 */
public class DataBuilder<S, T> {

    /**
     * Copy properties from source object to target object
     *
     * @param source
     * @param tClass
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> T to(S source, Class<T> tClass) {
        return Optional.ofNullable(source)
                .map(s -> {
                    T newTClass = null;
                    try {
                        newTClass = tClass.getDeclaredConstructor().newInstance();
                        BeanUtils.copyProperties(source, newTClass);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    return newTClass;
                }).orElse(null);
    }

    /**
     * Copy properties from source list object to target list object
     *
     * @param sources
     * @param tClass
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> toList(List<S> sources, Class<T> tClass) {
        return Optional.ofNullable(sources)
                .orElse(Collections.emptyList())
                .stream()
                .map(source -> to(source, tClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
