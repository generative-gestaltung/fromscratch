package de.fromscratch.node.server;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class HttpAdapter implements HttpHandler {

	protected void output (HttpExchange t, String theVal) throws IOException {
        t.sendResponseHeaders(200, theVal.length());
        OutputStream os = t.getResponseBody();
        os.write(theVal.getBytes());
        os.close();
	}
}
