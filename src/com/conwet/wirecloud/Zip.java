/*
 *  Copyright (c) 2013 CoNWeT Lab., Universidad Politécnica de Madrid
 *  
 *  This file is part of Wirecloud IDE.
 *
 *  Wirecloud IDE is free software: you can redistribute it and/or modify
 *  it under the terms of the European Union Public Licence (EUPL)
 *  as published by the European Commission, either version 1.1
 *  of the License, or (at your option) any later version.
 *
 *  Wirecloud IDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  European Union Public Licence for more details.
 *
 *  You should have received a copy of the European Union Public Licence
 *  along with Wirecloud IDE.
 *  If not, see <https://joinup.ec.europa.eu/software/page/eupl/licence-eupl>.
 */

package com.conwet.wirecloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {

  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;


  public void zipFile(String fileToZip, String zipFile, boolean excludeContainingFolder)
    throws IOException {        

	zipFile(fileToZip, new File(zipFile), excludeContainingFolder);
  }

  public void zipFile(String fileToZip, File zipFile, boolean excludeContainingFolder)
    throws IOException {        
    ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));    

    File srcFile = new File(fileToZip);
    if(excludeContainingFolder && srcFile.isDirectory()) {
      for(String fileName : srcFile.list()) {
        addToZip("", fileToZip + "/" + fileName, zipOut);
      }
    } else {
      addToZip("", fileToZip, zipOut);
    }

    zipOut.flush();
    zipOut.close();

    System.out.println("Successfully created " + zipFile);
  }
  
  private static void addToZip(String path, String srcFile, ZipOutputStream zipOut)
    throws IOException {        
    File file = new File(srcFile);
    String filePath = "".equals(path) ? file.getName() : path + "/" + file.getName();
    if (file.isDirectory()) {
      for (String fileName : file.list()) {             
        addToZip(filePath, srcFile + "/" + fileName, zipOut);
      }
    } else {
      zipOut.putNextEntry(new ZipEntry(filePath));
      FileInputStream in = new FileInputStream(srcFile);

      byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
      int len;
      while ((len = in.read(buffer)) != -1) {
        zipOut.write(buffer, 0, len);
      }

      in.close();
    }
  }
}