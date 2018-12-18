package com.sample;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class MockServer {

    public static void main(String[] args) {

        // step 1. 告诉程序： mock服务器在哪里
        WireMock.configureFor(8060); // 由于Wiremock架在本地，因此只需要输入端口号即可
        // WireMock.configueFor("localhost", 8080); // 如果WireMock不在本地，那么还需要输入服务器地址+端口号

        // step 2. 伪造一个测试桩，告诉程序：如何处理请求。
        // >> 接收GET请求：withBody里面的是一个json格式的字符串； withStatus状态码是200。
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/order/1"))
                .willReturn(WireMock.aResponse().withBody("{\"id\": 1}").withStatus(200)));

        // >> 接收POST请求
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/order/2")));

        // step 3. 用WireMock返回模板数据
        try {
            retFileContent("/order/3", "01");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void retFileContent(String url, String fileName) throws IOException {

        ClassPathResource classPathResource = new ClassPathResource("mock/response" + fileName + ".txt");
        String retContent = StringUtils.join(FileUtils.readLines(classPathResource.getFile(), "UTF-8").toArray(), "\n");
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(url)).willReturn(WireMock.aResponse().withBody(retContent).withStatus(200)));

    }
}
