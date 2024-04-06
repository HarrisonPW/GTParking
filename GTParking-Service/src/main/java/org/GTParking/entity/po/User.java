package org.GTParking.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Queue;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 7602052318242025807L;

    private String userid;

    private String username;

    private ArrayList<String> permits;

    private ArrayList<LocationTime> path;

//    True is checked in
//    False is checked out
    private boolean isCheckedIn;

//    True is driving
//    False is walking or other
    private boolean isDriving;
}