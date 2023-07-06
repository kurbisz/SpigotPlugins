package com.lessnop.customevents.ox;

public class Question {

	private String question;
	private boolean answer;

	public Question(String question, boolean answer) {
		this.question = question;
		this.answer = answer;
	}

	public String getQuestion() {
		return question;
	}

	public boolean isAnswer() {
		return answer;
	}
}
