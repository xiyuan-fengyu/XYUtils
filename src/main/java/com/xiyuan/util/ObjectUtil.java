package com.xiyuan.util;

import java.io.*;

public class ObjectUtil {

    public static boolean save(Serializable obj, String filePath) {
        File file = new File(filePath);
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(obj);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T load(InputStream in) {
        try (ObjectInputStream objIn = new ObjectInputStream(in)) {
            return (T) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends Serializable> T loadFromFile(String filePath) {
        try (FileInputStream in = new FileInputStream(filePath)) {
            return load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
