package ru.skillfactory.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Data
public class TakenData {
    @NonNull
    @JsonProperty("data")
    private String data;

    public TakenData() {

    }
}
