package br.com.mserpa.firstcodevertx;

import io.vertx.core.Vertx;

public class Application{

    public static void main(String[] args) {
        Vertx.vertx().createHttpServer()
            .requestHandler(handler -> {
                handler.response().end("<h1>Hello from my first Vert.x 3 application</h1>");
            })
            .listen(8080, handler -> {
                if (handler.succeeded()) {
                    System.out.println("Succeeded");
                } else {
                    System.out.println("Fail");
                }
            });
}

}
