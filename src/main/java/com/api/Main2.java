package com.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Main2 {
    public static void main(String[] args) {
        JSONObject userid;
        JSONObject AD_data;
        JSONObject seq_obj;
        String accountSeq;

        Thread getAPI = new getAPI();
        Thread getad = new getAD();
        Thread clusterSeq = new getCluster();
        Thread inactive = new userInactive();
        Thread add = new apiAdd();
        Thread modify = new userModify();

        try {
            // 클러스터 시퀀스 값
            seq_obj = ((getCluster) clusterSeq).get_ClusterSeq();

            // 클러스터 시퀀스에 대한 작업
            for(int i=0; i<seq_obj.size(); i++){
                accountSeq = seq_obj.get(i).toString();

                //cocktail api 서버에서 사용자 정보 가져옴
                userid = ((com.api.getAPI) getAPI).get_API(accountSeq);

                //ad 서버에서 사용자 정보 가져오기
                AD_data = ((getAD) getad).get_AD();

                //사용자 등록
                ((apiAdd)add ).add_API(userid, AD_data, accountSeq);

                // 사용자 부서 변경
                ((userModify) modify).user_modify(userid, AD_data, accountSeq);

            }

        }catch (Exception e){
            e.printStackTrace();
        }




    }
}