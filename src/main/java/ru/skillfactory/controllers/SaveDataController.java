package ru.skillfactory.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.dto.TakenData;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/savedatacontroller")
public class SaveDataController {


    @Autowired
    TakenData takenData;

    @PostMapping("/savedata")
    public String  saveDataMethod( @RequestBody TakenData takenData){


        this.takenData.setData(takenData.getData());

        return "Data was saved on bean";
    }

    @Bean
    @GetMapping("/showdata")
    public TakenData showDataMethod(){

        return takenData;
    }



}
