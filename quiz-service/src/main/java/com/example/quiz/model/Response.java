package com.example.quiz.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor//gives both parameterized and default constructor
public class Response {
      private Integer id;
      private String response;
}
