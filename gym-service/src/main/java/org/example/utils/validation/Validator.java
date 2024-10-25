package org.example.utils.validation;

public interface Validator<T> {
    Boolean isValidForCreate(T t);
    Boolean isValidForUpdate(T t);
}
