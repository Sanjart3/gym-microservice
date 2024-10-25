package org.example.utils.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String entityName;
    private final String identifier;

    public NotFoundException(String entityName, String identifier) {
        super(entityName + " with username " + identifier + " not found");
        this.entityName = entityName;
        this.identifier = identifier;
    }

}
