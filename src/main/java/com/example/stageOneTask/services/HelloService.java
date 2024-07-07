package com.example.stageOneTask.services;

import com.example.stageOneTask.response.GenericResponse;

import java.io.IOException;

public interface HelloService {
    GenericResponse getGreeting(String visitorName) throws IOException;

}
