import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.apache.xerces.impl.dv.util.Base64;

import com.fasterxml.jackson.core.*;

/* search using Bing API */
/* argument: query
   return: ArrayList of Urls
*/
public class BingSearch {
    public static ArrayList<String> SearchUrls(final String title) throws Exception {
    	ArrayList<String> urls = new ArrayList<String>();
    	//Bing Search API URL
    	String url = "https://api.datamarket.azure.com/Bing/Search/Web";
    	//query text
    	String query = new String("\'\""+ title + "\"\'");
    	//API key
    	final String key = "YOUR KEY";
    	//response format (xml or json)
    	String format = "json";
    	//num of pages
    	int num = 50;
    	//skip pages
    	int skip = 0;
    	try {
    		//set the api key by base64 encoding(if the key is long "\n" needs to be removed)
    		String keyenc = Base64.encode((key + ":" + key).getBytes()).replaceAll("\n","");
    		while(skip<=1000){
    			// request url
    			URL requesturl = new URL(url + "?Query=" + URLEncoder.encode(query, "UTF-8") + "&$format=" + format + "&$top=" + num + "&$skip=" + skip);

    			HttpURLConnection con = (HttpURLConnection)requesturl.openConnection();
    			//basic authentication
    			con.setRequestProperty("Authorization", "Basic " + keyenc);

    			con.setRequestMethod("GET");
    			con.connect();

    			//output requested url
    			//System.out.println(requesturl);

    			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

    			//output the response and store it into json
    			String str;
        		String json = "";
    			while ((str = in.readLine()) != null) {
    				//System.out.println(str);
    				json += str;
    			}
    			in.close();

    			//get results until the search result is nil
    			if(json.indexOf("{\"d\":{\"results\":[]}}")>0){
    				System.out.println("nil");
    				break;
    			}
    			urls.addAll(parseUrlJson(json));
    			
    			skip += 50;
    		}    		
    	} catch (Exception e) {  
    		e.printStackTrace();  
    	}  

		return urls;
    }
    
    public static ArrayList<String> parseUrlJson(String json) {
    	ArrayList<String> urls = new ArrayList<String>();
    	try {
    		// generate JsonFactory
    		JsonFactory factory = new JsonFactory();
    		// create JsonParser
    		JsonParser parser = factory.createJsonParser(json);
    		
    		while (parser.nextToken() != JsonToken.END_OBJECT) {
    		    if (("results").equals(parser.getText())){
    		    	while(parser.nextToken() != JsonToken.END_ARRAY){
    		    	    while (parser.nextToken() != JsonToken.END_OBJECT) {
    		    	    	if("Url".equals(parser.getCurrentName())){
    		    	    		parser.nextToken();
    		    	    		urls.add(parser.getText());
    		    	    	}
    		    	    }
    		    	}
    		    }
    		}
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return urls;
    }
}
