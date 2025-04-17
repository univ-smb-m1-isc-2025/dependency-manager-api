package com.info803.dependency_manager_api.domain.git;

@FunctionalInterface
public interface GitFunction<T, R> {
    R apply(T t) throws Exception;
}
