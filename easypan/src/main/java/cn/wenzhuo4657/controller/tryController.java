package cn.wenzhuo4657.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class tryController {

    @RequestMapping("/rs")
    public  String Try(){
        return "try";
    }
}
