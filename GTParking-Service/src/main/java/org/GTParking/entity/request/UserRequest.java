package org.GTParking.entity.request;

import lombok.Data;
import org.GTParking.bean.PageRequest;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class UserRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -6811476043310765159L;

    private Integer userid;

    private String username;

    private ArrayList<String> permits;

    private boolean isCheckedIn;

    private boolean isDriving;
}
