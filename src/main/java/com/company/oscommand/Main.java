package com.company.oscommand;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 10/29/2016.
 */
public class Main {
    public static void main(String[] args) {
        try {
            JsonRpcHttpClient client = new JsonRpcHttpClient(
                    new URL("http://192.168.79.128:6800/jsonrpc"));
            Object message = client.invoke("aria2.tellStopped",new Object[]{0,10,new String[]{"gid","status","files"}},Object.class);
            Object message2 = client.invoke("aria2.tellStatus",new Object[]{"3a59bf681d74f856"},Object.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

}
