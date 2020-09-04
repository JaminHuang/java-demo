package com.demo.rpc.hessian;

import com.caucho.hessian.client.HessianConnection;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 使用HttpClient实现hessian的远程调用
 */
public class HessianHttpClientConnection implements HessianConnection {

    private CloseableHttpClient _client;
    private HttpPost _request;
    private volatile CloseableHttpResponse _response;

    private ByteArrayOutputStream _outputStream;

    public HessianHttpClientConnection(HttpPost httpPost, CloseableHttpClient client)
            throws CloneNotSupportedException {
        _client = client;
        _request = (HttpPost) httpPost.clone(); // fix 并发串数据bug
        _outputStream = new ByteArrayOutputStream();
    }

    @Override
    public void addHeader(String key, String value) {
        _request.setHeader(new BasicHeader(key, value));
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this._outputStream;
    }

    @Override
    public void sendRequest() throws IOException {
        HessianPreRequestHandle.preRequestHandle(_request);
        _request.setEntity(new ByteArrayEntity(_outputStream.toByteArray()));
        _response = _client.execute(_request);
    }

    @Override
    public int getStatusCode() {
        return _response == null || _response.getStatusLine() == null ? 0 : _response.getStatusLine().getStatusCode();
    }

    @Override
    public String getStatusMessage() {
        return _response == null || _response.getStatusLine() == null ? null
                : _response.getStatusLine().getReasonPhrase();
    }

    @Override
    public String getContentEncoding() {
        return _response == null || _response.getEntity() == null || _response.getEntity().getContentEncoding() == null
                ? null : _response.getEntity().getContentEncoding().getValue();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = _response == null || _response.getEntity() == null ? null : _response.getEntity().getContent();
        HessianPostRequestHandle.postRequestHandle(_request, _response);
        return is;
    }

    @Override
    public void close() throws IOException {
        _outputStream.close();
        // if (_request != null)
        // _request.abort();
    }

    @Override
    public void destroy() throws IOException {
        if (_response != null)
            _response.close();
    }
}