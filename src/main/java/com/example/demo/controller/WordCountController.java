package com.example.demo.controller;

import com.example.demo.service.WordCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//@RestController
public class WordCountController {
    final WordCountService service;

    public WordCountController(WordCountService service) {
        this.service = service;
    }

//    @RequestMapping(method = RequestMethod.POST, path = "/wordcount")
//    public Map<String, Long> count(@RequestParam(required = true) String words) {
//        List<String> wordList = Arrays.asList(words.split("\\|"));
//        return service.getCount(wordList);
//    }
}