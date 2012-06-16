package com.ipsilonDev.ipsilonS3ToolLib;

import java.util.List;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.STSSessionCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.ipsilonDev.ipsilonS3ToolLib.ipsilonS3ToolException;

public class ipsilonS3ToolEngine {


    public AmazonS3       s3;
    public AWSCredentials credentials;
    public enum supportedMethods{
    	mb,rb,cp,mv,lb,ls,put,get,getf,del,delf;
    }
    public String[] args;
    private String accessKey;
    private String secretKey;
    protected EventListenerList listenerUploading = new EventListenerList();
    protected EventListenerList listenerDeleting = new EventListenerList();
    protected EventListenerList listenerProgress = new EventListenerList();
    protected EventListenerList listenerRDirectory = new EventListenerList();
    protected EventListenerList listenerLDirectory = new EventListenerList();
    protected EventListenerList listenerListing = new EventListenerList();
    protected EventListenerList listenerListingBuckets = new EventListenerList();
    protected EventListenerList listenerErrors = new EventListenerList();
    protected EventListenerList listenerDownloading = new EventListenerList();
    
    public void setCredentials(String access, String secret) throws ipsilonS3ToolException{
    	accessKey = access;
    	secretKey = secret;
    	init();
    }    
    
     public void addEventListener(String type, ipsilonS3ToolEngineListener listener) {
        if(type.equals(ipsilonS3ToolEvent.TYPE_DELETING)){
    	 listenerDeleting.add(ipsilonS3ToolEngineListener.class, listener);
        }
        if(type.equals(ipsilonS3ToolEvent.TYPE_UPLOADING)){
    	 listenerUploading.add(ipsilonS3ToolEngineListener.class, listener);
        }
        if(type.equals(ipsilonS3ToolEvent.TYPE_DOWNLOADING)){
    	 listenerDownloading.add(ipsilonS3ToolEngineListener.class, listener);
        }
        if(type.equals(ipsilonS3ToolEvent.TYPE_PROGRESS)){
    	 listenerProgress.add(ipsilonS3ToolEngineListener.class, listener);
        }
        if(type.equals(ipsilonS3ToolEvent.TYPE_REMOTEMKDIR)){
    	 listenerRDirectory.add(ipsilonS3ToolEngineListener.class, listener);
        }
        if(type.equals(ipsilonS3ToolEvent.TYPE_LISTING)){
    	 listenerListing.add(ipsilonS3ToolEngineListener.class, listener);
        }
        if(type.equals(ipsilonS3ToolEvent.TYPE_LISTINGBUCKETS)){
        	listenerListingBuckets.add(ipsilonS3ToolEngineListener.class, listener);
        }       
        if(type.equals(ipsilonS3ToolEvent.TYPE_ERROR)){
        	listenerErrors.add(ipsilonS3ToolEngineListener.class, listener);
        }       
      }
      public void removeEventListener(String type, ipsilonS3ToolEngineListener listener) {
          if(type.equals(ipsilonS3ToolEvent.TYPE_DELETING)){
         	 listenerDeleting.remove(ipsilonS3ToolEngineListener.class, listener);
             }
             if(type.equals(ipsilonS3ToolEvent.TYPE_UPLOADING)){
         	 listenerUploading.remove(ipsilonS3ToolEngineListener.class, listener);
             }
             if(type.equals(ipsilonS3ToolEvent.TYPE_DOWNLOADING)){
             listenerDownloading.remove(ipsilonS3ToolEngineListener.class, listener);
             }
             if(type.equals(ipsilonS3ToolEvent.TYPE_PROGRESS)){
         	 listenerProgress.remove(ipsilonS3ToolEngineListener.class, listener);
             }
             if(type.equals(ipsilonS3ToolEvent.TYPE_REMOTEMKDIR)){ 
         	 listenerRDirectory.remove(ipsilonS3ToolEngineListener.class, listener);
             }
             if(type.equals(ipsilonS3ToolEvent.TYPE_LISTING)){
            	 listenerListing.remove(ipsilonS3ToolEngineListener.class, listener);
             }
             if(type.equals(ipsilonS3ToolEvent.TYPE_LISTINGBUCKETS)){
             	listenerListingBuckets.remove(ipsilonS3ToolEngineListener.class, listener);
             }       
             if(type.equals(ipsilonS3ToolEvent.TYPE_ERROR)){
             	listenerErrors.remove(ipsilonS3ToolEngineListener.class, listener);
             }       
      }
      void fireEvent(ipsilonS3ToolEvent evt) throws ipsilonS3ToolException {
    	  Object[] listeners;
             if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_DELETING){
             listeners = listenerDeleting.getListenerList();
              }else if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_UPLOADING){
             listeners = listenerUploading.getListenerList();
              }else if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_PROGRESS){
             listeners = listenerProgress.getListenerList();
              }else if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_REMOTEMKDIR){
             listeners = listenerRDirectory.getListenerList();
              }else if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_LISTING){
          	 listeners = listenerListing.getListenerList();
              }else if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_LISTINGBUCKETS){
          	 listeners = listenerListingBuckets.getListenerList();
              }else if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_ERROR){
          	 listeners = listenerErrors.getListenerList();
              }else if(evt.typeEvent == ipsilonS3ToolEvent.TYPE_DOWNLOADING){
          	 listeners = listenerDownloading.getListenerList();
              }else{
            	  throw new ipsilonS3ToolException("wrong event");
              }

        for (int i = 0; i < listeners.length; i = i+2) {
          if (listeners[i] == ipsilonS3ToolEngineListener.class) {
            ((ipsilonS3ToolEngineListener) listeners[i+1]).process(evt);
          }
        }
      }
    
    
    private void init() throws ipsilonS3ToolException {
        credentials = new BasicAWSCredentials(
        		accessKey, secretKey);
        STSSessionCredentialsProvider sstCred = new STSSessionCredentialsProvider(credentials);
        try{
        s3  = new AmazonS3Client(sstCred);
        }catch(AmazonClientException err){
        	throw new ipsilonS3ToolException(err.getMessage(),err);
        }
    }

    public ipsilonS3ToolEngine() throws Exception {
    	super();       
    }
    
    public void getObjectsFromFolder(String bucket, String destination, List<String> folders) throws ipsilonS3ToolException{
        if(!destination.endsWith("/")){
    		destination+="/";
    	}
    	for(String folderItem : folders){
    	
    	 		if(folderItem.startsWith("/")){
         		   folderItem = folderItem.substring(folderItem.indexOf("/")+1);
                	}         		
         		fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_DOWNLOADING,folderItem));		 	
             	TransferManager tx = new TransferManager(credentials);
        		File fl = new File(destination);
        		MultipleFileDownload downloadObj = tx.downloadDirectory(bucket, folderItem, fl);
        		 while (downloadObj.isDone() == false) {
        		    fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_PROGRESS,folderItem,downloadObj.getProgress().getBytesTransfered(),downloadObj.getProgress().getTotalBytesToTransfer()));		 	
        		     try {
        		    	 Thread.sleep(500);
        		    	 }catch (InterruptedException e){
        		    		 throw new ipsilonS3ToolException("Failed to upload file",e);
        		    	 }
        		 }    		 
        		 tx.shutdownNow();
         		
            
    		
		 
    	}
    }
    
    public void getObjects(String bucket, String destination, List<String> files) throws ipsilonS3ToolException{
    	
    	if(!destination.endsWith("/")){
    		destination+="/";
    	}
    	for(String fileItem : files){
    	fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_DOWNLOADING,fileItem));		 	
		TransferManager tx = new TransferManager(credentials);
		File fl = new File(destination + new File(fileItem).getName());
		Download downloadObj = tx.download(bucket, fileItem, fl);
		 while (downloadObj.isDone() == false) {
		    fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_PROGRESS,fileItem,downloadObj.getProgress().getBytesTransfered(),downloadObj.getProgress().getTotalBytesToTransfer()));		 	
		     try {
		    	 Thread.sleep(500);
		    	 }catch (InterruptedException e){
		    		 throw new ipsilonS3ToolException("Failed to upload file",e);
		    	 }
		 }    		 
		 tx.shutdownNow(); 
    	}
    }
    
    public void listObjects(String bucket) throws Exception {
    	listObjects(bucket,"");
    }
    public void listObjects(String bucket, String prefix) throws Exception {
         ListObjectsRequest listObjectsRequest;
         ObjectListing objectListing;            
        	 try {
                 if(prefix!=""){
                 listObjectsRequest = new ListObjectsRequest()
                     .withBucketName(bucket)
                     .withPrefix(prefix);
                 }else{
                     listObjectsRequest = new ListObjectsRequest()
                     .withBucketName(bucket);                	                 	 
                 }
                 do {
                     objectListing = s3.listObjects(listObjectsRequest);
                     for (S3ObjectSummary objectSummary : 
                     	objectListing.getObjectSummaries()) {
             		    fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_LISTING,objectSummary.getKey(),objectSummary.getSize()));		 	                         
                     }
                     listObjectsRequest.setMarker(objectListing.getNextMarker());
                 } while (objectListing.isTruncated());
              } catch (AmazonServiceException ase) {
            	  throw new ipsilonS3ToolException(ase.getMessage(),ase);
             } catch (AmazonClientException ace) {
           	  throw new ipsilonS3ToolException(ace.getMessage(),ace);
             }
         
          	
    }
    public void deleteFolder(String bucket, List<String> listFolders) throws Exception{
        ListObjectsRequest listObjectsRequest;
        ObjectListing objectListing;            
       	for(String folderItem : listFolders){
        	if(!folderItem.trim().equalsIgnoreCase("/") && !folderItem.isEmpty()){
        try {
        		if(folderItem.startsWith("/")){
        		   folderItem = folderItem.substring(folderItem.indexOf("/")+1);
               	}
                
                listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(bucket)
                    .withPrefix(folderItem);                
                do {
                    objectListing = s3.listObjects(listObjectsRequest);
                    for (S3ObjectSummary objectSummary : 
                    	objectListing.getObjectSummaries()) {
                    	s3.deleteObject(new DeleteObjectRequest(bucket, objectSummary.getKey()));
            		    fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_DELETING,objectSummary.getKey()));		 	                         
                    }
                    listObjectsRequest.setMarker(objectListing.getNextMarker());
                } while (objectListing.isTruncated());
             } catch (AmazonServiceException ase) {
           	  throw new ipsilonS3ToolException(ase.getMessage(),ase);
            } catch (AmazonClientException ace) {
          	  throw new ipsilonS3ToolException(ace.getMessage(),ace);
            }
       	}
       }
    }
    public void deleteFile(String bucket, List<String> listFiles) throws Exception{
    	try {
        for(String fileItem : listFiles){
    	s3.deleteObject(new DeleteObjectRequest(bucket, fileItem));
	    fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_DELETING,fileItem));		 	                         
        }
        	}catch (AmazonServiceException ase) {
        		throw new ipsilonS3ToolException(ase.getMessage(),ase);
        } catch (AmazonClientException ace) {
    		throw new ipsilonS3ToolException(ace.getMessage(),ace);
        }

    	
    	
    }
    public void putObjects(String bucket, String destination, List<String> listFiles) throws Exception {
    	
    	if(destination.trim().equalsIgnoreCase("/")){
    		destination="";
    	}
       	if(destination.startsWith("/")){
       	destination = destination.substring(destination.indexOf("/")+1);
       	}
        if(!destination.endsWith("/") && !destination.isEmpty()){
    		destination+="/";
    	}
    	List<String[]> fileList;
    	//building file processing array	
    	for(String fileItem : listFiles){
    		File f = new File(fileItem);
    		if(f.exists()){
    			if(f.isDirectory()){
    				doCreateDirectory(bucket, destination + f.getName() + "/");
    				fileList = new ArrayList<String[]>();
    				fileList = recursiveListDir(f, destination + f.getName(), fileList);	
    		    	
    				for (String[] fileNameUnit : fileList) {
    					if(fileNameUnit[0]=="mkdir"){
    		    			doCreateDirectory(bucket, fileNameUnit[1] + fileNameUnit[2]   + "/" );		
    		    		}else{    		    			
    	    				File fr = new File(fileNameUnit[3]);
    	    				doUploadFile(bucket, fr , fileNameUnit[1]+"/"+fileNameUnit[2]);
    		    		}
    		    		
    		    	}
    				
    		    	    				
    			}else{
    				doUploadFile(bucket,f, destination + f.getName());
    			}
    			
    			
    			
    		}else{

    		}
    	}
    	
    }
    public List<String[]> recursiveListDir(File f , String prefix ,List<String[]> fl){
    	File[] fileListDir = f.listFiles();
    	for (File flTemp : fileListDir) {
    	if(flTemp.isDirectory()){
    		fl.add(new String[] {"mkdir", prefix + "/"  , flTemp.getName()});
    		fl = recursiveListDir(flTemp, prefix + "/" + flTemp.getName(), fl);
    	}else{
    		fl.add(new String[] {"put", prefix , flTemp.getName(), flTemp.getPath()});    		
    	}
    	}
    	return fl;
    }
    public void doCreateDirectory(String bucket, String dir) throws ipsilonS3ToolException{

    	fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_REMOTEMKDIR,dir));		 	
    	InputStream input = new ByteArrayInputStream(new byte[0]);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        try{
        s3.putObject(new PutObjectRequest(bucket, dir, input, metadata));
        } catch (AmazonServiceException ase) {
            throw new ipsilonS3ToolException("We can't create directory on bucket",ase);
        } catch (AmazonClientException ace) {
            throw new ipsilonS3ToolException("We can't create directory on bucket",ace);
        }

    }
    public void doUploadFile(String bucketName,File file, String keyName) throws ipsilonS3ToolException{
    	fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_UPLOADING,keyName));		 	
    		TransferManager tx = new TransferManager(credentials);
    		Upload uploadObj = tx.upload(bucketName, keyName, file);
    		 while (uploadObj.isDone() == false) {
    		    fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_PROGRESS,keyName,uploadObj.getProgress().getBytesTransfered(),uploadObj.getProgress().getTotalBytesToTransfer()));		 	
    		     try {
    		    	 Thread.sleep(500);
    		    	 }catch (InterruptedException e){
    		    		 throw new ipsilonS3ToolException("Failed to upload file",e);
    		    	 }
    		 }    		 
    		 tx.shutdownNow(); 
            
    }
    public void listBuckets() throws ipsilonS3ToolException{
         try {
             List<Bucket> bucketList = s3.listBuckets();
        	 int j = 0;
        	 while (j < bucketList.size()) {
         		    fireEvent(new ipsilonS3ToolEvent(ipsilonS3ToolEvent.TYPE_LISTINGBUCKETS,bucketList.get(j).getName()));		 	                         
        			j++;
        		}
          } catch (AmazonServiceException ase) {
        	  throw new ipsilonS3ToolException(ase.getMessage(),ase);
         } catch (AmazonClientException ace) {
       	  throw new ipsilonS3ToolException(ace.getMessage(),ace);
         }
    }
}
