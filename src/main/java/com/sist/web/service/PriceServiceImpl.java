package com.sist.web.service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PriceServiceImpl implements PriceService{
	private static HttpClient client;
	private static Map<String,Integer> priceList;
	private static ObjectMapper mapper;
	
	public PriceServiceImpl()
	{
		client= HttpClient.newHttpClient();
		priceList = new HashMap<String,Integer>();
		mapper = new ObjectMapper();
	}
	
	@Override
	public Map<String, Integer> prices() {
			
		String url = "https://apis.data.go.kr/B552845/recent/price"
		                + "?serviceKey=UwcCFRIOMm14Mgr0x49cYE4bVTy%2F2XeZn%2B21y0OA3dbl6gY9BIMb5Xe9OsA71qE4rgHctX18deBa4YLcb%2BtLIg%3D%3D"
		                + "&returnType=json"
		                + "&pageNo=1"
		                + "&numOfRows=452"
		                + "&selectable=item_nm,exmn_dd_prc";
		
		HttpResponse<String> response = getHttp(url);

        if (response != null) 
        {
           
            System.out.println(response.body());
            
            try 
            {
            	JsonNode priceData = mapper.readTree(response.body()).path("response").path("body").path("items").path("item");
            	
				for(JsonNode nodes : priceData)
		        {
					
					int price = nodes.get("exmn_dd_prc").asInt();
					String item = nodes.get("item_nm").asText();
					
					priceList.put(item,price);
		        }
			}
            catch (Exception ex) 
            {
				ex.printStackTrace();
			}
            
        }
        //if(priceList.containsKey("s"))
		 
        return priceList;
	}

    public HttpResponse<String> getHttp(String url) {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> httpResponse = null;

        try {

            httpResponse = client.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return httpResponse;
    }
    
 
    @Override
    public String ai(String a) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                + "gemini-3.6-flash:generateContent?key="
                + "";

        String body = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": "%s"
                    }
                  ]
                }
              ]
            }
            """.formatted(a.replace("\"", "\\\""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {

            System.out.println("Gemini 요청 시작");

            long start = System.currentTimeMillis();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            long end = System.currentTimeMillis();

            System.out.println(
                    "Gemini 응답 시간 : "
                    + (end - start)
                    + "ms"
            );

            if (response.statusCode() != 200) {
                System.out.println("Gemini 오류 : " + response.body());
                return null;
            }

            JsonNode root = mapper.readTree(response.body());

            return root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
