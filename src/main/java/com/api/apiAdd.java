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
 *  - Class : apiAdd
 *  - Description : JSONObject add_API()에서 userId, Datas, accountSeq를 받아서 사용
 * @section : 수정 정보
 *  - 수정일 : 2023.05.15
 *  - 수정자 : 정현서
 *  - 수정 내용 : 최초생성
 */

public class apiAdd extends Thread {

    /**
     * 특정 사용자 부서를 추가합니다.
     *
     * @param userId cocktail-server에서 가져온 사용자 정보를 참조합니다.
     * @param Datas AD-Server에서 가져온 사용자 정보 데이터들을 참조합니다.
     * @param accountSeq getCluster의 accountSeq 정보를 참조합니다.
     * @throws ParseException
     */

    public static void add_API(JSONObject userId, JSONObject Datas, String accountSeq) throws ParseException {
        //JSONObject
        JSONObject new_id;

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
            JSONObject AD = (JSONObject) jsonObj2.get(Integer.toString(i));
            String AD_id = AD.get("userId").toString();
            for (int j=0; j<jsonarry1.size(); j++){
                JSONObject API_user = (JSONObject) jsonarry1.get(Integer.parseInt(Integer.toString(j)));
                String API_user_id = API_user.get("userId").toString();
                if (API_user_id.equals(AD_id)){
                    count = count + 1;
                }
            }
            if (count == 0){
                AD.put("roles", roles);
                System.out.println(AD);
                update.put(count, AD);

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

                String send = AD.toString();
                String apiUrl = api_url + "/api/account/" + accountSeq + "/user";

                try
                {
                    url = new URL(apiUrl);

                    //method : POST
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

                new_id = ((getAPI) apiUser_get).get_API(accountSeq);
                ((userInactive) apiUser_inactive).user_Inactive(new_id, update, accountSeq);
            }
        }
    }
}