package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.BufferedReader;
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
     * 특정 부서의 AD 사용자 목록을 조회 합니다.
     * @return
     * @throws ParseException
     */
    public static JSONObject get_AD() throws ParseException {
        //AD_URL
        URL url = null;
        String adUrl = System.getenv("AD_URL");

        // 특정 부서 선태
        String Department = System.getenv("DEPARTMENT");
        String[] Department2 = Department.split(",");
        System.out.println(Arrays.toString(Department2));
        //System.out.println("Department" + Department);

        // buffer
        StringBuilder sb = null;
        BufferedReader br = null;

        // HttpURLConnection
        HttpURLConnection conn = null;
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
        //System.out.println(sb);

        //JSONParser
        JSONParser parser = new JSONParser();
        JSONArray arr = (JSONArray) parser.parse(sb.toString());
        JSONObject Datas = new JSONObject();

        int count = 0;
        for(int i=0; i<arr.size(); i++){
            JSONObject data = new JSONObject();
            JSONObject jsonObject = (JSONObject)arr.get(i);
            JSONObject jsonObject1 = (JSONObject)jsonObject.get("attributes");

            if(jsonObject.containsKey("username") && jsonObject.containsKey("firstName") && jsonObject.containsKey("lastName")){
                String username = jsonObject.get("username").toString();
                String firstName = jsonObject.get("firstName").toString();
                String lastName = jsonObject.get("lastName").toString();

                JSONArray jsonArray = (JSONArray) jsonObject1.get("LDAP_ENTRY_DN");

                if(jsonArray != null && jsonArray.size() > 0){
                    String dev = jsonArray.get(0).toString();
                    String[] arrStr = dev.split(",");
                    if (arrStr.length>1){
                        Department = arrStr[1].substring(3);
                        //System.out.println(department);
                    }
                }

                for (int j=0; j<Department2.length; j ++){
                    if (Department.equals(Department2[j])){
                        data.put("userId",username);
                        data.put("userName", lastName + firstName);
                        data.put("userDepartment", Department);
                        Datas.put(count, data);
                        count++;
                    }
                    //System.out.println(Datas);
                }

                //test
                System.out.println(username + lastName + firstName);
                System.out.println("department => " + Department);
            }
            else {
                System.out.println("=== 서버 응답이 안됨 ===");
            }
        }
        System.out.println("Datas => " + Datas);
        return  Datas; //get_AD
    }

}
