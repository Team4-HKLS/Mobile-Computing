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
	public String handleFileUpload(@RequestParam("file") MultipartFile[] sourceFile,
			@RequestHeader(value = "deviceMAC") String deviceID) throws IOException {
		int deviceNum = App.searchDevice(deviceID);
		if (deviceNum == -1) {
			throw new NotExistingException("Not existing deviceID");
		}
		String path = App.path + deviceID;
		System.out.println("path: " + path);
		File Folder = new File(path);

		if (!Folder.exists()) {
			Folder.mkdir(); // 폴더 생성합니다.
		}

		System.out.println("number of files: "+sourceFile.length);
		for (int i = 0; i < sourceFile.length; i++) {
			File target = new File(path, sourceFile[i].getOriginalFilename());
			FileCopyUtils.copy(sourceFile[i].getBytes(), target);
		}

		App.List.get(deviceNum).setUploadFile();
		return "Success!!";
	}
}
