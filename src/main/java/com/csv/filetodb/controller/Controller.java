package com.csv.filetodb.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.csv.file.pojo.FileInfo;
import com.csv.filetodb.service.Service;

@RestController
public class Controller {

	@Autowired
	private Service fileStorageService;

	@PostMapping("/uploadFile")
	public FileInfo uploadFile(@RequestParam("file") MultipartFile file) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		long startTime= System.currentTimeMillis();
		System.out.println("Start time :: " + startTime);
		String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
		long recordCount = 0;
		boolean tableCreated = false; 
		long timeTaken=0;

		try {
			if (FilenameUtils.getExtension(file.getOriginalFilename()).equalsIgnoreCase("csv")) {
				byte[] bytes = null;
				bytes = file.getBytes();
				String completeData = new String(bytes);
				int i = 0;
				ArrayList<String> colums = new ArrayList<String>();
				for (String b : Arrays.asList(completeData.split("\n"))) {
					
					if (i == 0) {
						//table creation part. Dynamic query creation
						StringBuffer sf = new StringBuffer();
						sf.append("CREATE TABLE ");
						sf.append(fileName.toUpperCase() + " ( ");
						String datatype = " VARCHAR2(1000 BYTE),";
						for (String c : Arrays.asList(b.split(","))) {
							sf.append(c + datatype);
							colums.add(c.trim().toLowerCase());
						}
						String query = sf.toString().substring(0, sf.toString().length() - 1).concat(");");
						//query to be executed through service
						System.out.println("Table creation Query ::" + query);
						tableCreated = fileStorageService.dbcall(query);
						System.out.println("Table Created");
						System.out.println("Processing");
					} else if (tableCreated) {
						
						//data insertion part. Dynamic query creation
						StringBuffer sf = new StringBuffer();
						int columValueIndex = 0;
						int pswdIndex = colums.indexOf("password");
						sf.append("insert into ");
						sf.append(fileName.toUpperCase() + " ");
						sf.append(colums.toString().replace("[", "(").replace("]", ")").replace(" ", ""));
						sf.append(" values (");
						for (String c : Arrays.asList(b.split(","))) {
							if (columValueIndex==pswdIndex) { // to ecncrypt passowrd column
								String ecrypted=encript(String.valueOf(c));
								sf.append("'" +ecrypted.hashCode()+ "',");
							} else { // non password coloumn
								sf.append("'" + c + "',");
							}
							columValueIndex++;
						}
						String query = sf.toString().substring(0, sf.toString().length() - 1).concat(")");
						
						//query to be executed through service
						fileStorageService.dbcall(query);
						recordCount++;
					}
					
					i++;
				}
				// checking time duration 
				System.out.println("Rows inserted into table");
				timeTaken= (System.currentTimeMillis()- startTime)/1000;
				System.out.println("Time taken for proccess is (secs) : " +timeTaken+" seconds");
				// returning a response
				return new FileInfo(fileName, file.getContentType(), null, recordCount,timeTaken);
			} else {
				// if not csv, return an error message
				String errMsg = "Please upload csv file only";
				return new FileInfo(fileName, file.getContentType(), errMsg, recordCount,timeTaken);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Done!!!");
		}
		return new FileInfo(fileName, file.getContentType(), null, recordCount,timeTaken);
	}

	public String encript(String pswd) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			BadPaddingException, IllegalBlockSizeException {
		//DES encryption
		Key symKey = KeyGenerator.getInstance("DESede").generateKey();
		Cipher c = Cipher.getInstance("DESede");
		c.init(Cipher.ENCRYPT_MODE, symKey);
		return new String(c.doFinal(pswd.getBytes()));
	}

}
