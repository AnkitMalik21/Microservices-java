package com.example.quiz.service;

import com.example.quiz.dao.QuizDao;
import com.example.quiz.feign.QuizInterface;
import com.example.quiz.model.QuestionWrapper;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService{
    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuizInterface quizInterface;

    public ResponseEntity<String> createQuiz(String category,Integer numQ,String title){
        List<Integer> questions = quizInterface.getQuestionsForQuiz(category,numQ).getBody();
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }



    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Quiz quiz = quizDao.findById(id).get();
        List<Integer> questionIds = quiz.getQuestionIds();

        List<QuestionWrapper> questions = quizInterface.getQuestionFromId(questionIds).getBody();

        return new ResponseEntity<>(questions,HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        //1. get quiz
        Quiz quiz = quizDao.findById(id)
                .orElseThrow(()->new RuntimeException("Quiz not Found "));
        //2. get quiz questions Ids
        Set<Integer> quizQuestionIds = new HashSet<>(quiz.getQuestionIds());

        //3 Filter responses ->only quiz question allowed
        List<Response> validResponses = new ArrayList<>();

        for(Response response : responses){
            if(quizQuestionIds.contains(response.getId()))
                 validResponses.add(response);
        }
        ResponseEntity<Integer> score = quizInterface.getScore(validResponses);
        return score;
    }

}
