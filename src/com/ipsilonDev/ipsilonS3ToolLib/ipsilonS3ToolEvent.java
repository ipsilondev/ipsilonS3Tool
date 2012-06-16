package com.ipsilonDev.ipsilonS3ToolLib;

import java.util.EventObject;

public class ipsilonS3ToolEvent extends EventObject {
		public final static String TYPE_PROGRESS = "progress";
		public final static String TYPE_UPLOADING = "uploading";
		public final static String TYPE_DELETING = "deleting";
		public final static String TYPE_REMOTEMKDIR = "rmkdir";
		public final static String TYPE_LISTING = "listing";
		public final static String TYPE_LISTINGBUCKETS = "listingbuckets";
		public final static String TYPE_ERROR = "error";
		public final static String TYPE_DOWNLOADING = "downloading";
		public String filename;
		public long progress;
		public long totalsize;
		public String typeEvent;
    	public ipsilonS3ToolEvent(String type) {
		    super(type);
		    typeEvent = type;
		  }
    	public ipsilonS3ToolEvent(String type,String file) {
		    super(type);
		    typeEvent = type;
		    filename =  file;
		}
    	public ipsilonS3ToolEvent(String type,String file, long prog) {
		    super(type);
		    typeEvent = type;
		    filename = file;
		    progress = prog;		    
    	}
    	public ipsilonS3ToolEvent(String type,String file, long prog, long total) {
		    super(type);
		    typeEvent = type;
		    filename = file;
		    progress = prog;		
		    totalsize = total;
    	}

}
