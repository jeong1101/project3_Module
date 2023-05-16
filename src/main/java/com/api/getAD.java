package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 *
 * @section 작성정보
 *  - author 개발3실
 *  - version 1.0
 *  - since 2023.05.15
 * @section Class
 *  - Class : getAD
 *  - Description : JSONObject get_AD() 이용한 Class
 * @section : 수정 정보
 *  - 수정일 : 2023.05.15
 *  - 수정자 : 정현서
 *  - 수정 내용 : 최초생성
 */


public class getAD extends Thread {

    /**
     * AD 서버에서 특정 부서 사용자 정보를 조회합니다.
     *
     * @param
     * @return JSONObject
     */
    public JSONObject get_AD() throws ParseException {
        URL url = null;
        String readLine = null;
        StringBuilder buffer = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        HttpURLConnection urlConnection = null;
        String ad_url = System.getenv("AD_URL");
        var pick = System.getenv("DEPARTMENT");
        String[] ArraysPick = pick.split(",");
        System.out.println(Arrays.toString(ArraysPick));

        int connTimeout = 5000;
        int readTimeout = 3000;

        String apiUrl = ad_url;    // 각자 상황에 맞는 IP & url 사용

        try
        {
            url = new URL(apiUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(connTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.setRequestProperty("Accept", "application/json;");

            buffer = new StringBuilder();
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
                while((readLine = bufferedReader.readLine()) != null)
                {
                    buffer.append(readLine);
                    //System.out.println(buffer);
                }
            }
            else
            {
                buffer.append("code : ");
                buffer.append(urlConnection.getResponseCode()).append("\n");
                buffer.append("message : ");
                buffer.append(urlConnection.getResponseMessage()).append("\n");
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
                if (bufferedReader != null) { bufferedReader.close(); }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        //System.out.println(buffer);
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray)parser.parse(buffer.toString());
        JSONObject AD_Datas = new JSONObject();
        int num = 0;


        for(int i=0; i<jsonArray.size(); i++){
            JSONObject AD_Data = new JSONObject();
            JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
            JSONObject jsonObject2 = (JSONObject)jsonObject1.get("attributes");
            if(jsonObject1.containsKey("username")){
                if(jsonObject1.containsKey("firstName")){
                    if(jsonObject1.containsKey("lastName")){
                        String username = jsonObject1.get("username").toString();
                        String firstName = jsonObject1.get("firstName").toString();
                        String lastName = jsonObject1.get("lastName").toString();

                        if(jsonObject1.containsKey("attributes")){
                            //LDAP = jsonObject2.get("LDAP_ENTRY_DN").toString();
                            JSONArray jsonArray1 = (JSONArray)jsonObject2.get("LDAP_ENTRY_DN");
                            String dev = jsonArray1.get(0).toString();
                            String[] ArraysStr = dev.split(",");
                            String department = ArraysStr[1].substring(3);
                            for(int k=0; k<ArraysPick.length; k++){
                                if (department.equals(ArraysPick[k])){
                                    AD_Data.put("userId", username);
                                    AD_Data.put("userName", lastName + firstName);
                                    AD_Data.put("userDepartment", department);
                                    //System.out.println("username: " + username + " / 이름: " + lastName + firstName + " / 부서: " + department);
                                    //System.out.println(AD_Data);
                                    AD_Datas.put(num, AD_Data);
                                    num = num + 1;
                                }
                            }
                        }else {
                            System.out.println("attributes를 입력해 주시기 바랍니다.");
                        }
                    }else {
                        System.out.println("lastName을 입력해 주시기 바랍니다.");
                    }
                }else {
                    System.out.println("firstName을 입력해 주시기 바랍니다.");
                }
            }else {
                System.out.println("username을 입력해 주시기 바랍니다.");
            }
        }
        //System.out.println(AD_Datas);
        return AD_Datas;
    }

}
