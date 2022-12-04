package APIpackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ApiExplorer {
    public static void main(String[] args) throws IOException {
       // 1. URL을 만들기 위한 StringBuilder.
        StringBuilder urlBuilder = new StringBuilder("https://api.odcloud.kr/api/15092231/v1/uddi:f485c10f-f5d2-4a00-a993-b85d929565ec"); /*URL*/
        // 2. 오픈 API의요청 규격에 맞는 파라미터 생성, 발급받은 인증키.
        urlBuilder.append("?page=1"); /*페이지 번호*/
        urlBuilder.append("&perPage=10"); /*한 페이지 결과 수*/
        
        urlBuilder.append("&serviceKey=Ar6tat092yzvcQYUO8wCqMtbnxBjQwCGjNL4BsE6kmpaWXECc153R13Nj2eUkD/HQw3Zi9YroLnnTzc4q1L/lw==");
        // 3. URL 객체 생성.
        try {
        	URL url = new URL(urlBuilder.toString());
        	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        	conn.setRequestMethod("GET");
        	conn.setRequestProperty("Content-type", "application/json");
        	conn.setDoOutput(true);
        	// 8. 전달받은 데이터를 BufferedReader 객체로 저장.
        	StringBuffer sb = new StringBuffer();
        	
        	
        	// 9. 저장된 데이터를 라인별로 읽어 StringBuilder 객체로 저장.
        	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        	while(br.ready()) {
        		sb.append(br.readLine());
        	}
        	String tostr = sb.toString();
    	    Properties properties = new Properties();
            String str = "unicodedString=" + tostr;
            StringReader stringReader = new StringReader(str);
            properties.load(stringReader);
            System.out.println(properties.getProperty("unicodedString"));

            
//            JSONParser jsonParser = new JSONParser();
//            JSONObject jsonObject = (JSONObject)jsonParser.parse(result);
//            System.out.println(jsonObject.get("data"));       
            br.close();
            }
       
        catch(Exception e) {
        	e.printStackTrace();
            }

        
        // 11. 전달받은 데이터 확인.
        
    }
}
