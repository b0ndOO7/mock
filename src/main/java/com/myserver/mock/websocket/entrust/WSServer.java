package com.myserver.mock.websocket.entrust;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

@Controller
@RestController
@ServerEndpoint(value = "/websocket")
public class WSServer {
	/** 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。 */
	private static int onlineCount = 0;

	private static boolean isFirst = true;

	/** concurrent包的线程安全Set，用来存放每个客户端对应的CumWebSocket对象。 */
	private static CopyOnWriteArraySet<WSServer> webSocketSet = new CopyOnWriteArraySet<WSServer>();
	/** 与某个客户端的连接会话，需要通过它来给客户端发送数据 */
	private Session session;

	private Map<String, String> commonMap = new HashMap<String, String>();

	private static final Logger log = LoggerFactory.getLogger(WSServer.class);

//	@Value("{sendmsg.rate}")
//	private String rate;
	
	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		webSocketSet.add(this);
		addOnlineCount();
		try {
			commonMap = getEnvParam();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("新连接接入。当前在线人数为：" + getOnlineCount());
		// try {
		// sendMessage("假装有内容");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		// 从set中删除
		webSocketSet.remove(this);
		// 在线数减1
		subOnlineCount();
		log.info("有连接关闭。当前在线人数为：" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用
	 * 
	 * @param message
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("客户端发送的消息：" + message);

		WSServer.isFirst = true;
		// sendAll(message);
		try {
			sendMessage(message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 暴露给外部的群发
	 * 
	 * @param message
	 * @throws IOException
	 */
	public static void sendInfo(String message) throws IOException {
		sendAll(message);
	}

	/**
	 * 群发
	 * 
	 * @param message
	 */
	private static void sendAll(String message) {

		Arrays.asList(webSocketSet.toArray()).forEach(item -> {
			WSServer wsServer = (WSServer) item;
			// 群发
			try {
				wsServer.sendMessage(message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
	}

	/**
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		log.info("异常!!!");
		error.printStackTrace();
	}

	/**
	 * 减少在线人数
	 */
	private void subOnlineCount() {
		WSServer.onlineCount--;
	}

	/**
	 * 添加在线人数
	 */
	private void addOnlineCount() {
		WSServer.onlineCount++;
	}

	/**
	 * 当前在线人数
	 * 
	 * @return
	 */
	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	/**
	 * 消息组装
	 */
	private static String getTradeJson(long tm, String channel, String marketId) {

		Map<String, Object> channelMap = new HashMap<>();
		ArrayList<Map<String, Object>> tradeList = new ArrayList<Map<String, Object>>();

		channelMap.put("channel", channel); //"etc_usdt_trades"
		channelMap.put("marketId", marketId); //"2000"
		channelMap.put("timestamp", tm / 1000);
		channelMap.put("dataType", "lastTrades");

		for (int i = 0; i < 2; i++) {
			Map<String, Object> tmpMap = new HashMap<>();
			tmpMap.put("date", tm / 1000);
			tmpMap.put("amount", String.valueOf(new Random().nextInt(10)) + "."
					+ String.valueOf(new Random().nextInt(10)) + "0000000");
			tmpMap.put("price", String.valueOf(new Random().nextInt(10)) + "."
					+ String.valueOf(new Random().nextInt(10)) + "0000000"); // 10.80000000
			tmpMap.put("tid", "T64488" + tm + i);
			tmpMap.put("tradeType", i % 2 == 0 ? "bid" : "ask");
			tradeList.add(tmpMap);
		}

		channelMap.put("data", tradeList);

		return JSON.toJSONString(channelMap);
	}

	// tapeData
	private static String getTapeDataJson(long tm, int isFull, String channel, String marketId) {

		Map<String, Object> channelMap = new HashMap<>();
		ArrayList<String[]> tapeListask = new ArrayList<String[]>();
		ArrayList<String[]> tapeListbid = new ArrayList<String[]>();

		channelMap.put("channel", channel); //etc_usdt_entrust
		channelMap.put("marketId", marketId); //2000
		channelMap.put("timestamp", tm / 1000);
		channelMap.put("full", isFull);

		int j = isFull == 1 ? 50 : 5;
		for (int i = 0; i < j; i++) {
			String[] tradeask = new String[2];
			String[] tradebid = new String[2];
			tradeask[0] = String.valueOf(new Random().nextInt(10)) + "." + String.valueOf(new Random().nextInt(10))
					+ "0000000";
			tradeask[1] = String.valueOf(new Random().nextInt(10)) + "." + String.valueOf(new Random().nextInt(10))
					+ "0000000";
			tradebid[0] = String.valueOf(new Random().nextInt(10)) + "." + String.valueOf(new Random().nextInt(10))
					+ "0000000";
			tradebid[1] = String.valueOf(new Random().nextInt(10)) + "." + String.valueOf(new Random().nextInt(10))
					+ "0000000";
			tapeListask.add(tradeask);
			tapeListbid.add(tradebid);
		}

		channelMap.put("asks", tapeListask);
		channelMap.put("bids", tapeListbid);

		return JSON.toJSONString(channelMap);
	}

	/**
	 * 发送信息
	 * 
	 * @param message
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void sendMessage(String message) throws IOException, InterruptedException {

		int msgRate = Integer.parseInt(commonMap.get("sendmsg.rate"));
		String marketId = commonMap.get("sendmsg.marketId");
		// {"event":"subscribe","content":"tapeData","marketId":"90"}
		if (message.indexOf("tapeData") != -1 && message.indexOf(marketId) != -1) {
			long t = System.currentTimeMillis();
			String msg_full = getTapeDataJson(t, 1, commonMap.get("sendmsg.channel")+"_entrust", marketId); //getTapeDataJson(t, 1);
			this.session.getBasicRemote().sendText(msg_full); // 第一次全量
			System.out.println("msg_full send:" + msg_full);
			WSServer.isFirst = false;
			while (!WSServer.isFirst) {
				// 频率控制
				long now = System.currentTimeMillis();
				String msg = getTapeDataJson(t, 0, commonMap.get("sendmsg.channel")+"_entrust", marketId);
				while (now - t < 1000L) {
					for (int i = 0; i < msgRate; i++) {
						// 增量消息发送
						this.session.getBasicRemote().sendText(msg);
						System.out.println("now:" + String.valueOf(now) + ",t:" + t + " msg send:" + msg);
					}
					now = System.currentTimeMillis();
					if (now - t < 1000L) {
						Thread.sleep(1000 - (now - t));
					}
					now = System.currentTimeMillis();
				}
				t = System.currentTimeMillis();
			}
		}

		// {"event":"subscribe","content":"transactions","marketId":"90"}
		if (message.indexOf("transactions") != -1 && message.indexOf(marketId) != -1) {
			long t = System.currentTimeMillis();

			WSServer.isFirst = false;
			while (!WSServer.isFirst) {
				// 频率控制
				long now = System.currentTimeMillis();
				String msg = getTradeJson(now, commonMap.get("sendmsg.channel")+"_trades", marketId);

				while (now - t < 1000L) {
					for (int i = 0; i < msgRate; i++) {
						// 增量消息发送
						this.session.getBasicRemote().sendText(msg);
						System.out.println("now:" + String.valueOf(now) + ",t:" + t + " msg send:" + msg);
					}
					now = System.currentTimeMillis();
					if (now - t < 1000L) {
						Thread.sleep(1000 - (now - t));
					}
					now = System.currentTimeMillis();
				}
				t = System.currentTimeMillis();
			}
		}
	}

	private Map<String, String> getEnvParam() throws IOException {

		Map<String, String> tMap = new HashMap<>();

		Properties properties = new Properties();
		FileInputStream inputStream = new FileInputStream("application.properties");
		properties.load(inputStream);

		for (String keyName : properties.stringPropertyNames()) {
			String value = properties.getProperty(keyName);
			tMap.put(keyName, value);
			System.out.println("key:" + keyName + ",value:" + value);
		}

		return tMap;
	}


	public static void main(String[] args) {
		// System.out.println(getTradeJson(System.currentTimeMillis()));
		System.out.println(getTapeDataJson(System.currentTimeMillis(), 1, "eth_usdt_entrust", "2000"));

	}

}
