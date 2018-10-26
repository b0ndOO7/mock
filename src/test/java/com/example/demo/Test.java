package com.example.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
 
public class Test extends WebSocketClient{
 
    public Test(String url) throws URISyntaxException {
        super(new URI(url));
    }
 
    @Override
    public void onOpen(ServerHandshake shake) {
        System.out.println("握手...");
//        for(Iterator<String> it=shake.iterateHttpFields();it.hasNext();) {
//            String key = it.next();
//            System.out.println(key+":"+shake.getFieldValue(key));
//        }
    }

    @Override
    public void onMessage(String paramString) {
        System.out.println("NOW：" + System.currentTimeMillis()/1000 + ",RespTime：" + paramString);
        //["E","80","1537182735","ETC_USDT","BID","5","7"]
//        if( paramString.indexOf("E")!=-1 || paramString.indexOf("T")!=-1 ){
//        	String respTime = paramString.substring(12, 22);
//        	System.out.println("respTime:" + respTime);
//        }
    }
    
    @Override
    public void onMessage(ByteBuffer bytes) {
    	
    	
    };
 
    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        System.out.println("关闭...");
        onMessage(paramString);
    }
 
    @Override
    public void onError(Exception e) {
        System.out.println("异常"+e);
        
    }
    
    public static void test() {
    	try {
            Test client = new Test("ws://test.zbg.com:28082/websocket");
            client.connect();
            while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                System.out.println("还没有打开");
            }
            System.out.println("建立websocket连接");
            client.send("{\"dataType\":\"80_KLINE_15M_ETC_USDT\",\"dataSize\":1000,\"action\":\"ADD\"}");
            client.send("{\"dataType\":\"80_ENTRUST_ADD_ETC_USDT\",\"dataSize\":50,\"action\":\"ADD\"}");
            client.send("{\"dataType\":\"80_TRADE_ETC_USDT\",\"dataSize\":20,\"action\":\"ADD\"}");
            client.send("{\"dataType\":\"ALL_TRADE_STATISTIC_24H\",\"dataSize\":1,\"action\":\"ADD\"}");
            
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } 
	}
    
    public static void main(String[] args) {
//    	test();
//    	for(int i=0; i<1; ++i){
//    		new Thread("" + i){
//    			public void run(){
//    				System.out.println("Thread: " + getName() + "running");
    				test();
//    			}
//    		}.start();
//    	}
        
    }

}