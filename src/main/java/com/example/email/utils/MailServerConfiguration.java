package com.example.email.utils;

//import com.sun.tools.javac.util.StringUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class MailServerConfiguration {
    private String host;
    private Integer port;
    private String username;
    private String password;

    private MailServerConfiguration(){
    }

    public MailServerConfiguration(String host, Integer port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    public void valid_or_not(){
        if(StringUtils.isBlank(getHost())||getPort()==0||StringUtils.isBlank((getUsername()))||StringUtils.isBlank(getPassword()))
            throw new RuntimeException("请把信息填写完整");
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
