package org.kurento.tutorial.groupcall.permissions.dto;

import java.io.Serializable;

public interface EntityDTO<T> extends Serializable {
    T getId();
}