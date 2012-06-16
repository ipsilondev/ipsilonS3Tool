ipsilonS3Tool
=============

A command line tool for S3 service, supporting multi-upload for big files and folder download by [Ipsilon Developments](https://www.y-developments.com/)

**compiled JAR ready to use included** 

What is the purpose of this?
=============

The idea is to provide a command line too for the S3 service. we know there are others available on the internet (like the great s3cmd), 
but we found problems downloading/uploading folders and big files (2GB+), so we made a new one that works :D

How to use it
=============

the available commands at this moments are:

####List Buckets
    java -jar ipsilonS3Tool.jar lb

####List Objects
    java -jar ipsilonS3Tool.jar ls --bucket [bucketName] --prefix [prefix]  

####Upload file/files/directories
    java -jar ipsilonS3Tool.jar put --bucket [bucketName] --destination [destinationInTheBucket] --files [file1] [file2] [file3]

####Download files
    java -jar ipsilonS3Tool.jar get --bucket [bucketName] --destination [destinationInYourHardDisk] --files [filePathInBucket1] [filePathInBucket2] [filePathInBucket3]

####Download Folders
    java -jar ipsilonS3Tool.jar get --bucket [bucketName] --destination [destinationInYourHardDisk] --files [folderPathInBucket1] [folderPathInBucket1] [folderPathInBucket1]

####Delete files
    java -jar ipsilonS3Tool.jar del --bucket [bucketName] --files [file1] [file2] [file3]

####Delete folders
    java -jar ipsilonS3Tool.jar delf --bucket [bucketName] --files [folder1] [folder2] [folder3]

Other uses
=============

The code have 1 main class that receive the command and execute the functions under the ipsilonS3ToolLib package, so you can grab it alone and use it in any of your apps :)

TODO
=============
* A better management of the exceptions
* Add move, copy, create bucket, delete bucket commands.
* Add extra features as ACL and user metadata to the libs and command line
* Windows testing