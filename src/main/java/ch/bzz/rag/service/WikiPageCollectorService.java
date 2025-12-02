package ch.bzz.rag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class WikiPageCollectorService {

    public Set<String> collectPagesForNamespace(String baseUrl, String namespace) {
        String encoded_namespace = URLEncoder.encode(namespace, StandardCharsets.UTF_8);
        String url = baseUrl + "/start?idx=" + encoded_namespace;
        log.info("Recursively loading all pages from {}", url);
        Set<String> pageSet = collectPagesForUrl(url);
        log.info("Collected {} pages", pageSet.size());
        return pageSet;
    }

    private Set<String> collectPagesForUrl(String url) {
        Set<String> pageSet = new HashSet<>();
        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            for (Element item : doc.select("a.wikilink1")) {
                String href = item.attr("href");
                pageSet.add(href);
                log.debug("Collected page: {}", href);
            }
            for (Element ns : doc.select("a.idx_dir")) {
                String href = ns.attr("href");
                if (href.startsWith(url) && !href.equals(url)) {
                    pageSet.addAll(collectPagesForUrl(href));
                }
            }
        } catch (Exception ex) {
            log.error("Error crawling namespace {}: {}", url, ex.getMessage(), ex);
        }
        return pageSet;
    }
}
