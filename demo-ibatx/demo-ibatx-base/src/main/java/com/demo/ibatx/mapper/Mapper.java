package com.demo.ibatx.mapper;

public interface Mapper<T> extends SelectMapper<T>, InsertMapper<T>, UpdateMapper<T>, DeleteMapper<T> {
}
