package ch.bzz.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WikiPageDownloaderService {

    private XmlRpcClient client;

    public void init(String baseUrl) throws MalformedURLException {
        String xmlRpcUrl = baseUrl + "/lib/exe/xmlrpc.php";
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(URI.create(xmlRpcUrl).toURL());

        client = new XmlRpcClient();
        client.setConfig(config);
    }

    public String downloadPage(String pageId) {
        try {
            Object result = client.execute("wiki.getPage", List.of(pageId));
            log.debug("Downloaded page: {}", pageId);
            return result.toString();
        } catch (XmlRpcException e) {
            log.error("Error downloading page {}: {}", pageId, e.getMessage(), e);
        }
        return null;
    }
}
