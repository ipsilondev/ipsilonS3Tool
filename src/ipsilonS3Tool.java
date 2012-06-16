import java.util.Scanner;
import java.util.prefs.Preferences;
import java.util.ArrayList;
import com.ipsilonDev.ipsilonS3ToolEngine;
import com.ipsilonDev.ipsilonS3ToolEngineListener;
import com.ipsilonDev.ipsilonS3ToolEvent;
import com.ipsilonDev.ipsilonS3ToolException;

public class ipsilonS3Tool {


    final static String ACCESS_PROP = "accessKey";
    final static String SECRET_PROP = "secretKey";
	public enum supportedParams{
		bucket,prefix,files,destination;
    };

	
    public static void main(String[] args) throws Exception {
    	Preferences prefs = Preferences.userRoot().node("com/ipsilonDev/ipsilonS3Tool");
    	String accessKey = prefs.get(ACCESS_PROP,"");
    	String secretKey = prefs.get(SECRET_PROP,"");
    	if((accessKey=="" || secretKey=="") || args[0]=="configure"){
            System.out.println("We can't find the AWS credentials in our records, please, insert them now");
        	String[] returnCred = configureAWSKeys(prefs);
        	accessKey=returnCred[0];
        	secretKey=returnCred[1];
        }
    	if(args[0]=="help"){
    		switch(ipsilonS3ToolEngine.supportedMethods.valueOf(args[1])){
    		case del:
            System.out.println("");        	
    		break;
        	}	
    	}else{
        ipsilonS3ToolEngine startObj = new ipsilonS3ToolEngine();
    	startObj.setCredentials(accessKey,secretKey);
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_REMOTEMKDIR,new ipsilonS3ToolEngineListener(){
     		 public void process(ipsilonS3ToolEvent evt) {
     	        System.out.println("INFO: Creating remote directory "+ evt.filename);
     	      }
      	});
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_DELETING,new ipsilonS3ToolEngineListener(){
      		 public void process(ipsilonS3ToolEvent evt) {
      	        System.out.println("INFO: Deleting "+ evt.filename);
      	      }
       	});
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_UPLOADING,new ipsilonS3ToolEngineListener(){
   		 public void process(ipsilonS3ToolEvent evt) {
   	        System.out.println("INFO: Starting uploading of "+ evt.filename);
   	      }
    	});
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_DOWNLOADING,new ipsilonS3ToolEngineListener(){
      		 public void process(ipsilonS3ToolEvent evt) {
      	        System.out.println("INFO: Starting downloading of "+ evt.filename);
      	      }
       	});
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_PROGRESS,new ipsilonS3ToolEngineListener(){
    		 public void process(ipsilonS3ToolEvent evt) {
    	        System.out.println("INFO: "+ evt.filename + " Progress: "+(evt.progress/1024)+ " Kb of "+(evt.totalsize/1024)+" KB");
    	      }
    	});
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_LISTING,new ipsilonS3ToolEngineListener(){
      		 public void process(ipsilonS3ToolEvent evt) {
      	        System.out.println(evt.filename+" size: "+(evt.progress/1024)+ " Kb");
      	      }
       	});
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_LISTINGBUCKETS,new ipsilonS3ToolEngineListener(){
     		 public void process(ipsilonS3ToolEvent evt) {
     	        System.out.println(evt.filename);
     	      }
      	});
    	startObj.addEventListener(ipsilonS3ToolEvent.TYPE_ERROR,new ipsilonS3ToolEngineListener(){
    		 public void process(ipsilonS3ToolEvent evt) {
    	        System.out.println("Error ocurred with the file "+evt.filename+" Operation type: "+evt.typeEvent);
    	      }
     	});

    	
    	String commandExec = args[0];
    	//process arguments passed
    	int i = 1;
    	String bucket = "";
    	String destination = "";
    	String prefix = "";
    	ArrayList<String> files = new ArrayList<String>();
    	for(;i<args.length;i+=2){
    		if(args[i].equals("--bucket")){
        		bucket = args[i+1];    			
    		}else if(args[i].equals("--prefix")){
        		prefix = args[i+1];    			
    		}else if(args[i].equals("--destination")){
        		destination = args[i+1];    			
    		}else if(args[i].equals("--files")){
        		for(int g = i+1;g<args.length;g++){
        	    	if(args[g].equals("--bucket") || args[g].equals("--prefix") || args[g].equals("--destination") || args[g].equals("--files")){
        				i = g-1;
        	    		g = args.length;        	    		
        	    	}else{
        	    		files.add(args[g]);    	    						
        	    	}        	    	
        		}    			
    		}
        }
    	
    	/*
    	 * yes, i used enums as it should be, but i'm switching to if-else on strings so 
    	 * i can have parameter with "-" and make more readable the use in console
    	 * 
    	 * for(;i<args.length;i+=2){
    	switch(supportedParams.valueOf(args[i])){
    		case bucket:
    		bucket = args[i+1];
    		break;
    		case prefix:
    		prefix = args[i+1];
    		break;
    		case destination:
    		destination = args[i+1];
    		break;
    		case files:    		
    		for(int g = i+1;g<args.length;g++){
    	    	try{
    	    		supportedParams.valueOf(args[g]);
    	    		i = g-1;
    	    		g = args.length;
    	    	}catch (IllegalArgumentException e){
    	    		files[files.length+1]=args[g];    	    				
    	    	}
    		}
    		break;
    	}
    	}*/
    	try{
    	switch(ipsilonS3ToolEngine.supportedMethods.valueOf(commandExec)){
    		case del:
          // del [BUCKET_NAME] FILE [FILES...]
    		startObj.deleteFile(bucket,files);
    		break;
    		case delf:
    	   // del [BUCKET_NAME] FOLDER [FOLDERS...]
    	    startObj.deleteFolder(bucket,files);
    	    break;
    		case ls:
    	  // ls [BUCKET_NAME] [PREFIX]
    		startObj.listObjects(bucket,prefix);
    		break;
    		case lb:
    	 // lb
    		startObj.listBuckets();
    		break;
    		case put:
          // put [BUCKET_NAME] [DESTINATION] FILE [FILES...]
    		startObj.putObjects(bucket,destination,files);
    		break;
    		case get:
    	    // get [BUCKET_NAME] [DESTINATION] FILE [FILES...]
    	    startObj.getObjects(bucket,destination,files);
    	    break;
    		case getf:
        	// get [BUCKET_NAME] [DESTINATION] FILE [FILES...]
        	startObj.getObjectsFromFolder(bucket,destination,files);
        	break;
    		default:
    			throw new ipsilonS3ToolException("ERROR: Command doesn't exit, run with 'help' to see instructions of how to use it");        	        		    	
    		}     
    	}catch (IllegalArgumentException err){
    		throw new ipsilonS3ToolException("ERROR: Command doesn't exit, run with 'help' to see instructions of how to use it",err);        	        		 	
    	}
    	
    	}
    }
    public static String[] configureAWSKeys(Preferences prefs){
    	String[] credentials = new String[2];
        Scanner in = new Scanner(System.in);
        System.out.println("Insert your AWS Access Key");
        credentials[0] = in.nextLine();
        System.out.println("Insert your AWS Secret Key");
        credentials[1] = in.nextLine();
        in.close();
    	prefs.put(ACCESS_PROP,credentials[0]);
    	prefs.put(SECRET_PROP,credentials[1]);
        
        return credentials;
    }

}
