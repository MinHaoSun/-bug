package esJavaTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class TestEsClient3 {
	public static void main(String[] args) {
		TransportClient client = null;
		try {

			// 设置集群名称
			Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
			// 创建client
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.24.0.96"), 9300));
			// 搜索数据
			// GetResponse response = client.prepareGet("shuaihao", "folks",
			// "andy").execute().actionGet();
			// 批量插入数据
			BulkRequestBuilder bulkRequest = client.prepareBulk();

			int num = 0;
			double totalTime = 0;
			double startTime = 0;
			double endTime = 0;

			// 生成数据
			int id = 1;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Map<String, Object> hashMap  = new HashMap<String, Object>();
			for (int i = 1; i <= 1000000; i++) {
//				JSONObject jsonObj1 = new JSONObject();// 创建json格式的数据
//				JSONObject jsonObjArr = new JSONObject();
//				String sysDate = df.format(new Date());
				UUID uuid = UUID.randomUUID();
				String s = uuid.toString();
//				String msg = id + "," + sysDate + "," + s;
//				String[] str = msg.split(",");
//				jsonObjArr.put("id", str[0]);
//				jsonObjArr.put("time", str[1]);
//				jsonObjArr.put("name", str[2]);
//				jsonObj1.put("index", jsonObjArr);
				hashMap.put("title",s);
                hashMap.put("describe", "测试123");  
                hashMap.put("author", i);  

				bulkRequest.add(client.prepareIndex("m2", "info").setSource(hashMap));
				// 每1000条提交一次
				if (i % 100000 == 0) {
					startTime = System.currentTimeMillis();
					bulkRequest.execute().actionGet();
					endTime = System.currentTimeMillis();
					double time = endTime - startTime;
					System.out.println("临时：" + time / 1000);
					totalTime += time;
					System.out.println("总时间为：" + totalTime / 1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}

	}

}
