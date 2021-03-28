package com.luwanqiang.crm.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrintJson {
	
	//将boolean值解析为json串
	public static void printJsonFlag(HttpServletResponse response,boolean flag){
		
		Map<String,Boolean> map = new HashMap<String,Boolean>();
		map.put("success",flag);
		
		ObjectMapper om = new ObjectMapper();
		try {
			//{"success":true}
			String json = om.writeValueAsString(map);
			PrintWriter printWriter = response.getWriter();

			printWriter.print(json);
			printWriter.flush();
			printWriter.close();

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	//将对象解析为json串
	public static void printJsonObj(HttpServletResponse response,Object obj){
		
		/*
		 * 
		 * Person p
		 * 	id name age
		 * {"id":"?","name":"?","age":?}
		 * 
		 * List<Person> pList
		 * [{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?}...]
		 * 
		 * Map
		 * 	key value
		 * {key:value}
		 * 
		 * 
		 */
		
		ObjectMapper om = new ObjectMapper();
		try {
			String json = om.writeValueAsString(obj);

			PrintWriter printWriter = response.getWriter();

			printWriter.print(json);
			printWriter.flush();
			printWriter.close();

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
}























