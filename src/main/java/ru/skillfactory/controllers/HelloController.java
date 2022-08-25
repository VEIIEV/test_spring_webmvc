package ru.skillfactory.controllers;


import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.dto.StudentData;

@RestController
// @Controller
@EnableAutoConfiguration
@RequestMapping("/hello")
public class HelloController {

 //   @RequestMapping(name = "/student" ,method = RequestMethod.GET)
    @GetMapping("/student")
    @ApiOperation(value = "приветствие студента")
    public String helloStudent(){
        return "hello, student";
    }

    @ApiOperation(value = "приветствие студента по имени")
    @RequestMapping(value = "/greetings/{name}")
    public String greetingsByName(@PathVariable String name){
        return "hello, " + name;
    }


    @ApiOperation(value = "вывод полученной оценки студента")
    @RequestMapping(value = "/student/submit")
    public String giveMeFeedbackAboutGrage(@RequestBody StudentData studentData){
        return "You grade  is " + studentData.getGrade();


    }

}
