package com.api;

//import
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getADUsers extends Thread{

    public static JSONObject get_ADUsers() throws ParseException {
        // URL
        URL url = null;

        // buffer
        StringBuilder sb = null;
        BufferedReader br = null;

        // HttpURLConnection
        HttpURLConnection conn = null;
        String ad_url = System.getenv("AD_URL");

        String adUrl = ad_url;

        try {
            // conn
            url = new URL(adUrl);
            conn = (HttpURLConnection) url.openConnection();

            //method 방식 : GET
            conn.setRequestMethod("GET");

            //타임 아웃 설정 3초
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestProperty("Content-type","application/json");

            sb = new StringBuilder();

            if(conn.getResponseCode() == 200){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

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

        // test
        System.out.println(sb);

        //JSONParser
        JSONParser parser = new JSONParser();
        JSONArray arr = (JSONArray) parser.parse(sb.toString());
        JSONObject Datas = new JSONObject();

        int count = 0;
        for(Object obj : arr){
            JSONObject data = new JSONObject();
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject jsonObject1 = (JSONObject)jsonObject.get("attributes");
            if(jsonObject.containsKey("username") && jsonObject.containsKey("firstName") && jsonObject.containsKey("lastName")){
                String username = jsonObject.get("username").toString();
                String firstName = jsonObject.get("firstName").toString();
                String lastName = jsonObject.get("lastName").toString();

                //test
                System.out.println(username + firstName + lastName);

                if (jsonObject.containsKey("attributes")) {
                    JSONArray jsonArray1 = (JSONArray) jsonObject1.get("LDAP_ENTRY_DN");
                    String dev = jsonArray1.get(0).toString();
                    String[] ArraysStr = dev.split(",");
                    String department = ArraysStr[1].substring(3);
                    data.put("userId", username);
                    data.put("userName", lastName + firstName);
                    data.put("userDepartment", department);
                    Datas.put(count, data);
                    count++;
                }
            }
        }
        return  Datas; //get_ADUsers
    } //get_ADUsers()


}//getADUsers
