/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.miacomsoft;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author MyasnikovIA (20-01-2022)
 *
 *     Состав:
 *          HttpSrv.java - Вэб сервер
 *          HttpResponse.java - объект с описанием запроса и вспомогательными функциями
 *          org.json.jar (05-12-2021) - библиотека для работы с JSON структурами  (JSONObject JSONArray)
 *
 *     Применение:
 *
 *         HttpSrv srv;
 *         srv = new HttpSrv(9090);
 *
 *         // Обработка запросов из терминала
 *         srv.onTerminal((HttpResponse Head) -> {
 *             System.out.println(Head.message);
 *             Head.write("Сообщение обработано на сервере:" + Head.message);
 *         });
 *
 *         // Обработка запросов браузера URL пути
 *         // страница не найдена
 *         srv.onPage404((HttpResponse Head) -> {
 *             Head.Head();
 *             Head.Body("<h1><center>Ресурс не найден</center></h1>");
 *             Head.End();
 *         });
 *
 *         // Создаем страницу в коде
 *         srv.onPage("index.html", (HttpResponse Head) -> {
 *             Head.Head();
 *             Head.Body("Текст HTML страницы");
 *             Head.Body("fff<h1>Обработка тэгов</h1>ffffffffff");
 *             Head.Body("--------------------------");
 *             Head.End();
 *         });
 *
 *         // Отправляем JSON объект в коде
 *         srv.onPage("json.html", (HttpResponse Head) -> {
 *             Head.sendJson("{'ok':14234234}");
 *         });
 *
 *         // Отправка файла при указании ресурса
 *         srv.onPage("test.html","F:\\javaProject\\HtmlServer013\\www\\index.html");
 *
 *         //  Указываем директорию в которой распложен контент
 *         srv.onPage("F:\\javaProject\\HtmlServer013\\www\\");
 *
 *         // Запуск сервера
 *         srv.start();
 *
 */
public class HttpSrv {

    public interface CallbackSocket {
        public void call(JSONObject Headers);
    }

    public interface CallbackQuery {
        public void call(JSONObject Headers);
    }

    public CallbackPage callbackSocketPage = null;
    public CallbackPage callbackSocketTerminal = null;
    public CallbackPage callbackOnPage404 = null;
    private HashMap<String, PageObj> pagesList = new HashMap<String, PageObj>(10, (float) 0.5);
    private HashMap<String, File> pagesPathList = new HashMap<String, File>(10, (float) 0.5);
    private HashMap<String, Object> sessionList = new HashMap<String, Object>(10, (float) 0.5);
    private String rootPath = null;

    public class PageObj {
        CallbackPage callback;
        String ContentType;
    }

    public interface CallbackPage {
        public void call(HttpResponse Headers) throws JSONException, IOException;
    }


    private Thread mainThready = null;
    private int port = 9091;

    public HttpSrv(int port) {
        this.port = port;
    }

    private boolean process = false;

    /**
     * Запуск сервера
     */
    public void start() {
        process = true;
        if (mainThready != null) {
            Stop();
            mainThready.stop();
        }
        mainThready = new Thread(new Runnable() {
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(port);
                    while (process == true) {
                        Socket socket = ss.accept();
                        new Thread(new SocketProcessor(socket)).start();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(HttpSrv.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Throwable ex) {
                    Logger.getLogger(HttpSrv.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        mainThready.start();    //Запуск потока
    }

    /***
     * Остановка сервера
     */
    public void Stop() {
        process = false;
    }

    /***
     * Обработка подключений к серверу через терминал
     * @param callbackSocketTerminal
     */
    public void onTerminal(CallbackPage callbackSocketTerminal) {
        this.callbackSocketTerminal = callbackSocketTerminal;
    }

    /***
     * Описываем события обработки  не найденого ресурса
     * @param callbackOnPage404
     */
    public void onPage404(CallbackPage callbackOnPage404) {
        this.callbackOnPage404 = callbackOnPage404;
    }

    /***
     * Указываем ресурс для обработки любых запросов
     * @param callbackSocketPage- функция обработки вызова ресурса
     */
    public void onPage(CallbackPage callbackSocketPage) {
        this.callbackSocketPage = callbackSocketPage;
    }

    /***
     * Указываем ресурс для URL запроса
     * @param query - текст запроса (ContentType определяется по расширению ресурса )
     * @param callbackSocketPage- функция обработки вызова ресурса
     */
    public void onPage(String query, CallbackPage callbackSocketPage) {
        PageObj pageObj = new PageObj();
        pageObj.callback = callbackSocketPage;
        pageObj.ContentType = ContentType(new File(query.toLowerCase()));
        this.pagesList.put(query, pageObj);
    }

    /***
     * Указываем ресурс для URL запроса
     * @param query - текст запроса
     * @param contentType - тип контента для интерпритации браузером (ContentType)
     * @param callbackSocketPage - функция обработки вызова ресурса
     */
    public void onPage(String query, String contentType, CallbackPage callbackSocketPage) {
        PageObj pageObj = new PageObj();
        pageObj.callback = callbackSocketPage;
        if (contentType.indexOf(".") != -1) {
            pageObj.ContentType = ContentType(new File("page" + contentType));
        } else {
            pageObj.ContentType = contentType;
        }
        this.pagesList.put(query, pageObj);
    }

    /***
     * Указываем ресурс для URL запроса
     * @param query - URL запрос из брайзера
     * @param absalutePath - абсалютный путь к файла
     */
    public void onPage(String query, String absalutePath) {
        pagesPathList.put(query, new File(absalutePath));
    }

    /**
     * Указываем путь к директории ресурсов
     *
     * @param rootPath
     */
    public void onPage(String rootPath) {
        File f = new File(rootPath);
        if (f.exists() && f.isDirectory()) {
            this.rootPath = rootPath;
        }
    }

    /**
     * Указываем ресурс для URL запроса
     *
     * @param query-      URL запрос из брайзера
     * @param pathResurse - абсалютный путь к файла
     */
    public void onPage(String query, File pathResurse) {
        pagesPathList.put(query, pathResurse);
    }

    /***
     * Основной поток обработки  подключения клиента
     */
    private class SocketProcessor implements Runnable {
        HashMap<String, Object> session; // локальная сессия каждого подключения
        HttpResponse query; // универсальный объект обработки запросов

        public SocketProcessor(Socket socket) throws IOException {
            String textId = "C" + getMd5(socket.getRemoteSocketAddress().toString().split(":")[0] + "_" + socket.getInetAddress().getCanonicalHostName());
            if (sessionList.containsKey(textId)) {
                session = (HashMap<String, Object>) sessionList.get(textId);
            } else {
                session = new HashMap<String, Object>(10, (float) 0.5);
                sessionList.put(textId, session);
            }
            query = new HttpResponse(socket, session);
        }

        public void run() {
            try {
                if (readHead()) {
                    writeResponse();
                }
            } catch (Throwable t) {
            } finally {
                try {
                    query.socket.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
        }

        /***
         * Чтение заголовка запроса от клиента,если в первой строке невстречается слово GET или POST, тогда обрабатывается терминальное подключение
         * @return
         * @throws IOException
         */
        private boolean readHead() throws IOException {
            StringBuffer sbInData = new StringBuffer();
            int numLin = 0;
            int charInt;
            StringBuffer sb = new StringBuffer();
            StringBuffer sbTmp = new StringBuffer();
            while ((charInt = query.inputStreamReader.read()) > 0) {
                if (query.socket.isConnected() == false) return false;
                sbTmp.append((char) charInt);
                if (sbTmp.toString().indexOf("\r") != -1) {
                    // если в первой строке невстречается слово GET или POST, тогда отключаем соединение
                    if (sb.toString().split("\r").length == 1) {
                        int res = sb.toString().indexOf("GET");
                        if (res == -1) {
                            res = sb.toString().indexOf("POST");
                            if (res == -1) {
                                // обработка терминального запроса
                                while (query.socket.isConnected()) {
                                    StringBuffer sbSub2;
                                    while ((sbSub2 = query.readText()).toString().length() != 0) {
                                        if (query.getStatusExut()) return false;
                                        query.countQuery++;
                                        query.message = sbSub2.toString();
                                        if (callbackSocketTerminal != null) {
                                            callbackSocketTerminal.call(query);
                                        }
                                    }
                                }
                                query.close();
                                return false;
                            }
                        }
                    }
                    if (sbTmp.toString().length() == 2) {
                        break; // чтение заголовка окончено
                    }
                    sbTmp.setLength(0);
                }
                sb.append((char) charInt);

            }
            if (sb.toString().indexOf("Content-Length: ") != -1) {
                String sbTmp2 = sb.toString().substring(sb.toString().indexOf("Content-Length: ") + "Content-Length: ".length(), sb.toString().length());
                String lengPostStr = sbTmp2.substring(0, sbTmp2.indexOf("\n")).replace("\r", "");
                int LengPOstBody = Integer.valueOf(lengPostStr);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                while ((charInt = query.inputStreamReader.read()) > 0) {
                    if (query.socket.isConnected() == false) {
                        return false;
                    }
                    // outLog.write((char) charInt);
                    buffer.write((char) charInt);
                    LengPOstBody--;
                    if (LengPOstBody == 0) {
                        break;
                    }
                }
                buffer.flush();
                // POST = buffer.toByteArray();
                query.request.put("PostBodyText", new JSONObject(new String(buffer.toByteArray())));
                query.POST = buffer.toByteArray();
            }
            int indLine = 0;
            String getCmd = "";
            for (String TitleLine : sb.toString().split("\r")) {
                TitleLine = TitleLine.replace("\n", "");
                indLine++;
                if (indLine == 1) {
                    TitleLine = TitleLine.replaceAll("GET /", "");
                    TitleLine = TitleLine.replaceAll("POST /", "");
                    TitleLine = TitleLine.replaceAll(" HTTP/1.1", "");
                    TitleLine = TitleLine.replaceAll(" HTTP/1.0", "");
                    query.contentZapros = java.net.URLDecoder.decode(TitleLine, "UTF-8");
                    query.request.put("ContentZapros", query.contentZapros);
                    if (query.contentZapros.indexOf("?") != -1) {
                        String tmp = query.contentZapros.substring(0, query.contentZapros.indexOf("?") + 1);
                        String param = query.contentZapros.replace(tmp, "");
                        getCmd = param;
                        query.request.put("ParamAll", param);
                        int indParam = 0;
                        for (String par : param.split("&")) {
                            String[] val = par.split("=");
                            if (val.length == 2) {
                                val[0] = java.net.URLDecoder.decode(val[0], "UTF-8");
                                val[1] = java.net.URLDecoder.decode(val[1], "UTF-8");
                                query.request.put(val[0], val[1]);
                                val[0] = val[0].replace(" ", "_");
                                query.request.put(val[0], val[1]);
                                query.requestParam.put(val[0], val[1]);
                            } else {
                                indParam++;
                                val[0] = java.net.URLDecoder.decode(val[0], "UTF-8");
                                query.request.put("Param" + String.valueOf(indParam), val[0]);
                                query.requestParam.put("Param" + String.valueOf(indParam), val[0]);
                            }
                        }
                        query.contentZapros = tmp.substring(0, tmp.length() - 1);//.toLowerCase()
                    }
                    query.request.put("Zapros", query.contentZapros);
                    //query.Json.put("RootPath", rootPath);
                    //query.Json.put("AbsalutZapros", rootPath + "\\" + query.contentZapros);
                } else {
                    if (TitleLine == null || TitleLine.trim().length() == 0) {
                        break;
                    }
                    if (TitleLine.split(":").length > 0) {
                        String val = TitleLine.split(":")[0];
                        val = val.replace(" ", "_");
                        query.request.put(val, TitleLine.replace(TitleLine.split(":")[0] + ":", ""));
                    }
                    if (TitleLine.indexOf("Authorization:") == 0) {
                        //Authorization: Basic dXNlcjoxMjM=
                        String coderead = TitleLine.replaceAll("Authorization: Basic ", "");
                        query.request.put("Author", TitleLine.replaceAll("Authorization: Basic ", ""));
                    }
                }
            }
            //
            // кодировка входных данных
            if (query.request.has("Content-Type") == true) {
                // Content-Type: text/html; charset=windows-1251
                if (query.request.get("Content-Type").toString().split("charset=").length == 2) {
                    query.request.put("Charset", query.request.get("Content-Type").toString().split("charset=")[1]);
                }
            }
            // Парсим Cookie если он есть
            if (query.request.has("Cookie") == true) {
                String Cookie = query.request.get("Cookie").toString();
                Cookie = Cookie.substring(1, Cookie.length());// убираем лишний пробел сначала строки
                for (String elem : Cookie.split("; ")) {
                    String[] val = elem.split("=");
                    query.request.put(val[0], val[1]);
                    val[0] = val[0].replace(" ", "_");
                    query.request.put(val[0], val[1]);
                    query.requestParam.put(val[0], val[1]);
                }
            }
            if (query.request.has("X-Forwarded-For") == true) {
                query.requestParam.put("MacAddClient", query.GetMacClient(query.request.get("X-Forwarded-For").toString()));
            }
            sb.setLength(0);
            return true;
        }

        /**
         * Отправка ответа  вэб браузеру
         *
         * @throws IOException
         */
        private void writeResponse() throws IOException {
            String queryText = query.request.getString("Zapros").toLowerCase();
            File pageFile = null;
            if (rootPath != null) {
                pageFile = new File(rootPath + "\\" + queryText);
            }
            if (pagesList.containsKey(queryText)) {
                PageObj pageObj = pagesList.get(queryText);
                query.request.put("ContentType", pageObj.ContentType);
                pageObj.callback.call(query);
            } else if (callbackSocketPage != null) {
                callbackSocketPage.call(query);
            } else if (pagesPathList.containsKey(queryText)) {
                sendRawFile(pagesPathList.get(queryText));
            } else if ((pageFile != null) && (pageFile.isFile())) {
                sendRawFile(pageFile);
            } else if (callbackOnPage404 != null) {
                callbackOnPage404.call(query);
            } else {
                query.sendJson("{\"ok\":true}");
            }
        }

        /**
         * Отправка бинарного файла клиенту
         *
         * @param pageFile
         */
        private void sendRawFile(File pageFile) {
            try {
                String TypeCont = ContentType(pageFile);
                // Первая строка ответа
                query.os.write("HTTP/1.1 200 OK\r\n".getBytes());
                // дата создания в GMT
                DateFormat df = DateFormat.getTimeInstance();
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                // Время последней модификации файла в GMT
                query.os.write(("Last-Modified: " + df.format(new Date(pageFile.lastModified())) + "\r\n").getBytes());
                // Длина файла
                query.os.write(("Content-Length: " + pageFile.length() + "\r\n").getBytes());
                query.os.write(("Content-Type: " + TypeCont + "; charset=utf-8\r\n").getBytes());
                //query.os.write(("Content-Type: " + TypeCont + "; ").getBytes());
                //query.os.write(("charset=" + query.Json.get("Charset") + "\r\n").getBytes());;
                // Остальные заголовки
                query.os.write("Connection: close\r\n".getBytes());
                query.os.write("Server: HTMLserver\r\n\r\n\r\n".getBytes());
                // Сам файл:
                FileInputStream fis = new FileInputStream(pageFile.getAbsolutePath());
                int lengRead = 1;
                byte buf[] = new byte[1024];
                while ((lengRead = fis.read(buf)) != -1) {
                    query.os.write(buf, 0, lengRead);
                    query.os.flush();
                }
                // закрыть файл
                fis.close();
                // завершаем соединение
                query.os.flush();
                query.is.close();
            } catch (IOException ex) {
                Logger.getLogger(HttpSrv.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Определить по файлу тип HTML контента
     *
     * @param pageFile
     * @return
     */
    public static String ContentType(File pageFile) {
        String ras = null;
        // путь без файла
        String Dir = pageFile.getPath().replace(pageFile.getName(), "").toLowerCase();
        ;
        // имя файла с расширением
        String FileName = pageFile.getName();
        // расширение файла
        String rashirenie = FileName.substring(FileName.lastIndexOf(".") + 1);
        // путь к файлу + имя файла - расширение файла
        String DirFile = pageFile.getPath().replace("." + rashirenie, "");
        // имя файла без расширения
        String File2 = FileName.replace("." + rashirenie, "");
        rashirenie = rashirenie.toLowerCase();// преобразуем в нижний регистр

        //  try {
        //   PrintWriter pw = new PrintWriter(new FileWriter("E:\\YandexDisk\\WebServer\\HtmlServer_012\\LOG!!!!.txt"));
        //   pw.write(rashirenie);
        //   pw.close();
        //  } catch (IOException ex) {
        //     Logger.getLogger(HttpSrv.class.getName()).log(Level.SEVERE, null, ex);
        //  }

        if (rashirenie.equals("css")) {
            return "text/css";
        }
        if (rashirenie.equals("js")) {
            return "application/x-javascript";
        }
        if (rashirenie.equals("xml") || rashirenie.equals("dtd")) {
            return "text/xml";
        }
        if ((rashirenie.equals("txt")) || (rashirenie.equals("inf")) || (rashirenie.equals("nfo"))) {
            return "text/plain";
        }
        if ((rashirenie.equals("html")) || (rashirenie.equals("htm")) || (rashirenie.equals("shtml")) || (rashirenie.equals("shtm")) || (rashirenie.equals("stm")) || (rashirenie.equals("sht"))) {
            return "text/html";
        }
        if ((rashirenie.equals("mpeg")) || (rashirenie.equals("mpg")) || (rashirenie.equals("mpe"))) {
            return "video/mpeg";
        }
        if ((rashirenie.equals("ai")) || (rashirenie.equals("ps")) || (rashirenie.equals("eps"))) {
            return "application/postscript";
        }
        if (rashirenie.equals("rtf")) {
            return "application/rtf";
        }
        if ((rashirenie.equals("au")) || (rashirenie.equals("snd"))) {
            return "audio/basic";
        }
        if ((rashirenie.equals("bin")) || (rashirenie.equals("dms")) || (rashirenie.equals("lha")) || (rashirenie.equals("lzh")) || (rashirenie.equals("class")) || (rashirenie.equals("exe"))) {
            return "application/octet-stream";
        }
        if (rashirenie.equals("doc")) {
            return "application/msword";
        }
        if (rashirenie.equals("pdf")) {
            return "application/pdf";
        }
        if (rashirenie.equals("ppt")) {
            return "application/powerpoint";
        }
        if ((rashirenie.equals("smi")) || (rashirenie.equals("smil")) || (rashirenie.equals("sml"))) {
            return "pplication/smil";
        }
        if (rashirenie.equals("zip")) {
            return "application/zip";
        }
        if ((rashirenie.equals("midi")) || (rashirenie.equals("kar"))) {
            return "audio/midi";
        }
        if ((rashirenie.equals("mpga")) || (rashirenie.equals("mp2")) || (rashirenie.equals("mp3"))) {
            return "audio/mpeg";
        }
        if (rashirenie.equals("wav")) {
            return "audio/x-wav";
        }
        if (rashirenie.equals("ief")) {
            return "image/ief";
        }

        if ((rashirenie.equals("jpeg")) || (rashirenie.equals("jpg")) || (rashirenie.equals("jpe"))) {
            return "image/jpeg";
        }
        if (rashirenie.equals("png")) {
            return "image/png";
        }
        if (rashirenie.equals("ico")) {
            return "image/x-icon";
        }
        if ((rashirenie.equals("tiff")) || (rashirenie.equals("tif"))) {
            return "image/tiff";
        }
        if ((rashirenie.equals("wrl")) || (rashirenie.equals("vrml"))) {
            return "model/vrml";
        }
        if (rashirenie.equals("avi")) {
            return "video/x-msvideo";
        }
        if (rashirenie.equals("flv")) {
            return "video/x-flv";
        }
        if (rashirenie.equals("ogg")) {
            return "video/ogg";
        }
        return "application/octet-stream";
    }

    /***
     * Ренерация MD5 хэша
     * @param input
     * @return
     */
    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
