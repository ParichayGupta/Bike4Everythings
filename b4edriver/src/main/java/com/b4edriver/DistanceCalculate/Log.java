package com.b4edriver.DistanceCalculate;

import android.os.Environment;

import java.io.File;

public class Log {
    static String APP_NAME = "b2b";
    static String LOG_FILE = "LOGFILE";
    private static final boolean WRITE_TO_FILE = false;
    private static final boolean WRITE_TO_FILE_IN = false;

    public static void m616i(String tag, String message) {
    }

    public static void m614d(String tag, String message) {
    }

    public static void m615e(String tag, String message) {
    }

    public static void m617v(String tag, String message) {
    }

    public static void m618w(String tag, String message) {
    }

    public static void writeLogToFile(String filePrefix, String response) {
    }

    public static File getPathLogFolder() {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/JugnooData");
            if (folder.exists()) {
                return folder;
            }
            folder.mkdirs();
            return folder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getPathLogFile(String filePrefix, boolean createNew) {
        try {
            File gpxfile = new File(getPathLogFolder() + "/" + filePrefix + ".txt");
            if (gpxfile.exists() || !createNew) {
                return gpxfile;
            }
            gpxfile.createNewFile();
            return gpxfile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   /* public static void writePathLogToFile(String filePrefix, String response) {
        try {
            File gpxfile = getPathLogFile(filePrefix, true);
            if (gpxfile != null) {
                FileWriter writer = new FileWriter(gpxfile, true);
                writer.append("\n" + DateOperations.getCurrentTime() + " - " + response);
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
        }
    }
*/
    public static void deleteFolder(File folder) {
        try {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteFolder(f);
                    } else {
                        f.delete();
                    }
                }
            }
            folder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deletePathLogFolder() {
        try {
            deleteFolder(getPathLogFolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
