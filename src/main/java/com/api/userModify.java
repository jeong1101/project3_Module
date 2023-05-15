package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class userModify  extends Thread{
    public static void user_modify(JSONObject userid, JSONObject AD_data, String accountSeq) throws ParseException {
        Thread user_inactive = new userInactive();
        Thread getadusers = new getADUsers();
        JSONObject all_user = new JSONObject();
        all_user = ((getADUsers) getadusers).get_ADUsers();
        JSONParser parser = new JSONParser();
        JSONObject jsonObj1 = (JSONObject) parser.parse(userid.toString());
        JSONObject jsonObj2 = (JSONObject) parser.parse(all_user.toString());
        JSONArray jsonarry1 = (JSONArray) jsonObj1.get("result");
        JSONObject update_data = new JSONObject();
        JSONArray roles = new JSONArray();
        int num = 0;
        roles.add(0, "DEVOPS");

        for (int i=0; i<jsonarry1.size(); i++){
            JSONObject API_user = (JSONObject) jsonarry1.get(Integer.parseInt(Integer.toString(i)));
            String API_user_id = API_user.get("userId").toString();
            String API_user_department;
            API_user_department = "null";
            if(API_user.containsKey("userDepartment")){
                API_user_department = API_user.get("userDepartment").toString();
            }
            for (int j=0; j<jsonObj2.size(); j++){
                JSONObject AD_user = (JSONObject) jsonObj2.get(Integer.toString(j));
                String AD_user_department = AD_user.get("userDepartment").toString();
                String AD_user_id = AD_user.get("userId").toString();
                //System.out.println(AD_user);
                if(API_user_id.equals(AD_user_id)){
                    if (!API_user_department.equals(AD_user_department)){
                        System.out.println(AD_user);
                        update_data.put(num, AD_user);
                        AD_user.put("roles", roles);
                        num = num + 1;

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

                        String sendData = AD_user.toString();
                        String apiUrl = api_url + "/api/account/" + accountSeq + "/user/" + API_user.get("userSeq");

                        try
                        {
                            url = new URL(apiUrl);

                            urlConnection = (HttpURLConnection)url.openConnection();
                            urlConnection.setRequestMethod("PUT");
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

                        System.out.println("결과 : " + buffer.toString());
                    }
                }
            }
        }
        ((userInactive) user_inactive).user_Inactive(userid, update_data, accountSeq);
    }
}
