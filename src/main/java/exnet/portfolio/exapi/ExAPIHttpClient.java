package exnet.portfolio.exapi;

import java.security.MessageDigest;

import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

//https://github.com/OKCoin/rest/blob/master/java/src/com/okcoin/rest/HttpUtilManager.java
public class ExAPIHttpClient{
    private final String apiKey = "apiKey";
    private final String secretKey = "secretKey";
    private final String APIUrl = "https://www.okex.com";
    private final String accountAPIPath = "/api/v1/userinfo.do";

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
    '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public ExAPIHttpClient(){

    }

    private String SignRequestData(JSONObject data) throws Exception{
        StringBuilder sign = new StringBuilder();
        
        data.keySet().forEach(key-> sign.append(key + "=" + data.getString(key) + "&"));
        String combined = sign.toString() + "secret_key=" + this.secretKey;

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(combined.getBytes());

        byte[] bytes = md5.digest();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(HEX_DIGITS[(bytes[i] & 0xf0) >> 4] + ""
                    + HEX_DIGITS[bytes[i] & 0xf]);
        }
        System.out.println(buffer.toString());
        return buffer.toString();
    }

    public String GetSpotAssets(){
        String result = "";

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost request = new HttpPost(this.APIUrl + accountAPIPath);
            request.setHeader("Content-type", "application/x-www-form-urlencoded");

            System.out.println("executing request " + request.getURI());

            JSONObject requestData = new JSONObject();
            requestData.put("api_key", this.apiKey);
            requestData.put("sign", this.SignRequestData(requestData));
            
            StringEntity params = new StringEntity("{'api_key': 'aacc4dcc-92af-4f25-a14d-a56512f6f4a9', 'sign': '7B16F821CD80772FB0BC061F50CEEE75'}");
            request.setEntity(params);

            CloseableHttpResponse response = httpclient.execute(request);
            
            //System.out.println("============="+response.toString());
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("--------------------------------------");
                // 打印响应状态
                System.out.println(response.getStatusLine());
                result = response.getStatusLine().toString();
                if (entity != null) {
                    // 打印响应内容长度
                    //System.out.println("Response content length: " + entity.getContentLength());
                    // 打印响应内容
                    System.out.println("Response content: " + EntityUtils.toString(entity));
                }
                System.out.println("------------------------------------");
            } finally {
                response.close();
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        } 
        finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
        return result;
    }
}