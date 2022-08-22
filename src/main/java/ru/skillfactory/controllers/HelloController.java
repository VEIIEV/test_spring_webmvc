package ru.skillfactory.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.dto.StudentData;

@RestController
// @Controller
@RequestMapping("/hello")
public class HelloController {

 //   @RequestMapping(name = "/student" ,method = RequestMethod.GET)
    @GetMapping("/student")
    public String helloStudent(){
        return "hello, student";
    }

    @RequestMapping(value = "/greetings/{name}")
    public String greetingsByName(@PathVariable String name){
        return "hello, " + name;
    }

    @RequestMapping(value = "/student/submit")
    public String giveMeFeedbackAboutGrage(@RequestBody StudentData studentData){
        return "You grade  is " + studentData.getGrade();
    }

}
