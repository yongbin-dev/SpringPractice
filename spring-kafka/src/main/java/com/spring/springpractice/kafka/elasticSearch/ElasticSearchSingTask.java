package com.spring.springpractice.kafka.elasticSearch;

import com.google.gson.Gson;
import com.spring.springpractice.kafka.elasticSearch.config.ElasticSearchSinkConnectorConfig;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class ElasticSearchSingTask extends SinkTask {

    private final Logger logger = LoggerFactory.getLogger(ElasticSearchSingTask.class);

    private ElasticSearchSinkConnectorConfig config;
    private RestHighLevelClient esClient;

    @Override
    public void start(Map<String, String> props) {
        try{
            config = new ElasticSearchSinkConnectorConfig(props);
        } catch (ConfigException e){
            throw new ConnectException(e.getMessage() , e);
        }

        esClient = new RestHighLevelClient(RestClient.builder(new HttpHost(config.getString(config.ES_CLUSTER_HOST) , config.getInt(config.ES_CLUSTER_PORT))));
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if(records.size() > 0){
            BulkRequest bulkRequest = new BulkRequest();
            for ( SinkRecord record : records){
                Gson gson = new Gson();
                Map map = gson.fromJson(record.value().toString() , Map.class);
                bulkRequest.add(new IndexRequest(config.getString(config.ES_CLUSTER_INDEX)).source(map , XContentType.JSON));
                logger.info("record : {}"  , record.value());
            }

            esClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse bulkItemResponses) {
                    if (bulkItemResponses.hasFailures()){
                        logger.error(bulkItemResponses.buildFailureMessage());
                    }else{
                        logger.info("bulk save success");
                    }

                }

                @Override
                public void onFailure(Exception e) {
                    logger.error(e.getMessage() , e);
                }
            });
        }
    }

    @Override
    public void stop() {
        try{
            esClient.close();
        }catch(IOException e){
            logger.error(e.getMessage() , e);
        }
    }
    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        logger.info("flush");
    }
    @Override
    public String version() {
        return "1.0";
    }
}
