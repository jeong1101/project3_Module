package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @section 작성정보
 *  - author 개발3실
 *  - version 1.0
 *  - since 2023.05.15
 * @section Class
 *  - Class : userInactive
 *  - Description : JSONObject user_Inactive()에서 userId, Datas, accountSeq를 벋아서 사용
 * @section : 수정 정보
 *  - 수정일 : 2023.05.15
 *  - 수정자 : 정현서
 *  - 수정 내용 : 최초생성
 */

public class userInactive extends Thread {

    /**
     *
     * 특정 사용자의 권한을 비활성화 합니다.
     *
     * @param userId cocktail-server에서 가져온 사용자 정보를 참조합니다.
     * @param Datas AD-Server에서 가져온 사용자 정보 데이터들을 참조합니다.
     * @param accountSeq getCluster의 accountSeq 정보를 참조합니다.
     * @throws ParseException
     */

    public static void user_Inactive(JSONObject userId, JSONObject Datas, String accountSeq) throws ParseException {
        //JSON 데이터 파싱 위한 JSONParser
        JSONParser parser = new JSONParser();
        
        // 사용자 id정보
        JSONObject jsonObject = (JSONObject) parser.parse(userId.toString());
        JSONArray jsonArray = (JSONArray) jsonObject.get("result");
        
        for (int i=0; i<jsonArray.size(); i++){
            JSONObject user = (JSONObject) jsonArray.get(i);
            String api_Id = user.get("userId").toString();
            
            // AD  데이터 파싱 및 비교
            JSONObject jsonObject1 = (JSONObject) parser.parse(Datas.toString());
            for (Object ad_obj : jsonObject1.values()){
                JSONObject ad = (JSONObject) ad_obj;
                String ad_Id = ad.get("userId").toString();
                
                if (api_Id.equals(ad_Id)){
                    // 사용자 비활성화 정보 생성
                    JSONObject inactive = new JSONObject();
                    inactive.put("inactiveYn", "N");
                    inactive.put("userSeq",user.get("userSeq"));
                    System.out.println(inactive);
                    
                    //API 호출 위한 URL 연결
                    //COCKTAIL_API
                    URL url = null;
                    String apiUrl = System.getenv("COCKTAIL_API") + "/api/account" + accountSeq + "/user" + user.get("userSeq") + "/inactive";

                    // buffer
                    StringBuilder sb = null;
                    BufferedReader br = null;
                    BufferedWriter bw = null;
                    String line = null;
                    OutputStream out = null;


                    // HttpURLConnection
                    HttpURLConnection conn = null;
                    
                    //데이터 전송
                    String send = inactive.toString();

                    try {
                        // conn
                        url = new URL(apiUrl);
                        conn = (HttpURLConnection) url.openConnection();

                        //method 방식 : PUT
                        conn.setRequestMethod("PUT");

                        //타임 아웃 설정 3초
                        conn.setConnectTimeout(3000);
                        conn.setReadTimeout(3000);

                        //header setting
                        conn.setRequestProperty("user-id", "136");
                        conn.setRequestProperty("user-role", "SYSTEM");
                        conn.setRequestProperty("Content-type","application/json");
                        conn.setDoOutput(true);

                        out = conn.getOutputStream();

                        bw = new BufferedWriter(new OutputStreamWriter(out));
                        bw.write(send);
                        bw.flush();

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
                            if (bw != null){
                                bw.close();
                            }
                            if (out != null){
                                out.close();
                            }
                            if (conn != null){
                                conn.disconnect();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }//catch
                    }

                    System.out.println("sb => " + sb.toString());

                }
            }
        }
    }
}
