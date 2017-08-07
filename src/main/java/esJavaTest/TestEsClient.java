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
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class TestEsClient {

	public static void createData() {
		int id = 1;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// pw.write("nihao");
		while (true) {
			String sysDate = df.format(new Date());
			UUID uuid = UUID.randomUUID();
			String s = UUID.randomUUID().toString();
			String msg = id + "," + sysDate + "," + s;
			
			if (id == 100000000) {
				break;
			}
			id++;
			System.out.println(id);
		}
	}

	public static void main(String[] args) {
		try {

			// ���ü�Ⱥ����
			Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
			// ����client
			TransportClient client = new PreBuiltTransportClient(settings)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("172.24.0.96"), 9300));
			// ��������
			GetResponse response = client.prepareGet("shuaihao", "folks", "andy").execute().actionGet();
			// ������������
			File article = new File("D:\\result.json");
			FileReader fr = new FileReader(article);
			BufferedReader bfr = new BufferedReader(fr);
			String line = null;
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int count = 0;
			int num = 0;
			double totalTime = 0;
			double startTime = 0;
			double endTime = 0;

			while ((line = bfr.readLine()) != null) {
				bulkRequest.add(client.prepareIndex("m8", "info").setSource(line));
				if (count % (100000000 - 1) == 0) {

					System.out.println("start....");
					startTime = System.currentTimeMillis();
					bulkRequest.execute().actionGet();
					endTime = System.currentTimeMillis();
					System.out.println(++num + "finish....");
					totalTime += endTime - startTime;
					System.out.println("��ʱ��" + (endTime - startTime) / 1000);
				}
				count++;
				// System.out.println(line);
			}
			bulkRequest.execute().actionGet();
			System.out.println("��ʱ��:" + totalTime);
			bfr.close();
			fr.close();

			// ������
			System.out.println(response.getSourceAsString());
			// �ر�client
			client.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
