package ru0xdc.mozserver.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

@Immutable
public class SubmitWifi {

    public final String key;

    public final int channel;

    public final int frequency;

    public final int signal;

    @JsonCreator
    public SubmitWifi(Map<String, Object> props) {
        key = (String)props.get("key");
        channel = props.containsKey("channel") ? (Integer)props.get("channel") :  -1;
        frequency = props.containsKey("frequency") ? (Integer)props.get("frequency") :  -1;
        signal =  props.containsKey("signal") ? (Integer)props.get("signal") :  Integer.MIN_VALUE;
    }


}
