package com.miacomsoft;

import org.json.JSONObject;

import java.net.*;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;


public class Main {

    public static void main(String[] args) {
        HttpSrv srv;
        srv = new HttpSrv(9091);

        // Обработка запросов из терминала
        srv.onTerminal((HttpResponse Head) -> {
            System.out.println(Head.message);
            Head.write("Сообщение обработано на сервере:" + Head.message);
        });

        // Обработка запросов браузера URL пути
        // страница не найдена
        srv.onPage404((HttpResponse Head) -> {
            Head.Head();
            Head.Body("<h1><center>Ресурс не найден</center></h1>");
            Head.End();
        });

        // Создаем страницу в коде
        srv.onPage("index.html", (HttpResponse Head) -> {
            Head.Head();
            Head.Body("Текст HTML страницы");
            Head.Body("fff<h1>Обработка тэгов</h1>ffffffffff");
            Head.Body("--------------------------");
            Head.Body(Head.request.toString());
            Head.Body(Head.request.toString());
            Head.End();
        });

        // Отправляем JSON объект в коде
        srv.onPage("json.html", (HttpResponse Head) -> {
            Head.sendJson("{'ok':14234234}");
        });

        // Отправка файла при указании ресурса
        srv.onPage("test.html","F:\\javaProject\\HtmlServer013\\www\\index.html");

        //  Указываем директорию в которой распложен контент
        srv.onPage("F:\\javaProject\\HtmlServer013\\www\\");

        // Запуск сервера
        srv.start();
    }
}
