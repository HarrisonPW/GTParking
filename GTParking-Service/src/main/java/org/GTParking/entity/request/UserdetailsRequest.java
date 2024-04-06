package org.GTParking.entity.request;

import lombok.Data;
import org.GTParking.bean.PageRequest;

import java.io.Serializable;

@Data
public class UserdetailsRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = 4949982929442469201L;

    private Integer userid;

    private String username;

    private String permits;

    private String path;

    private Integer ischeckedin;

    private Integer isdriving;

}
