/*
 *     Copyright (c) 2013 CoNWeT Lab., Universidad Politécnica de Madrid
 *
 *     This file is part of the Wirecloud IDE Project,
 *
 *     http://conwet.fi.upm.es/wirecloud/ide
 *
 *     Licensed under the GNU General Public License, Version 3.0 (the 
 *     "License"); you may not use this file except in compliance with the 
 *     License.
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     under the License is distributed in the hope that it will be useful, 
 *     but on an "AS IS" BASIS, WITHOUT ANY WARRANTY OR CONDITION,
 *     either express or implied; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  
 *     See the GNU General Public License for specific language governing
 *     permissions and limitations under the License.
 *
 *     <http://www.gnu.org/licenses/gpl.txt>.
 *
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