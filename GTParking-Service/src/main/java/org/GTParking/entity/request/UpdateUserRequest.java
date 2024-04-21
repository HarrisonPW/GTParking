package org.GTParking.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserRequest implements Serializable {
    private static final long serialVersionUID = -8728839278148662813L;
    private String username;
}
