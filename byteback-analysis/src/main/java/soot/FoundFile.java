package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.util.SharedCloseable;

public class FoundFile {
  private static final Logger logger = LoggerFactory.getLogger(FoundFile.class);

  protected final List<InputStream> openedInputStreams = new ArrayList<InputStream>();

  protected Path path;

  protected File file;
  protected String entryName;

  protected SharedCloseable<ZipFile> zipFile;
  protected ZipEntry zipEntry;

  public FoundFile(String archivePath, String entryName) {
    if (archivePath == null || entryName == null) {
      throw new IllegalArgumentException("Error: The archive path and entry name cannot be null.");
    }

    this.file = new File(archivePath);
    this.entryName = entryName;
  }

  public FoundFile(File file) {
    if (file == null) {
      throw new IllegalArgumentException("Error: The file cannot be null.");
    }

    this.file = file;
    this.entryName = null;
  }

  public FoundFile(Path path) {
    this.path = path;
  }

  public File getFile() {
    return file;
  }

  public InputStream inputStream() {
    InputStream inputStream;

    if (path != null) {
      try {
        inputStream = Files.newInputStream(path);
      } catch (IOException e) {
        throw new RuntimeException(
            "Error: Failed to open a InputStream for the file at path '" + path.toAbsolutePath() + "'.", e);
      }
    } else if (entryName == null) {
      try {
        inputStream = new FileInputStream(file);
      } catch (Exception e) {
        throw new RuntimeException("Error: Failed to open a InputStream for the file at path '" + file.getPath() + "'.", e);
      }
    } else {
      if (zipFile == null) {
        try {
          zipFile = SourceLocator.v().archivePathToZip.getRef(file.getPath());
          zipEntry = zipFile.get().getEntry(entryName);
          if (zipEntry == null) {
            silentClose();
            throw new RuntimeException(
                "Error: Failed to find entry '" + entryName + "' in the archive file at path '" + file.getPath() + "'.");
          }
        } catch (Exception e) {
          silentClose();
          throw new RuntimeException(
              "Error: Failed to open the archive file at path '" + file.getPath() + "' for entry '" + entryName + "'.", e);
        }
      }

      try (InputStream stream = zipFile.get().getInputStream(zipEntry)) {
        inputStream = readChunk(stream, zipEntry.getSize());
      } catch (Exception e) {
        throw new RuntimeException("Error: Failed to open a InputStream for the entry '" + zipEntry.getName()
            + "' of the archive at path '" + zipFile.get().getName() + "'.", e);
      }
    }

    inputStream = new BufferedInputStream(inputStream);
    openedInputStreams.add(inputStream);
    return inputStream;
  }

  public void close() {
    // Try to close all opened input streams
    List<Exception> errs = new ArrayList<Exception>(0);
    for (InputStream is : openedInputStreams) {
      try {
        is.close();
      } catch (Exception e) {
        errs.add(e);// record errors for later
      }
    }
    openedInputStreams.clear();
    closeZipFile(errs);

    // Throw single exception combining all errors
    if (!errs.isEmpty()) {
      String msg = null;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try (PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
        ps.println("Error: Failed to close all opened resources. The following exceptions were thrown in the process: ");
        int i = 0;

        for (Throwable t : errs) {
          ps.print("Exception ");
          ps.print(i++);
          ps.print(": ");
          logger.error(t.getMessage(), t);
        }

        msg = baos.toString(StandardCharsets.UTF_8);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      throw new RuntimeException(msg);
    }
  }

  protected void closeZipFile(List<Exception> errs) {
    // Try to close the opened zip file if it exists
    if (zipFile != null) {

      try {
        zipFile.close();
        errs.clear();// Successfully closed the archive so all input
        // streams were closed successfully also
      } catch (Exception e) {
        errs.add(e);
      }

      zipFile = null;// set to null no matter what
      zipEntry = null;// set to null no matter what
    }
  }

  private static InputStream readChunk(final InputStream inputStream, final long size) throws IOException {
    int isize = (int) size;
    final byte[] buffer = new byte[isize];
    final int N = 1024;

    for (int offset = 0, count = 0; isize > 0 && (offset = inputStream.read(buffer, count, Math.min(N, isize))) != -1;) {
      count += offset;
      isize -= offset;
    }

    return new ByteArrayInputStream(buffer);
  }

  private void silentClose() {
    try {
      close();
    } catch (Exception e) {
      LoggerFactory.getLogger(getClass()).debug(e.getMessage(), e);
    }
  }

}
