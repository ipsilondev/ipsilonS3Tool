package com.ipsilonDev.ipsilonS3ToolLib;

public class ipsilonS3ToolException extends Exception {

	private String msg;
	private Throwable as3Except;
	
	public ipsilonS3ToolException() {
		// TODO Auto-generated constructor stub
	}

	public ipsilonS3ToolException(String arg0) {
		super(arg0);
		msg = arg0;
		System.out.println(msg);
		// TODO Auto-generated constructor stub
	}

	public ipsilonS3ToolException(Throwable arg0) {
		super(arg0);
		as3Except = arg0;
		// TODO Auto-generated constructor stub
	}

	public ipsilonS3ToolException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		msg = arg0;
		as3Except = arg1;
		System.out.println(msg);
		// TODO Auto-generated constructor stub
	}
	
	public String getMessage(){
		return msg;
	}
	
	public Throwable getException(){
		return as3Except;
	}

}
