package com.myutils.stock;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.myutils.email.EmailUtils;
import com.myutils.http.HttpGet;

public class StockUtils {
	
	private static Logger log = LoggerFactory.getLogger(StockUtils.class);

	public static void main(String[] args) throws InterruptedException {
		String toMail = "317883803@qq.com";
		Integer buyNum = 620572960;
		boolean mailFlag = true;
//	    String host = "http://stock.market.alicloudapi.com/batch-real-stockinfo";
	    String appcode = "c5d3aa72c9a94f08ac95c5cbb5add09a";
//	    Map<String, String> headers = new HashMap<String, String>();
//	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//	    headers.put("Authorization", "APPCODE " + appcode);
//	    Map<String, String> querys = new HashMap<String, String>();
//	    querys.put("needIndex", "0");
//	    querys.put("stocks", "sh600050");
//
//	    String result = HttpGet.get(host, headers, querys);
//	    JSONObject jsonObject = JSONObject.parseObject(result);
	    for(int i=0; i < 3000; i++){
	    	try{
	    		JSONObject json = StockUtils.stocksAPI(appcode);
	    		if(json.getInteger("buy1_n") < buyNum && mailFlag){
//	    			EmailUtils.sendEmai(toMail, "stocks", appendStr(json));
	    			buyNum = (int) (buyNum * 0.90);
	    		}
	    		log.info("The alarm threshold："+buyNum);
	    	} catch (Exception e) {
			}
	    	Thread.sleep(5000);
	    }
	  
	}
	
	/**
	 * 昆明秀派的API
	 * @return
	 */
	public static JSONObject stocksAPI(String appcode){
		
		String host = "http://stock.market.alicloudapi.com/batch-real-stockinfo";
//	    String appcode = "c5d3aa72c9a94f08ac95c5cbb5add09a";
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("needIndex", "0");
	    querys.put("stocks", "sh600050");

	    String result = HttpGet.get(host, headers, querys);
	    JSONObject jsonObject = JSONObject.parseObject(result);
		JSONObject jsonjsonObject2 = jsonObject.getJSONObject("showapi_res_body");
		JSONArray jsonArray = jsonjsonObject2.getJSONArray("list");
		if (jsonArray.size() == 0){
			return null;
		}
		JSONObject json = jsonArray.getJSONObject(0);
		log.info(appendStr(json));
	    return json;
	}
	
	public static String appendStr(JSONObject json) {
		StringBuffer sb = new StringBuffer();
		sb.append("code:").append(json.getString("code"));
		sb.append("--buy1:").append(json.getString("buy1_m")).append("(").append(json.getString("buy1_n")).append(")");
		sb.append("--buy2:").append(json.getString("buy2_m")).append("(").append(json.getString("buy2_n")).append(")");
		sb.append("--buy3:").append(json.getString("buy3_m")).append("(").append(json.getString("buy3_n")).append(")");
		sb.append("--buy4:").append(json.getString("buy4_m")).append("(").append(json.getString("buy4_n")).append(")");
		sb.append("--buy5:").append(json.getString("buy5_m")).append("(").append(json.getString("buy5_n")).append(")");
		return sb.toString();
	}
	
}
