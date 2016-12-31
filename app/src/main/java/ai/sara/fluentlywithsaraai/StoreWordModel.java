package ai.sara.fluentlywithsaraai;

/**
 * Created by russ.fugal on 12/31/2016.
 */

import android.util.LogPrinter;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StoreWordModel {
    public static void Flatten (SightWords model) {
        String filename = model.getUserId();
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(model);
            out.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    public static SightWords Inflate (String userId) {
        String filename = userId;
        SightWords model = null;
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(filename);
            in = new ObjectInputStream(fis);
            model = (SightWords) in.readObject();
            in.close();
            return model;
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
