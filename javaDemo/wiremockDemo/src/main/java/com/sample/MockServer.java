package com.sample;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class MockServer {

    public static void main(String[] args) {

        // step 0. 清空之前的配置
        // WireMock.removeAllMappings();

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

        // step 4. 模拟登陆验证
        try {
            loginSuccess();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e.toString());
        }
        try {
            loginFaild();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e.toString());
        }

    }

    private static void retFileContent(String url, String fileName) throws IOException {

        ClassPathResource classPathResource = new ClassPathResource("mock/response/" + fileName + ".txt");
        String retContent = StringUtils.join(FileUtils.readLines(classPathResource.getFile(), "UTF-8").toArray(), "\n");
        // WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(url)).willReturn(WireMock.aResponse().withBody(retContent).withStatus(200)));
        WireMock.stubFor(WireMock.request("GET", WireMock.urlPathEqualTo(url)).willReturn(WireMock.aResponse().withBody(retContent).withStatus(200)));
    }

    private static void loginSuccess() throws IOException {

        UrlPattern urlPattern = WireMock.urlEqualTo("/login");
        StringValuePattern bodyPattern = WireMock.equalToJson("{\"username\": \"aaa\", \"password\": \"111111\"}");
        MappingBuilder mappingBuilder = WireMock.request("POST", urlPattern).withRequestBody(bodyPattern);

        ClassPathResource classPathResource = new ClassPathResource("mock/response/login/success.txt");
        String retContent = StringUtils.join(FileUtils.readLines(classPathResource.getFile(), "UTF-8").toArray(), "\n");
        
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/login"))
            .withRequestBody(bodyPattern)
            .willReturn(WireMock.aResponse().withBody(retContent).withStatus(200)));

    }

    private static void loginFaild() throws IOException {

        UrlPattern urlPattern = WireMock.urlEqualTo("/login");
        StringValuePattern bodyPattern = WireMock.equalToJson("{\"username\": \"bbb\", \"password\": \"111111\"}");
        MappingBuilder mappingBuilder = WireMock.request("POST", urlPattern).withRequestBody(bodyPattern);

        ClassPathResource classPathResource = new ClassPathResource("mock/response/login/faild.txt");
        String retContent = StringUtils.join(FileUtils.readLines(classPathResource.getFile(), "UTF-8").toArray(), "\n");
        
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/login"))
            .withRequestBody(bodyPattern)
            .willReturn(WireMock.aResponse().withBody(retContent).withStatus(200)));

    }
}
