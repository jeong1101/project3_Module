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
 *  - Class : userModify
 *  - Description : JSONObject user_Modify()에서 userId, Datas, accountSeq를 벋아서 사용
 * @section : 수정 정보
 *  - 수정일 : 2023.05.15
 *  - 수정자 : 정현서
 *  - 수정 내용 : 최초생성
 */

public class userModify extends Thread {

    /**
     *
     * @param userId cocktail-server에서 가져온 사용자 정보를 참조합니다.
     * @param Datas AD-Server에서 가져온 사용자 정보 데이터들을 참조합니다.
     * @param accountSeq getCluster의 accountSeq 정보를 참조합니다.
     * @throws ParseException
     */

    public static void user_Modify(JSONObject userId, JSONObject Datas, String accountSeq) throws ParseException {
        // class
        Thread userInactive = new userInactive();
        Thread getADUsers = new getADUsers();
        JSONObject allUsers = ((getADUsers) getADUsers).get_ADUsers();
        JSONParser parser = new JSONParser();

        //JSONObject Update
        JSONObject jsonObj1 = (JSONObject) parser.parse(userId.toString());
        JSONObject jsonObj2 = (JSONObject) parser.parse(allUsers.toString());
        JSONArray jsonarry1 = (JSONArray) jsonObj1.get("result");

        //update
        JSONObject update = new JSONObject();
        JSONArray roles = new JSONArray();
        int count = 0;
        roles.add(0, "DEV"); // DEV 추가

        for (Object obj : jsonarry1) {
            //API 사용자 정보 정보
            JSONObject apiUser = (JSONObject) obj;
            String apiUserId = apiUser.get("userId").toString();
            String apiUserDepartment = apiUser.containsKey("userDepartment") ? apiUser.get("userDepartment").toString() : "null"; 

            //AD 사용자 정보
            for (Object adUserObj : allUsers.values()) {
                JSONObject adUser = (JSONObject) adUserObj;
                String adUserDepartment = adUser.get("userDepartment").toString();
                String adUserId = adUser.get("userId").toString();

                //업데이트할 사용자 정보, AD정보
                if (apiUserId.equals(adUserId) && !apiUserDepartment.equals(adUserDepartment)) {
                    System.out.println(adUser);
                    update.put(count, adUser);
                    adUser.put("roles", roles);
                    count++;

                    try {
                        //url
                        URL url = new URL(System.getenv("COCKTAIL_API") + "/api/account/" + accountSeq + "/user/" + apiUser.get("userSeq"));
                        //conn
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        //method 방식 : PUT
                        connection.setRequestMethod("PUT");

                        //header setting
                        connection.setRequestProperty("user-id", "1");
                        connection.setRequestProperty("user-role", "ADMIN");
                        connection.setRequestProperty("Content-Type", "application/json;");
                        connection.setDoOutput(true);

                        // 정보 전송
                        try (OutputStream outputStream = connection.getOutputStream();
                             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))) {
                            writer.write(adUser.toString());
                            writer.flush();
                        }

                        StringBuilder sb = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line).append("\n");
                            }
                        }

                        if (connection.getResponseCode() == 200) {
                            System.out.println("결과: " + sb.toString());
                        } else {
                            System.out.println("\"code\": \"" + connection.getResponseCode() + "\"");
                            System.out.println("\"message\": \"" + connection.getResponseMessage() + "\"");
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        //비활성
        ((userInactive) userInactive).user_Inactive(userId, update, accountSeq);
    }
}
