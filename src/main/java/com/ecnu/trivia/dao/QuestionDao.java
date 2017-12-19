package com.ecnu.trivia.dao;


import com.ecnu.trivia.model.Question;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 86761 on 2017/12/15.
 */
public interface QuestionDao {
    List<Question> selectAll();
    List<Question> selectByDomain(@Param("domain") String domain);
}
