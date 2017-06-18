package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tomi on 17/06/17.
 */
public class Listener extends Thread{

    private ChatClient client;
    private InputStream reader;

    public Listener(ChatClient client, InputStream reader) {
        this.client = client;
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.reader));
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println(line);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
