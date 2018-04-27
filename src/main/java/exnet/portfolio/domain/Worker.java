package exnet.portfolio.domain;

import exnet.portfolio.exapi.ExAPIHttpClient;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Worker{
    private ExAPIHttpClient client;

    public Worker(){
        this.client = new ExAPIHttpClient();
    }

    private Map<String,Float> GetAccountAsset(){
        Map<String,Float> assetList = new HashMap<String,Float>();
        try{
            JSONObject assetResult = this.client.GetSpotAssets();
            JSONObject free = (JSONObject)assetResult.query("/info/funds/free");

            free.keySet().forEach(symbol->{
                Float amt = Float.parseFloat(free.getString(symbol));
                if (amt > 0){
                    assetList.put(symbol, amt);
                }
            });
            System.out.println(free.toString());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return assetList;
    }

    @Scheduled(fixedRate = 120 * 1000)
    public void Record(){
        
        try{
            Map<String,Float> asset = this.GetAccountAsset();

            asset.keySet().forEach(symbol->{
                String pair = String.format("%s_usdt", symbol);
                try {
                    JSONObject ordersHistoryResult = this.client.GetOrderHistory(pair);
                    
                    if (ordersHistoryResult.has("orders")){
                        JSONArray orders = ordersHistoryResult.getJSONArray("orders");
                        if (orders.toList().size() > 0){
                            System.out.println(orders.toString());
                        }
                    }
                    Thread.sleep(800);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            });

            // JSONObject ordersHistoryResult = this.client.GetOrderHistory("trx_usdt");
            // JSONArray orderList = ordersHistoryResult.getJSONArray("orders");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}