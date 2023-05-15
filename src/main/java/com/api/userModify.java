package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class userModify extends Thread {
    public static void user_Modify(JSONObject userid, JSONObject AD_data, String accountSeq) throws ParseException {
        Thread userInactive = new userInactive();
        Thread getADUsers = new getADUsers();
        JSONObject allUsers = ((getADUsers) getADUsers).get_ADUsers();
        JSONParser parser = new JSONParser();
        JSONArray jsonArr1 = (JSONArray) parser.parse(userid.toString());
        JSONObject updateData = new JSONObject();
        JSONArray roles = new JSONArray();
        roles.add("DEVOPS");
        int count = 0;

        for (Object obj : jsonArr1) {
            JSONObject apiUser = (JSONObject) obj;
            String apiUserId = apiUser.get("userId").toString();
            String apiUserDepartment = apiUser.containsKey("userDepartment") ? apiUser.get("userDepartment").toString() : "null";

            for (Object adUserObj : allUsers.values()) {
                JSONObject adUser = (JSONObject) adUserObj;
                String adUserDepartment = adUser.get("userDepartment").toString();
                String adUserId = adUser.get("userId").toString();

                if (apiUserId.equals(adUserId) && !apiUserDepartment.equals(adUserDepartment)) {
                    System.out.println(adUser);
                    updateData.put(count, adUser);
                    adUser.put("roles", roles);
                    count++;

                    try {
                        URL url = new URL(System.getenv("API_URL") + "/api/account/" + accountSeq + "/user/" + apiUser.get("userSeq"));
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("PUT");
                        connection.setRequestProperty("user-id", "1");
                        connection.setRequestProperty("user-role", "ADMIN");
                        connection.setRequestProperty("Content-Type", "application/json;");
                        connection.setDoOutput(true);

                        try (OutputStream outputStream = connection.getOutputStream();
                             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))) {
                            writer.write(adUser.toString());
                            writer.flush();
                        }

                        StringBuilder response = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line).append("\n");
                            }
                        }

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            System.out.println("결과: " + response.toString());
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

        ((userInactive) userInactive).user_Inactive(userid, updateData, accountSeq);
    }
}
