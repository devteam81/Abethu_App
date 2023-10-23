package com.abethu.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by codegama on 16/10/17.
 */

public class GenreSeason implements Serializable {

    private int id;
    private String name;
    private boolean isSelected;

    public GenreSeason(){

    }

    public GenreSeason(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name; // What to display in the Spinner list.
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
