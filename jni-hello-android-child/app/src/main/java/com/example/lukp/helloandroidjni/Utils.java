package com.example.lukp.helloandroidjni;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;


/***
 * Utility class
 *
 * Extracts zip archives, create and deletes directories
 *
 * based off Anthony Prieur & Daniel Oppenheim work
 * https://code.google.com/p/android-python27/
 *
 */
public class Utils {

  static String TAG = "Utils";

  public static String getFileExtension(String sFileName) {
    int dotIndex = sFileName.lastIndexOf('.');
    if (dotIndex == -1) {
      return null;
    }
    return sFileName.substring(dotIndex);
  }

  /* Use Android's internal chmod by reflection */
  public static int chmod(File path, int mode) throws Exception {
    Class<?> fileUtils = Class.forName("android.os.FileUtils");
    Method setPermissions =
            fileUtils.getMethod("setPermissions", String.class, int.class, int.class, int.class);
    return (Integer) setPermissions.invoke(null, path.getAbsolutePath(), mode, -1, -1);
  }

  public static void unzip(InputStream inputStream, String dest,
                           boolean replaceIfExists) throws Exception {

    final int BUFFER_SIZE = 4096;
    BufferedOutputStream bufferedOutputStream = null;

    try {
      ZipInputStream zipInputStream = new ZipInputStream(
              new BufferedInputStream(inputStream));
      ZipEntry zipEntry;

      while ((zipEntry = zipInputStream.getNextEntry()) != null) {
        String zipEntryName = zipEntry.getName();
        // Check if file exist, if it does then delete
        File file2 = new File(dest + zipEntryName);

          // XXX: This can delete a directory that is still being extracted
        if (file2.isFile() && file2.exists()) {
          if (replaceIfExists) {
            try {
              boolean b = deleteDir(file2);
              if (!b) {
                Log.e(TAG, "Unzip failed to delete " + dest
                        + zipEntryName);
              } else {
                Log.i(TAG, "Unzip deleted " + dest + zipEntryName);
              }
            } catch (Exception e) {
              Log.e(TAG, "Unzip failed to delete " + dest
                      + zipEntryName, e);
            }
          }
        }
        // extract
        File file = new File(dest + zipEntryName);

        if (file.exists()) {

        } else {
          if (zipEntry.isDirectory()) {
            file.mkdirs();
            chmod(file, 0755);

          } else {

            // Create parent file folder if not exists yet
            if (!file.getParentFile().exists()) {
              file.getParentFile().mkdirs();
              chmod(file.getParentFile(), 0755);
            }

            byte buffer[] = new byte[BUFFER_SIZE];
            bufferedOutputStream = new BufferedOutputStream(
                    new FileOutputStream(file), BUFFER_SIZE);
            int count;

            while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
              bufferedOutputStream.write(buffer, 0, count);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
          }
        }

        // Set permission for python files
        if (file.getName().endsWith(".so") || file.getName().endsWith(".py")
                || file.getName().endsWith(".pyc")
                || file.getName().endsWith(".pyo")) {
          chmod(file, 0755);
        }
        Log.i(TAG, "Unzip extracted " + dest + zipEntryName);
      }
      zipInputStream.close();

    } catch (FileNotFoundException e) {
      Log.e(TAG, "Unzip error, file not found", e);
      throw e;
    } catch (Exception e) {
      Log.e(TAG, "Unzip error: ", e);
      throw e;
    }

  }

  public static boolean deleteDir(File dir) {
    try {
      if (dir.isDirectory()) {
        String[] children = dir.list();
        // Delete everything in directory
        for (int i = 0; i < children.length; i++) {
          boolean success = deleteDir(new File(dir, children[i]));
          if (!success) {
            return false;
          }
        }
      }
      // The directory is now empty so delete the folder
      return dir.delete();

    } catch (Exception e) {
      Log.e(TAG, "Failed to delete " + dir + " : " + e);
      return false;
    }
  }
}
