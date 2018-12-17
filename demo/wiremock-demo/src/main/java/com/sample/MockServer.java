/**
 * 
 */
package com.sample;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * @author llei
 *
 */
public class MockServer {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) {

		// 1. 告诉程序：Mock服务器在哪里
		configureFor(8060); // 由于WireMock架在本地，因此只需要输入端口号即可
		// WireMock.configureFor("localhost", 8080);  // 如果WireMock不在本地，那么还需要输入服务器地址+端口号
		
		// 2. 伪造一个测试桩，告诉程序：如何处理请求。
		// 接收GET请求； withBody里面的是一个json格式的字符串； withStatus状态码是200。
		stubFor(get(urlPathEqualTo("/order/1")).willReturn(aResponse().withBody("{\"id\": 1}").withStatus(200)));
		
		// 接收POST请求
		stubFor(post(urlPathEqualTo("/order/2")));
		
		// 3. 用WireMock返回模板数据 
		try {
			retFileContent("/order/3", "01");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param url 
	 * @param file 
	 * @throws Exception 
	 */
	private static void	retFileContent(String url, String fileName) throws Exception {

		ClassPathResource classPathResource = new ClassPathResource("mock/response/" + fileName + ".txt");
		String retContent = StringUtils.join(FileUtils.readLines(classPathResource.getFile(), "UTF-8").toArray(), "\n");
		stubFor(get(urlPathEqualTo(url)).willReturn(aResponse().withBody(retContent).withStatus(200)));

	}

}
