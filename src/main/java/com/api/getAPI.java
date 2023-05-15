package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getAPI extends Thread{
    public static JSONObject get_API(String accountSeq) throws ParseException {
        //COCKTAIL_API
        URL url = null;
        String apiUrl = System.getenv("COCKTAIL_API") + "/api/account/" + accountSeq + "/users";

        // buffer
        StringBuilder sb = null;
        BufferedReader br = null;
        String line = null;

        // HttpURLConnection
        HttpURLConnection conn = null;

        try {
            // conn
            url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();

            //method 방식 : GET
            conn.setRequestMethod("GET");

            //타임 아웃 설정 3초
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            //header setting
            conn.setRequestProperty("user-id", "1");
            conn.setRequestProperty("user-role", "ADMIN");
            conn.setRequestProperty("Content-type","application/json");

            sb = new StringBuilder();

            if(conn.getResponseCode() == 200){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null){
                    sb.append(line);
                } //while
            } else {
                sb.append("code : " + conn.getResponseCode() + "\n" + "message : " + conn.getResponseMessage());
            }

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (br != null){
                    br.close();
                }
                if (conn != null){
                    conn.disconnect();
                }
            } catch (Exception e){
                e.printStackTrace();
            }//catch
        }

        //JSONParser
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(sb.toString());
        JSONArray jsonArray = (JSONArray) jsonObject.get("result");
        JSONObject userId = new JSONObject();
        JSONObject apiUser = new JSONObject();

        for (int i = 0; i<jsonArray.size(); i ++){
            JSONObject id = new JSONObject();
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            String userid = jsonObject1.get("userId").toString();
            id.put("userId", userid);
            userId.put(i,id);
        }

        //System.out.println(userId);
        return jsonObject;
    }

}
