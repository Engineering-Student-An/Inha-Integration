package an.inhaintegration.rentalee.service;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateWithProxy {

    public static RestTemplate createRestTemplateWithProxy() {
        // 프록시 정보 입력 (예: 사내 프록시)
        String proxyHost = "https://5vwyouaxq0.execute-api.ap-northeast-2.amazonaws.com"; // 프록시 호스트
        int proxyPort = 8080;                       // 프록시 포트

        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory((HttpClient) httpClient);

        return new RestTemplate(factory);
    }
}
