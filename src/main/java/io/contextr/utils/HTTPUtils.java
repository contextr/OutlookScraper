package io.contextr.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HTTPUtils {

	public String urlEncode(String data) {
		data = data.replace(" ", "_");
		try {
			data = URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Sends a request to the specified URL with the specified payload, using
	 * the specified method, and returning a specified class.
	 * 
	 * @param requestPayload
	 * @param requestURL
	 * @param requestMethod
	 * @param responseClass
	 * @return
	 */
	public static <S, T> T sendRequest(S requestPayload, String requestURL, HttpMethod requestMethod,
			Class<T> responseClass) {
		
//		sleep(500);
		
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<S> requestEntity = null;
		headers.setContentType(MediaType.APPLICATION_JSON);
		requestEntity = new HttpEntity<>(requestPayload, headers);

		ResponseEntity<T> responseEntity = restTemplate.exchange(requestURL, requestMethod, requestEntity,
				responseClass);
		return responseEntity.getBody();
	}

	private static void sleep(long time) {
		if (time > 0)
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
