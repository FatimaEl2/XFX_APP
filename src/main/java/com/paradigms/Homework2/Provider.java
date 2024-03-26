package com.paradigms.Homework2;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;




@RestController
public class Provider {

     @GetMapping("/welcome")
    public String welcoming() {
        return "This is a welcoming message!";
        
    }
 
    
    @GetMapping("/browse")
    public ResponseEntity<FileInfo[]> Browse_Files(String path) {

    String Path_Shared_Directory = "C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\SharedDirectory\\";
    
    StringTokenizer ST = new StringTokenizer (path);
   
    ArrayList <String> Directory = new ArrayList<>();
    

    if (path.isEmpty()) {

        Directory.add("C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\SharedDirectory\\"+path);
      
    } else  {
            while(ST.hasMoreTokens()){
                Path_Shared_Directory+="/"+ ST.nextToken("/");
                Directory.add(Path_Shared_Directory) ;
            }
    }
    File File_directory = new File(Path_Shared_Directory);
    
    if (!File_directory.exists()) {
        return ResponseEntity.notFound().build();
    }

    if (!File_directory.isDirectory()) {
        return ResponseEntity.badRequest().body(new FileInfo[0]);
    }

    File[] files = File_directory.listFiles();
    if (files == null) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new FileInfo[0]);
    }

    List<FileInfo> file_List = new ArrayList<>();
    for (File file : files) {
        file_List.add(new FileInfo(file.getName(), file.isDirectory() ? "directory" : "file", file.length() + " kb"));
    }

    FileInfo[] fileArray = file_List.toArray(new FileInfo[0]);

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(fileArray);
    }


	@PatchMapping("/rename")
	public ResponseEntity<String> Rename_File(String path, String newname) {

        boolean worked;

        File File_To_Rename = new File("C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\SharedDirectory\\"+path);
        
        File new_Name = new File("C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\SharedDirectory\\"+newname);
		
       if (File_To_Rename.isDirectory() || File_To_Rename.isFile()) {
             worked = File_To_Rename.renameTo(new_Name);
            if (!worked) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure, we could not rename the file!");
            } else {
                return ResponseEntity.ok().body("Success, the file you specified is renamed!");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("the path you gave us is not a file nor a directory");
	}
	
 
 @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> Download_File( String filename, String path) throws IOException {
        
        File File_To_Download;

        if (path != null && !path.isEmpty()) {
            File_To_Download = new File(path, filename);
       
        } else {
            File_To_Download = new File("C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\SharedDirectory\\", filename);
        }

        if (!File_To_Download.exists()) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource BAresource;

        try (InputStream inputstream = new BufferedInputStream(new FileInputStream(File_To_Download))) {
            BAresource = new ByteArrayResource(inputstream.readAllBytes());
        }

        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + File_To_Download.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(File_To_Download.length()));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(File_To_Download.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(BAresource);
    }

    
 // upload a file
    @PostMapping("/upload")
    public ResponseEntity<String> Upload_File(@RequestParam("File") MultipartFile file,
            @RequestParam(required = false) String path) {
                
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        File directory = new File(path != null && !path.isEmpty() ? path : "C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\SharedDirectory\\");
        if (!directory.isDirectory()) {
            return ResponseEntity.badRequest().body("Invalid path: " + directory.getAbsolutePath());
        }

        File newFile = new File(directory, file.getOriginalFilename());
        if (newFile.exists()) {
            return ResponseEntity.badRequest().body(
                    "File with name " + newFile.getName() + " already exists at path " + directory.getAbsolutePath());
        }

        try (InputStream inputStream = file.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
        }

        return ResponseEntity.ok().body("File uploaded successfully to path " + directory.getAbsolutePath());
    }

    
 // delete a file
    @DeleteMapping("/delete")
    public ResponseEntity<String> Delete_File( String path) {
        
        File File_To_Delete;

        File_To_Delete = new File("C:\\Users\\Mes documents\\Downloads\\Homework2 (1)\\Homework2\\SharedDirectory\\" + path);
       
        if (!File_To_Delete.exists() || File_To_Delete.isDirectory()) {
            return ResponseEntity.badRequest().body("The file you specified does not exist at this path " + path);
        }

        if (!File_To_Delete.delete()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failure, we could not delete the file! " + path);
        }

        return ResponseEntity.ok().body("Success, The File you specified is deleted from path: " + path);
    }
 
}
