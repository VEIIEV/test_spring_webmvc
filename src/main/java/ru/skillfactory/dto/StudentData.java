package ru.skillfactory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(exclude = "grade")
public class StudentData {
    @NonNull
    @ApiModelProperty(value = "Имя студента")
    @JsonProperty("firstName")
    private String firstName;

    @NonNull
    @ApiModelProperty(value = "Фамилия студента")
    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("grade")
    @ApiModelProperty(value = "оценка студента")
    private int grade;


}