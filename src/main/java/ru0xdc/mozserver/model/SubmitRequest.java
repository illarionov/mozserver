package ru0xdc.mozserver.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by alexey on 05.02.14.
 */
public class SubmitRequest {

    public List<SubmitItem> items;

    @Override
    public String toString() {
        if (items == null) return "null";
        if (items.isEmpty()) return "empty";

        return Arrays.toString(items.toArray(new SubmitItem[items.size()]));
    }
}
