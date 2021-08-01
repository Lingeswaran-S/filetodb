'use strict';

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');
var singleFileUploadProcess = document.querySelector('#singleFileUploadProcess');
var table = document.querySelector('#table');


singleUploadForm.addEventListener('submit', function(event){
    var files = singleFileUploadInput.files;
    if(files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a csv file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
}, true);

function _(el) {
  return document.getElementById(el);
}

function uploadSingleFile(file) {
  var formdata = new FormData();
  formdata.append("file", file);
  var ajax = new XMLHttpRequest();
  ajax.upload.addEventListener("progress", progressHandler, false);
  ajax.addEventListener("error", errorHandler, false);
  ajax.addEventListener("abort", abortHandler, false);
  ajax.open("POST", "/uploadFile");
  ajax.onload = function() {
        console.log(ajax.responseText);
        var response = JSON.parse(ajax.responseText);
        if(ajax.status == 200 && response.errorMsg==null) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Proccessed successfully.</b> Inserted " +response.recordCount+ " records in "+response.timeduration+" seconds</p>";
            singleFileUploadSuccess.style.display = "block";
            table.style.display = "block";
        } else {
            singleFileUploadSuccess.style.display = "none";
            table.style.display = "none";
            singleFileUploadError.style.display = "block";
            singleFileUploadError.innerHTML ="Error : "+response.errorMsg;
        }
         singleFileUploadProcess.innerHTML = "";
         singleFileUploadProcess.style.display = "none";
    }
  ajax.send(formdata);
}

function progressHandler(event) {
  singleFileUploadSuccess.style.display = "none";
  singleFileUploadError.style.display = "none";
  table.style.display = "none";
  singleFileUploadProcess.style.display = "block";
  singleFileUploadProcess.innerHTML = "";
  var percent = (event.loaded / event.total) * 100;
  singleFileUploadProcess.innerHTML = Math.round(percent) + "% uploaded. Processing...";
}

function errorHandler(event) {
  singleFileUploadProcess.innerHTML = "Upload Failed";
}

function abortHandler(event) {
  singleFileUploadProcess.innerHTML = "Upload Aborted";
}
