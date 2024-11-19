package com.projectcondor.condor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import com.projectcondor.condor.service.OpenAIService;

@SpringBootApplication
public class OpenAIConnectionTest {

    @Autowired
    private OpenAIService openAIService;

    public static void main(String[] args) {
        SpringApplication.run(OpenAIConnectionTest.class, args);
    }

   
}