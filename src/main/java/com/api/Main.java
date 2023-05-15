/*
package com.api;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {

        //모든 AD User
        JSONObject ad = getADUsers.get_ADUsers();
        //System.out.println(ad);

        // 특정 부서의 AD User
        JSONObject ad_dpt = getAD.get_AD();

        // 클러스터 seq
        JSONObject seq = getCluster.get_ClusterSeq();

        // getAPI
        //JSONObject api = getAPI.get_API();
        try {
            String accountSeq = "1";
            JSONObject result = getAPI.get_API(accountSeq);

            System.out.println(result);
        }catch (ParseException e){
            e.printStackTrace();
        }

        // inactive
        try {
            userInactive apiinactive = new userInactive();

            apiinactive.user_Inactive(userId,Datas,accountSeq);

        }catch (ParseException e){
            e.printStackTrace();
        }



    }
}*/
