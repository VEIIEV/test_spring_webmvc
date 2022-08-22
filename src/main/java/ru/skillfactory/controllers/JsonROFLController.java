package ru.skillfactory.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.dto.StudentData;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@RestController
@RequestMapping(value = "/json")
public class JsonROFLController {

    @GetMapping("/getmethod")
    public StudentData testMethod() throws JsonProcessingException {
        StudentData studentData = new StudentData();
        studentData.setGrade(23);
        studentData.setLastName("bot");
        studentData.setFirstName("kirill");
        return studentData;
    }


    @PostMapping("/postmethod")
    public String testPostMethod(@RequestBody StudentData studentData) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String studentModel = objectMapper.writeValueAsString(studentData);
   //     final Path desktopPath = Paths.get(System.getenv("USERPROFILE")+"\\OneDrive\\Рабочий стол\\file.json");
        final Path ProjectPath = Paths.get("C:\\Users\\world\\IdeaProjects\\SF_31_2_Spirng_webApp\\src\\main\\java\\stash\\file.json");

        //Desktop
        try {
            Files.write(ProjectPath, Collections.singleton(objectMapper.writeValueAsString(studentData)));
            System.out.println("на рабочий стол пользователя успешно записан file.json");
            System.out.println(Files.readString(ProjectPath));
        }
        catch (AccessDeniedException e)
        {
            Logger logger=LoggerFactory.getLogger("logger");
            logger.warn("путь к файлу записан неправильно ");
            logger.trace(e.toString());
        }
        return studentModel+ "\nты красава ";
    }
}
