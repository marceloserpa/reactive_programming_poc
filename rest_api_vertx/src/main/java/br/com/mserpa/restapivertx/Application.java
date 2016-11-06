package br.com.mserpa.restapivertx;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Application extends AbstractVerticle {

    private final static int APP_PORT = 8080;
    private List<Book> books = new ArrayList<>();

    public static void main(String[] args){
        Launcher.executeCommand("run", Application.class.getName());
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        router.get("/api/books").handler(rc -> {
            rc.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(books));
        });

        router.route("/api/books*").handler(BodyHandler.create());
        router.post("/api/books").handler(routingContextHandler -> {
            Book book = Json.decodeValue(routingContextHandler.getBodyAsString(), Book.class);
            books.add(book);
            routingContextHandler.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(book));
        });

        router.get("/api/books/:id").handler(routingContextHandler -> {
            Long id = Long.valueOf(routingContextHandler.request().getParam("id"));
            Optional<Book> bookFound = books.stream().filter(book -> book.getId() == id).findFirst();

            bookFound.ifPresent(book -> {
                routingContextHandler.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .setStatusCode(200)
                    .end(Json.encodePrettily(book));
            });

            bookFound.orElseGet(() -> {
                routingContextHandler.response().setStatusCode(404).end();
                return null;
            });

        });

        router.delete("/api/books/:id").handler(routingContextHandler -> {
            Long id = Long.valueOf(routingContextHandler.request().getParam("id"));
            Optional<Book> bookFound = books.stream().filter(book -> book.getId() == id).findFirst();

            bookFound.ifPresent(book -> {
                this.books = this.books.stream().filter(b -> b.getId() != id).collect(Collectors.toList());
                routingContextHandler.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .setStatusCode(201)
                        .end(new JsonObject().put("message", "Book "+id+" removed!").encode());
            });

            bookFound.orElseGet(() -> {
                routingContextHandler.response().setStatusCode(404).end();
                return null;
            });

        });

        vertx.createHttpServer().requestHandler(router::accept).listen(APP_PORT);
    }





}
