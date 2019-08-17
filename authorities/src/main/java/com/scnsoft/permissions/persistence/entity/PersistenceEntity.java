package com.scnsoft.permissions.persistence.entity;

import java.io.Serializable;

public interface PersistenceEntity<K> extends Serializable {
    K getId();
}