package exnet.portfolio.domain;

import exnet.portfolio.exapi.ExAPIHttpClient;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class Worker{
    private final Logger LOG = LoggerFactory.getLogger("Record");

    private ExAPIHttpClient client;

    public Worker(){
        this.client = new ExAPIHttpClient();
    }

    private Map<String,Float> GetAccountAsset(){
        Map<String,Float> assetList = null;
        try{
            JSONObject assetResult = this.client.GetSpotAssets();
            JSONObject free = (JSONObject)assetResult.query("/info/funds/free");

            assetList = free.toMap()
                .entrySet()
                .stream()
                .filter(x->!((String)x.getValue()).equals("0"))
                .collect(Collectors.toMap(x -> x.getKey(), x -> Float.parseFloat((String)x.getValue())));
            
            //System.out.println(free.toString());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return assetList;
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void Record(){
        try{
            Map<String,Float> asset = this.GetAccountAsset();
            //System.out.println());
            LOG.info((new JSONObject(asset)).toString());
            asset.keySet().forEach(symbol->{
                String pair = String.format("%s_usdt", symbol);
                try {
                    JSONObject ordersHistoryResult = this.client.GetOrderHistory(pair);
                    
                    if (ordersHistoryResult.has("orders")){
                        JSONArray orders = ordersHistoryResult.getJSONArray("orders");
                        if (orders.toList().size() > 0){
                            LOG.info(orders.toString());
                            //System.out.println(orders.toString());
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