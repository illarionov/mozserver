package ru0xdc.mozserver.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexey on 05.02.14.
 */
public class SubmitResponse {

    public Map<String, String> errors;

    public SubmitResponse() {
        errors = new HashMap<String, String>(0);
    }

    public SubmitResponse(String error) {
        errors = new HashMap<String, String>(1);
        errors.put("msg", error);
    }

}
