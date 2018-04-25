package exnet.portfolio.exapi;

import java.security.MessageDigest;

import org.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ExAPIHttpClient{
    private final String apiKey = "aacc4dcc-92af-4f25-a14d-a56512f6f4a9";
    private final String secretKey = "EE41AFD790FE117DCE7B8F51632A2FD7";
    private final String APIUrl = "https://www.okex.com";
    private final String accountAPIPath = "/api/v1/userinfo.do";

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
    '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public ExAPIHttpClient(){

    }

    private String SignRequestData(JSONObject data) throws Exception{
        StringBuilder sign = new StringBuilder();
        Iterable<String> keys = (Iterable<String>)data.keys();
        
        keys.forEach(key-> sign.append(key + "=" + data.getString(key) + "&"));
        String combined = sign.toString() + "secret_key=" + this.secretKey;

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(combined.getBytes());

        byte[] bytes = md5.digest();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            buffer.append(HEX_DIGITS[(bytes[i] & 0xf0) >> 4] + ""
                    + HEX_DIGITS[bytes[i] & 0xf]);
        }
        
        return buffer.toString();
    }

    public void GetSpotAssets(){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost request = new HttpPost(this.APIUrl + accountAPIPath);
            request.setHeader("Content-type", "application/x-www-form-urlencoded");

            System.out.println("executing request " + request.getURI());

            JSONObject requestData = new JSONObject();
            requestData.put("api_key", this.apiKey);
            requestData.put("sign", this.SignRequestData(requestData));
            
            CloseableHttpResponse response = httpclient.execute(request);
            
            //System.out.println("============="+response.toString());
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("--------------------------------------");
                // 打印响应状态
                System.out.println(response.getStatusLine());
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
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}