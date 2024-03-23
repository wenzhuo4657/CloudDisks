package cn.wenzhuo4657.domain.enums;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class appconfig {

    @Value("${spring.mail.username}")
    private  String username;

    @Value("${admin.emails}")
    private  String admin;

    @Value("${project.folder}")
    private  String projectFolder;



    public String getProjectFolder() {
        return projectFolder;
    }

    public String getAdmin() {
        return admin;
    }

    public String getUsername() {
        return username;
    }

}
