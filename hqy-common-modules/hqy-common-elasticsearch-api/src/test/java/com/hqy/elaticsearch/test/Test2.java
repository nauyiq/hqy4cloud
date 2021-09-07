package com.hqy.elaticsearch.test;

import com.hqy.elasticsearch.test.document.User;
import com.hqy.elaticsearch.Main;
import com.hqy.util.JsonUtil;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author qy
 * @create 2021/9/2 23:47
 */
@SpringBootTest(classes = {Main.class})
@RunWith(SpringRunner.class)
public class Test2 {


    @Resource
    private RestHighLevelClient client;

    /**
     * 创建索引
     * @throws IOException
     */
    @Test
    public void testCreateIndex() throws IOException {
        //1.创建索引的请求
        CreateIndexRequest request = new CreateIndexRequest("test_index");
        //2.执行创建请
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    /**
     * 添加文档
     * @throws IOException
     */
    @Test
    public void testAddDocument() throws IOException {
        User user = new User("nauyiq", 3);
        //索引请求
        IndexRequest request = new IndexRequest("test_index");
        //设置id，超时
        request.id("1").timeout(TimeValue.timeValueSeconds(10));
        //讲数据放进IndexRequest
        request.source(JsonUtil.toJson(user), XContentType.JSON);

        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println(index.status());
        System.out.println(index.toString());
    }

    /**
     * 判断文档存不存在
     */
    @Test
    public void existDocument() throws IOException {
        GetRequest request = new GetRequest("test_index", "1");
        //不获取返回的 _source 的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    public void getDocument() throws Exception {
        GetRequest request = new GetRequest("test_index", "1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());

    }


}
