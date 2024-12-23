package org.dflib.http;

import io.bootique.BQRuntime;
import io.bootique.Bootique;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.junit5.JettyTester;
import io.bootique.junit5.BQApp;
import io.bootique.junit5.BQTest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@BQTest
public class HttpTest {

    static final JettyTester jetty = JettyTester.create();

    @BQApp
    static final BQRuntime app = Bootique.app("--server")
            .autoLoadModules()
            .module(jetty.moduleReplacingConnectors())
            .module(b -> JettyModule.extend(b)
                    .addServlet(TestRedirectServlet.class)
                    .addServlet(TestHeadersServlet.class)
                    .addServlet(TestServlet.class))
            .createRuntime();

    @Test
    void concatUrls_Root() {
        assertEquals("/", Http.concatUrls("", null));
        assertEquals("/", Http.concatUrls("/", null));
        assertEquals("/", Http.concatUrls("/", "/"));
        assertEquals("/", Http.concatUrls("/", ""));
        assertEquals("/a", Http.concatUrls("/", "a"));
        assertEquals("/a", Http.concatUrls("/", "/a"));
    }

    @Test
    void concatUrls_Http() {
        assertEquals("http://abc", Http.concatUrls("http://abc", null));
        assertEquals("http://abc", Http.concatUrls("http://abc", ""));
        assertEquals("http://abc/", Http.concatUrls("http://abc", "/"));
        assertEquals("http://abc/", Http.concatUrls("http://abc/", "/"));
        assertEquals("http://abc/a", Http.concatUrls("http://abc", "a"));
        assertEquals("http://abc/a", Http.concatUrls("http://abc/", "a"));
        assertEquals("http://abc/a", Http.concatUrls("http://abc", "/a"));
        assertEquals("http://abc/a/", Http.concatUrls("http://abc", "a/"));
    }

    @Test
    void createURI() {
        assertEquals("http://a", Http.of("http://a").createURI());
        assertEquals("http://a?a=B&b=C", Http.of("http://a").queryParam("a", "B").queryParam("b", "C").createURI());
        assertEquals("http://a?a=B&a=C", Http.of("http://a").queryParam("a", "B", "C").createURI());
        assertEquals("http://a?a=B+C", Http.of("http://a").queryParam("a", "B C").createURI());
        assertEquals("http://a?a=B%26C", Http.of("http://a").queryParam("a", "B&C").createURI());
    }

    @Test
    void source_text() {
        Http connector = Http.of(jetty.getUrl());

        byte[] r1 = connector.path("_text/a").source().asBytes();
        assertEquals("response to [/a]", new String(r1));

        byte[] r2 = connector.path("_text/b/c/x.txt").source().asBytes();
        assertEquals("response to [/b/c/x.txt]", new String(r2));
    }

    @Test
    void source_redirect() {
        Http connector = Http.of(jetty.getUrl());

        byte[] r1 = connector.path("_redirect").source().asBytes();
        assertEquals("response to [/redirect]", new String(r1));
    }

    @Test
    void source_headers() {
        Http connector = Http.of(jetty.getUrl());

        byte[] r1 = connector.path("_headers").source().asBytes();
        assertEquals("auth: [null]", new String(r1));

        String basicAuth = "Basic: " + Base64.getEncoder().encodeToString("admin:test".getBytes());
        byte[] r2 = connector.path("_headers").header("Authorization", basicAuth).source().asBytes();
        assertEquals("auth: [Basic: YWRtaW46dGVzdA==]", new String(r2));
    }

    @WebServlet(urlPatterns = "/_text/*")
    static class TestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.getWriter().print("response to [" + req.getPathInfo() + "]");
        }
    }

    @WebServlet(urlPatterns = "/_redirect")
    static class TestRedirectServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.sendRedirect("_text/redirect");
        }
    }

    @WebServlet(urlPatterns = "/_headers")
    static class TestHeadersServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

            String auth = req.getHeader("Authorization");
            resp.getWriter().print("auth: [" + auth + "]");
        }
    }
}
