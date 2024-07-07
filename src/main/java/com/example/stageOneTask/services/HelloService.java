package com.example.stageOneTask.services;

import com.example.stageOneTask.response.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public interface HelloService {
    GenericResponse getGreeting(String visitorName, HttpServletRequest request) throws IOException;
}
