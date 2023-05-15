package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class apiAdd extends Thread {
    public static void add_API(JSONObject userId, JSONObject Datas, String accountSeq) throws ParseException {

        //JSONObject
        JSONObject new_userid;

        //참조 class
        Thread apiUser_inactive = new userInactive();
        Thread apiUser_get = new getAPI();

        //JSONParser
        JSONParser parser = new JSONParser();
        JSONObject jsonObj1 = (JSONObject) parser.parse(userId.toString());
        JSONObject jsonObj2 = (JSONObject) parser.parse(Datas.toString());
        JSONArray jsonarry1 = (JSONArray) jsonObj1.get("result");
        JSONObject update = new JSONObject();
        JSONArray roles = new JSONArray();
        roles.add(0, "DEVOPS");

        for (int i=0; i<jsonObj2.size(); i++){
            int count = 0;
            JSONObject AD_user = (JSONObject) jsonObj2.get(Integer.toString(i));
            String AD_user_id = AD_user.get("userId").toString();
            for (int j=0; j<jsonarry1.size(); j++){
                JSONObject API_user = (JSONObject) jsonarry1.get(Integer.parseInt(Integer.toString(j)));
                String API_user_id = API_user.get("userId").toString();
                if (API_user_id.equals(AD_user_id)){
                    count = count + 1;
                }
            }
            if (count == 0){
                AD_user.put("roles", roles);
                System.out.println(AD_user);
                update.put(count, AD_user);

                //URL
                URL url = null;

                // buffer
                String readLine = null;
                StringBuilder sb = null;
                OutputStream out = null;
                BufferedReader br = null;
                BufferedWriter bw = null;

                // HttpURLConnection
                HttpURLConnection conn = null;
                String api_url = System.getenv("COCKTAIL_API");

                String send = AD_user.toString();
                String apiUrl = api_url + "/api/account/" + accountSeq + "/user";

                try
                {
                    url = new URL(apiUrl);

                    //method POST
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    
                    //타임 아웃 설정
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    //header setting
                    conn.setRequestProperty("user-id", "1");
                    conn.setRequestProperty("user-role", "ADMIN");
                    conn.setRequestProperty("Content-Type", "application/json;");
                    conn.setDoOutput(true);
                    conn.setInstanceFollowRedirects(true);

                    out = conn.getOutputStream();

                    bw = new BufferedWriter(new OutputStreamWriter(out,"UTF-8"));
                    bw.write(send);
                    bw.flush();

                    sb = new StringBuilder();
                    if(conn.getResponseCode() == 200)
                    {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                        while((readLine = br.readLine()) != null)
                        {
                            sb.append(readLine).append("\n");
                        }
                    }
                    else
                    {
                        sb.append("code : " + conn.getResponseCode() + "\n" + "message : " + conn.getResponseMessage());
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (bw != null) { bw.close(); }
                        if (out != null) { out.close(); }
                        if (br != null) { br.close(); }
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }

                new_userid = ((getAPI) apiUser_get).get_API(accountSeq);
                ((userInactive) apiUser_inactive).user_Inactive(new_userid, update, accountSeq);
            }
        }
        /*JSONObject new_userid;

        Thread user_inactive = new userInactive();
        Thread getapi = new getAPI();
        JSONParser parser = new JSONParser();
        JSONObject jsonObj1 = (JSONObject) parser.parse(userid.toString());
        JSONObject jsonObj2 = (JSONObject) parser.parse(AD_data.toString());
        JSONArray jsonarry1 = (JSONArray) jsonObj1.get("result");
        JSONObject update_data = new JSONObject();
        JSONArray roles = new JSONArray();
        roles.add(0, "DEVOPS");

        for (int i=0; i<jsonObj2.size(); i++) {
            int count = 0;
            JSONObject AD_user = (JSONObject) jsonObj2.get(Integer.toString(i));
            String AD_id = AD_user.get("userId").toString();
            for (int j = 0; j < jsonarry1.size(); j++) {
                JSONObject API_user = (JSONObject) jsonarry1.get(Integer.parseInt(Integer.toString(j)));
                String API_user_id = API_user.get("userId").toString();
                if (API_user_id.equals(AD_id)) {
                    count = count + 1;
                }
            }
            if (count == 0) {
                AD_user.put("roles", roles);
                System.out.println(AD_user);
                update_data.put(count, AD_user);

                // API_URL 환경 변수 가져오기
                String api_url = System.getenv("API_URL");

                String sendData = AD_user.toString(); // 대다수의 경우 JSON 데이터 사용
                String apiUrl = api_url + "/api/account/" + accountSeq + "/user"; // 각자 상황에 맞는 IP & url 사용

                try {
                    // conn
                    URL url = new URL(apiUrl);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn = (HttpURLConnection) url.openConnection();

                    //method 방식 : PUT
                    conn.setRequestMethod("POST");

                    //타임 아웃 설정 3초
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    //header setting
                    conn.setRequestProperty("user-id", "1");
                    conn.setRequestProperty("user-role", "ADMIN");
                    conn.setRequestProperty("Content-type", "application/json");
                    conn.setDoOutput(true);
                    conn.setInstanceFollowRedirects(true);
                    OutputStream out = conn.getOutputStream();
                    out = conn.getOutputStream();
                    String send = AD_user.toString();
                    String line;

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                    bw.write(send);
                    bw.flush();

                    StringBuilder sb = new StringBuilder();

                    if (conn.getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        } //while
                    } else {
                        sb.append("code : " + conn.getResponseCode() + "\n" + "message : " + conn.getResponseMessage());
                    }
                    // 자원 해제

                    bw.close();
                    out.close();

                    System.out.println(sb);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 새로운 유저 정보 가져오기
                new_userid = getAPI.get_API(accountSeq);
                userInactive.user_Inactive(new_userid, update_data, accountSeq);



            }
        }*/
    }
}