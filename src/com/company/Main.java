package com.company;
import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        String desktopDirectory = System.getProperty("user.home") + "\\Desktop";
        String lpuName = "p87";
        String archivation = "7z";//winrar 7z no
        ArrayList<String> inputList = new ArrayList<>();
        try {
            File inputFile = new File(desktopDirectory + "\\input.txt");
            FileReader fR = new FileReader(inputFile);
            BufferedReader reader = new BufferedReader(fR);
            String line = reader.readLine();
            while (line != null) {
                inputList.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String logPath = inputList.get(0);
        String archiverPath = inputList.get(2);
        ArrayList<String> dbPaths = new ArrayList<>();
        for (int i = 3; i < inputList.size(); i++) {
            dbPaths.add(inputList.get(i));
        }
        ArrayList<String> dbNames = new ArrayList<>();
        ArrayList<String> dbDirectories = new ArrayList<>();
        for (int i = 0; i < dbPaths.size(); i++) {
            dbNames.add(dbPaths.get(i).substring(dbPaths.get(i).lastIndexOf('\\') + 1, dbPaths.get(i).indexOf('.') ));
            dbDirectories.add(dbPaths.get(i).substring(0, dbPaths.get(i).lastIndexOf('\\')));
        }
        File file = new File(desktopDirectory + "\\backup_" + lpuName + ".bat");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);

        writer.write("cd /d C:\\Windows\\System32" + "\n");
        writer.write("iisreset /stop" + "\n");
        writer.write("Taskkill /IM SQLiteStudio.exe /F" + "\n");

        for (int i = 0; i < dbPaths.size(); i++) {
            writer.write("echo --------------------------" + dbNames.get(i).toUpperCase() + "-------------------------- >> " + logPath + "\n");
            writer.write("cd /d " + dbDirectories.get(i) + "\n");
            writer.write("echo 1. A new backup started %date% at %time% >> " + logPath + "\n");
            writer.write("if exist " + dbNames.get(i)+ "_backup_*.db forfiles /p " + dbDirectories.get(i) + " /m " + dbNames.get(i) + "_backup_*.db /d -3 /c \"cmd /c del @file\"\n");
            writer.write("echo 2. The old backups were deleted at %time% >>  " + logPath + "\n");
            writer.write("sqlite3 \"" + dbNames.get(i) + ".db\" \".backup '" + dbNames.get(i) + "_backup_%date%.db'\"" + "\n");
            writer.write("echo 3. A new backup was created at %time% >> " + logPath + "\n");
            writer.write("echo ------------------------------------------------------------- >> " + logPath + "\n" + "\n");
        }
        writer.write("cd /d C:\\Windows\\System32" + "\n");
        writer.write("iisreset /start" + "\n");

        writer.write("echo -----------------BACKUPS-ARCHIVATION-STARTS-------------------- >> " + logPath + "\n");
        for (int i = 0; i < dbPaths.size(); i ++) {
            writer.write("echo  Archivation of the base " + dbNames.get(i) + ", number " + i +" started %date% at %time% >> " + logPath + "\n");
            writer.write("cd /d " + dbDirectories.get(i) + "\n");
            if (archivation == "winrar") {
                writer.write("\"" + archiverPath + "\\winrar.exe" + "\" a " + dbNames.get(i) + "_backup_%date%.rar " + dbNames.get(i) + "_backup_%date%.db" + "\n");
                writer.write("if exist " + dbNames.get(i) + "_backup_%date%.rar del " + dbNames.get(i) + "_backup_%date%.db" + "\n");
            } else if (archivation == "7z") {//??????????!!!
                writer.write("\"" + archiverPath + "\\7z.exe" + "\" a " + dbNames.get(i) + "_backup_%date%.7z " + dbNames.get(i) + "_backup_%date%.db" + "\n");
                writer.write("if exist " + dbNames.get(i) + "_backup_%date%.7z del " + dbNames.get(i) + "_backup_%date%.db" + "\n");
            }
            writer.write("echo  Archivation of the base " + dbNames.get(i) + ", number " + i +" ended %date% at %time% >> " + logPath + "\n");
        }
        writer.write("echo -----------------BACKUPS-ARCHIVATION-ENDS-------------------- >> " + logPath + "\n" + "\n");



        writer.flush();
        writer.close();
        System.out.println("?????? ?????? ?????? " + lpuName + " ???????????????????????? ????????????!");
    }
}