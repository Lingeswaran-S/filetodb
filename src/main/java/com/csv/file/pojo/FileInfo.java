package com.csv.file.pojo;

public class FileInfo {
private String filename;
private String fileType;
private String errorMsg;
private long recordCount;
private double timeduration;
public FileInfo(String filename, String fileType, String errorMsg, long recordCount, double timeduration) {
	super();
	this.filename = filename;
	this.fileType = fileType;
	this.errorMsg = errorMsg;
	this.recordCount = recordCount;
	this.timeduration = timeduration;
}
public String getFilename() {
	return filename;
}
public void setFilename(String filename) {
	this.filename = filename;
}
public String getFileType() {
	return fileType;
}
public void setFileType(String fileType) {
	this.fileType = fileType;
}
public String getErrorMsg() {
	return errorMsg;
}
public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
}
public long getRecordCount() {
	return recordCount;
}
public void setRecordCount(long recordCount) {
	this.recordCount = recordCount;
}
public double getTimeduration() {
	return timeduration;
}
public void setTimeduration(double timeduration) {
	this.timeduration = timeduration;
}


}

