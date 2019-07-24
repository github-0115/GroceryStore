package uyun.show.server.domain.util.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import uyun.show.server.domain.model.websocket.WSParmas;
import uyun.show.server.domain.model.websocket.WSResult;

import javax.websocket.Session;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaConsumerUtil implements Runnable {

    private final KafkaConsumer<String, String> consumer;
    private ConsumerRecords<String, String> msgList;
    private String topic;
    private static final String GROUPID = "Databank";

    private static Map<String, WSParmas> WSParmasMap = new ConcurrentHashMap<>();

    public static void AddClient(WSParmas wsParmas) {
        WSParmasMap.put(wsParmas.getSequence(), wsParmas);
    }

    public static void RemoveClient(String sequence) {
        if (WSParmasMap.get(sequence) == null) {
            return;
        }
        WSParmasMap.remove(sequence);
    }

    public KafkaConsumerUtil(WSParmas wsParmas, String servers, String topicName) {

        WSParmasMap.put(wsParmas.getSequence(), wsParmas);

        Properties props = new Properties();
        //kafka消费的的地址
        props.put("bootstrap.servers", servers);
        //组名 不同组名可以重复消费
        props.put("group.id", GROUPID);
        //是否自动提交
        props.put("enable.auto.commit", "true");
        //从poll(拉)的回话处理时长
        props.put("auto.commit.interval.ms", "1000");
        //超时时间
        props.put("session.timeout.ms", "30000");
        //一次最大拉取的条数
        props.put("max.poll.records", 1000);
//		earliest当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
//		latest
//		当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
//		none
//		topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
        props.put("auto.offset.reset", "earliest");
        //序列化
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<String, String>(props);

        this.topic = topicName;
        //订阅主题列表topic
        this.consumer.subscribe(Arrays.asList(topic));
    }

    @Override
    public void run() {

        System.out.println(topic + " :  开始消费");

        try {

            for (; ; ) {

                if (WSParmasMap == null || WSParmasMap.size() == 0) {
                    Thread.sleep(1000);
                    continue;
                }

                msgList = consumer.poll(1000);
                if (null != msgList && msgList.count() > 0) {

                    List<JSONObject> list = new ArrayList<>();
                    for (ConsumerRecord<String, String> record : msgList) {
//                        System.out.println(messageNo + "=======receive: key = " + record.key() + ", value = " + record.value() + " offset===" + record.offset());
                        list.add(JSON.parseObject(record.value()));
                    }

                    for (String sequence : WSParmasMap.keySet()
                    ) {
                        if (sequence == null || sequence.isEmpty()) {
                            WSParmasMap.remove(sequence);
                            continue;
                        }

                        WSParmas wsParmas = WSParmasMap.get(sequence);
                        if (wsParmas == null) {
                            WSParmasMap.remove(sequence);
                            return;
                        }

                        Session session = wsParmas.getSession();
                        if (session == null || !session.isOpen()) {
                            WSParmasMap.remove(sequence);
                            continue;
                        }

                        WSResult wsResult = new WSResult();
                        wsResult.setId(wsParmas.getId());
                        wsResult.setSequence(wsParmas.getSequence() == null ? "" : wsParmas.getSequence());
                        wsResult.setStatus(true);
                        wsResult.setCollection(list);
                        session.getBasicRemote().sendText(URLEncoder.encode(JSON.toJSONString(wsResult), "utf-8"));
                    }

                } else {
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(topic + " InterruptedException : " + e);
        } catch (UnsupportedEncodingException e) {
            System.out.println(topic + " UnsupportedEncodingException : " + e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }

//    public static void main(String args[]) {
//        KafkaConsumerTest test1 = new KafkaConsumerTest("KAFKA_TEST");
//        Thread thread1 = new Thread(test1);
//        thread1.start();
//    }
}