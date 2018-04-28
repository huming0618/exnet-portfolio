package exnet.portfolio.exapi;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

//https://github.com/OKCoin/rest/blob/master/java/src/com/okcoin/rest/HttpUtilManager.java
//https://spring.io/guides/gs/scheduling-tasks/
public class ExAPIHttpClient{
    private final static String API_KEY_NAME = "api_key";
    private final static String SIGN_NAME = "sign";
    private final static String SYMBOL_NAME = "symbol";
    private final static String STATUS_NAME = "status";
    private final static String CURRENT_PAGE_NAME = "current_page";
    private final static String PAGE_LENGTH_NAME = "page_length";

    private final static String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

    private final String apiKey = "aa";
    private final String secretKey = "dd";
    private final String APIUrl = "https://www.okex.com";

    private final String accountAPIPath = "/api/v1/userinfo.do";
    private final String orderHistoryAPIPath = "/api/v1/order_history.do";

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
    '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public ExAPIHttpClient(){

    }

    private String SignRequestData(List<NameValuePair> data) throws Exception{
        StringBuilder sign = new StringBuilder();
        
        data.sort((a,b)-> a.getName().compareTo(b.getName()));

        data.forEach(item->{
            sign.append(item.getName() + "=" + item.getValue() + "&");
        });

        String combined = sign.toString() + "secret_key=" + this.secretKey;

        //System.out.println("with sign " +  combined);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(combined.getBytes());

        byte[] bytes = md5.digest();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(HEX_DIGITS[(bytes[i] & 0xf0) >> 4] + ""
                    + HEX_DIGITS[bytes[i] & 0xf]);
        }
        //System.out.println(buffer.toString());
        return buffer.toString();
    }

    private JSONObject RequestHTTPAPI(String apiPath, List<NameValuePair> params) throws Exception{
        JSONObject result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
 
        HttpPost request = new HttpPost(this.APIUrl + apiPath);
        request.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_VALUE);

        System.out.println("executing request " + request.getURI());

        //StringEntity params = new StringEntity("{'api_key': 'aacc4dcc-92af-4f25-a14d-a56512f6f4a9', 'sign': '7B16F821CD80772FB0BC061F50CEEE75'}");
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
  
        CloseableHttpResponse response = httpclient.execute(request);
            //System.out.println("============="+response.toString());
        try {
            HttpEntity entity = response.getEntity();
            //System.out.println(response.getStatusLine());
            //result = response.getStatusLine().toString();
            if (entity != null) {
                //System.out.println("Response content length: " + entity.getContentLength());
                //System.out.println("Response content: " + EntityUtils.toString(entity));
                result = new JSONObject(new JSONTokener(EntityUtils.toString(entity))); 
            }
        } finally {
            response.close();
        }

        try {
            httpclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject GetOrderHistory(String symbolPair) throws Exception{
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(API_KEY_NAME, this.apiKey));
        params.add(new BasicNameValuePair(SYMBOL_NAME, symbolPair));
        params.add(new BasicNameValuePair(STATUS_NAME, "1"));
        params.add(new BasicNameValuePair(CURRENT_PAGE_NAME, "0"));
        params.add(new BasicNameValuePair(PAGE_LENGTH_NAME, "200"));
        params.add(new BasicNameValuePair(SIGN_NAME, this.SignRequestData(params)));

        return this.RequestHTTPAPI(this.orderHistoryAPIPath, params);
    }

    public JSONObject GetSpotAssets() throws Exception{
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(API_KEY_NAME, this.apiKey));
        params.add(new BasicNameValuePair(SIGN_NAME, this.SignRequestData(params)));

        return this.RequestHTTPAPI(this.accountAPIPath, params);
    }
}