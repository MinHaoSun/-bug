package esJavaTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class TestEsClient2 {
	public static void main(String[] args) {
		TransportClient client = null;
		try {

			// 设置集群名称
			Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
			// 创建client
			client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.24.0.96"), 9300));
			// 搜索数据
			//GetResponse response = client.prepareGet("shuaihao", "folks", "andy").execute().actionGet();
			// 批量插入数据
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			
			int num = 0;
			double totalTime = 0;
			double startTime = 0;
			double endTime = 0;
			
			// 生成数据
			int id = 1;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while (true) {
				// 生成json格式数据
				JSONObject jsonObj1 = new JSONObject();// 创建json格式的数据
				JSONObject jsonObjArr = new JSONObject();
				String sysDate = df.format(new Date());
				UUID uuid = UUID.randomUUID();
				String s = uuid.toString();
				String msg = id + "," + sysDate + "," + s;
				String[] str = msg.split(",");
				jsonObjArr.put("id", str[0]);
				jsonObjArr.put("time", str[1]);
				jsonObjArr.put("name", str[2]);
				jsonObj1.put("index", jsonObjArr);
				System.out.println(jsonObj1);
				// 执行es
				bulkRequest.add(client.prepareIndex("m8", "info").setSource(jsonObj1.toString()));
				//System.out.println("start....");
				startTime = System.currentTimeMillis();
				bulkRequest.execute().actionGet();
				endTime = System.currentTimeMillis();
				//System.out.println(++num + "finish....");
				double time = endTime - startTime;
				System.out.println("临时：" + time / 1000);
				totalTime += time;
				if (id == 10000) {
					break;
				}
				System.out.println(id);
				id++;
				System.out.println("总时间为："+totalTime/1000);
			}
			System.out.println(".............."+totalTime/1000);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.close();
		}

	}

}
