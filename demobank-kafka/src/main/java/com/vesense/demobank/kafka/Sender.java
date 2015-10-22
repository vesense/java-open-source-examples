package com.vesense.demobank.kafka;

public interface Sender {

	public void send(String msg);
	
	public void destroy();
	
}
