package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class apiAdd extends Thread {
    public static void add_API(JSONObject userid, JSONObject AD_data, String accountSeq) throws ParseException {
        JSONObject new_userid;
        Thread apiUser_inactive = new userInactive();
        Thread apiUser_get = new getAPI();
        JSONParser parser = new JSONParser();
        JSONObject jsonObj1 = (JSONObject) parser.parse(userid.toString());
        JSONObject jsonObj2 = (JSONObject) parser.parse(AD_data.toString());
        JSONArray jsonarry1 = (JSONArray) jsonObj1.get("result");
        JSONObject update_data = new JSONObject();
        JSONArray roles = new JSONArray();
        roles.add(0, "DEVOPS");

        for (int i=0; i<jsonObj2.size(); i++){
            int num = 0;
            JSONObject AD_user = (JSONObject) jsonObj2.get(Integer.toString(i));
            String AD_user_id = AD_user.get("userId").toString();
            for (int j=0; j<jsonarry1.size(); j++){
                JSONObject API_user = (JSONObject) jsonarry1.get(Integer.parseInt(Integer.toString(j)));
                String API_user_id = API_user.get("userId").toString();
                if (API_user_id.equals(AD_user_id)){
                    num = num + 1;
                }
            }
            if (num == 0){
                AD_user.put("roles", roles);
                System.out.println(AD_user);
                update_data.put(num, AD_user);

                URL url = null;
                String readLine = null;
                StringBuilder buffer = null;
                OutputStream outputStream = null;
                BufferedReader bufferedReader = null;
                BufferedWriter bufferedWriter = null;
                HttpURLConnection urlConnection = null;
                String api_url = System.getenv("API_URL");

                int connTimeout = 5000;
                int readTimeout = 3000;

                String sendData = AD_user.toString();                        // 대다수의 경우 JSON 데이터 사용
                String apiUrl = api_url + "/api/account/" + accountSeq + "/user";    // 각자 상황에 맞는 IP & url 사용

                try
                {
                    url = new URL(apiUrl);

                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setConnectTimeout(connTimeout);
                    urlConnection.setReadTimeout(readTimeout);
                    urlConnection.setRequestProperty("user-id", "1");
                    urlConnection.setRequestProperty("user-role", "ADMIN");
                    urlConnection.setRequestProperty("Content-Type", "application/json;");
                    urlConnection.setDoOutput(true);
                    urlConnection.setInstanceFollowRedirects(true);

                    outputStream = urlConnection.getOutputStream();

                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    bufferedWriter.write(sendData);
                    bufferedWriter.flush();

                    buffer = new StringBuilder();
                    if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                        while((readLine = bufferedReader.readLine()) != null)
                        {
                            buffer.append(readLine).append("\n");
                        }
                    }
                    else
                    {
                        buffer.append("\"code\" : \""+urlConnection.getResponseCode()+"\"");
                        buffer.append(", \"message\" : \""+urlConnection.getResponseMessage()+"\"");
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
                        if (bufferedWriter != null) { bufferedWriter.close(); }
                        if (outputStream != null) { outputStream.close(); }
                        if (bufferedReader != null) { bufferedReader.close(); }
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }

                new_userid = ((getAPI) apiUser_get).get_API(accountSeq);
                ((userInactive) apiUser_inactive).user_Inactive(new_userid, update_data, accountSeq);
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