package com.artemis.example;

import com.artemis.example.constants.Constants;
import com.artemis.example.controller.ProducerTestController;
import lombok.SneakyThrows;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SendMessageTest
 * @Date: Create in 10:17 2024/11/26
 */
public class SendMessageTest {

    public static void main(String[] args) {
        // step1 ：start Node2Application
        // step2 ：start Node1Application
        // step2 ：executeMethod
        jmsBridgeFailureTest();
        //clientBridgeSuccessButMessageTypeWrongTest();
        //clientBridgeSuccessTest();
    }

    /**
     * callInterface {@link ProducerTestController#sendMessage(String)}
     */
    @SneakyThrows
    private static void jmsBridgeFailureTest() {
        String url = Constants.NODE1_APPLICATION_URL + "/producer/sendMessage";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("message", "jmsBridgeFailureTest:" + Constants.MESSAGE));

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
            httpPost.setEntity(formEntity);

            String response = httpClient.execute(httpPost, httpResponse ->
                    EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8)
            );
            System.out.println("Response: " + response);
        }
    }

    @SneakyThrows
    private static void clientBridgeSuccessButMessageTypeWrongTest() {
        try (ClientSession session = ActiveMQClient.createServerLocator(Constants.NODE1_BROKER_URL)
                .createSessionFactory()
                .createSession()) {

            session.start();
            ClientProducer producer = session.createProducer(Constants.MULTICAST_ADDRESS);
            ClientMessage clientMessage = session.createMessage(true);
            clientMessage.setType(Message.TEXT_TYPE);
            clientMessage.getBodyBuffer()
                    .writeString("clientBridgeSuccessButMessageTypeWrongTest:" +Constants.MESSAGE);
            producer.send(clientMessage);
        }
    }

    @SneakyThrows
    private static void clientBridgeSuccessTest() {
        try (ClientSession session = ActiveMQClient.createServerLocator(Constants.NODE1_BROKER_URL)
                .createSessionFactory()
                .createSession()) {

            session.start();
            ClientProducer producer = session.createProducer(Constants.MULTICAST_ADDRESS);
            ClientMessage clientMessage = session.createMessage(true);
            clientMessage.setType(Message.TEXT_TYPE);
            clientMessage.getBodyBuffer()
                    .writeNullableSimpleString(SimpleString.toSimpleString("clientBridgeSuccessTest:" +Constants.MESSAGE));
            producer.send(clientMessage);
        }
    }
}
