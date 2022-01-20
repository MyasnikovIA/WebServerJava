# WebServerJava
Реализация Вэб сервера на Java (может работать в проектах Android)



 <pre>
      Состав:
           HttpSrv.java - Вэб сервер
           HttpResponse.java - объект с описанием запроса и вспомогательными функциями
           org.json.jar (05-12-2021) - библиотека для работы с JSON структурами  (JSONObject JSONArray)
 </pre>
 
      Применение:
 ```java
          HttpSrv srv;
          srv = new HttpSrv(9090);
 
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
```		  