package org.GTParking.entity.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Userdetails implements Serializable {
    private static final long serialVersionUID = -4092794334590418514L;

    private Integer userid;

    private String username;

    private String permits;

    private String path;

    private Integer ischeckedin;

    private Integer isdriving;

}

