package com.pro.service;

import com.pro.dao.QuestionDao;
import com.pro.model.Question;
import com.pro.model.QuestionWrapper;
import com.pro.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;

    public ResponseEntity<List<Question>> getAllQuestion() {
        try {
            List<Question> questionsList = questionDao.findAll();
            return new ResponseEntity<>(questionsList,HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionByCategory(String category) {
        try {
            List<Question> questionList = questionDao.findByCategory(category);
            return new ResponseEntity<>(questionList,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        questionDao.save(question);
        return new ResponseEntity<>("success",HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteQuestion(Integer id) {
        Question question = questionDao.findById(id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));

        questionDao.deleteById(id);
        return new ResponseEntity<>("Success",HttpStatus.OK);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {
        List<Integer> questions = questionDao.findRandomQuestionByCategory(categoryName,numQuestions);
        return new ResponseEntity<>(questions,HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
         List<QuestionWrapper> wrappers = new ArrayList<>();
         List<Question> questions = new ArrayList<>();
         for(Integer id : questionIds){
             questions.add(questionDao.findById(id).get());
         }

         for(Question question : questions){
             QuestionWrapper wrapper = new QuestionWrapper();
             wrapper.setId(question.getId());
             wrapper.setQuestionTitle(question.getQuestionTitle());
             wrapper.setOption1(question.getOption1());
             wrapper.setOption2(question.getOption2());
             wrapper.setOption3(question.getOption3());
             wrapper.setOption4(question.getOption4());

             wrappers.add(wrapper);
         }
         return new ResponseEntity<>(wrappers,HttpStatus.OK);
    }


    public ResponseEntity<Integer> getScore(List<Response> responses) {
        int right = 0;

        for(Response response : responses){
            // 1. Fetch the question from DB
            Question question = questionDao.findById(response.getId()).get();

            // 2. FIXED: Compare DB answer (safe) with User answer (unsafe)
            if(question.getRightAnswer().equals(response.getResponse()))
                right++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
