package com.team4.server;

import java.io.File;
import java.io.IOException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "/send_data")
public class SendDataController {
    @PostMapping
    public String handleFileUpload(@RequestParam("file") MultipartFile sourceFile,@RequestHeader(value="deviceMAC") String deviceID) throws IOException {
    	
    	String path = "/home/kwkwon/DeviceFolder/"+deviceID;
    	System.out.println("path: "+path);
    	File Folder = new File(path);

    	if (!Folder.exists()) {
    		try{
    		    Folder.mkdir(); //폴더 생성합니다.
    	        } 
    	        catch(Exception e){
    		    e.getStackTrace();
    		}        
             }else {
    		System.out.println("Folder exist!");
            throw new NotExistingException("Already existing BLE result");
    	}
    	
    	File target = new File(path,sourceFile.getOriginalFilename());
    	FileCopyUtils.copy(sourceFile.getBytes(), target);
    	
    	return "Success!!";

    }
}
