package com.shop.app.servicecenter.inquiry.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.shop.app.common.entity.ImageAttachment;
import com.shop.app.servicecenter.inquiry.dto.QuestionCreateDto;
import com.shop.app.servicecenter.inquiry.entity.Answer;
import com.shop.app.servicecenter.inquiry.entity.Question;
import com.shop.app.servicecenter.inquiry.entity.QuestionDetails;

public interface QuestionService {
	
	List<QuestionDetails> findQuestionAll(Map<String, Object> params);

	Question findQuestionById(Question question);
	
	Answer findQuestionAnswersById(Answer answer);

	int insertQuestion(Question question);

	int deleteQuestion(int questionId);

	int updateQuestion(Question question);

	Question findQuestionByMemberId(String memberId);

	Answer findAnswersByContent(Answer answer);

	List<Question> questionSearch(String searchKeyword);

	int findTotalQuestionCount();

	QuestionDetails findImageAttachmentsByQuestionId(int questionId);

	Question findQuestionByAnwerCount(Question question);

	int calculateAnswerCount(int questionId);

	ImageAttachment findAttachmentById(int questionId);

	int createQuestionWithAttachments(QuestionCreateDto _question, List<MultipartFile> upFiles, String saveDirectory) throws IllegalStateException, IOException;
}
