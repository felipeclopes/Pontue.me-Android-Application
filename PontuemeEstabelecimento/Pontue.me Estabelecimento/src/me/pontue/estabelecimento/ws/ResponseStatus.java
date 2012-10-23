package me.pontue.estabelecimento.ws;

import java.util.ArrayList;
import java.util.List;

public class ResponseStatus {

	private List<Exception> exceptions;
	private List<String> messages;
	private StatusEnum status;

	public void addException(Exception e) {
		if (exceptions == null) {
			exceptions = new ArrayList<Exception>();
		}
		exceptions.add(e);
	}

	public void addMessage(String mgs) {
		if (messages == null) {
			messages = new ArrayList<String>();
		}
		messages.add(mgs);
	}

	public List<Exception> getExceptions() {
		return exceptions;
	}

	public void setExceptions(List<Exception> exceptions) {
		this.exceptions = exceptions;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessage(List<String> message) {
		this.messages = message;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public enum StatusEnum {
		Ok, Warning, Error;
	}

}
