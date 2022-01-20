/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.miacomsoft;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import static com.miacomsoft.HttpSrv.ContentType;

/**
 * @author MyasnikovIA
 */
public class HttpResponse {
    public int countQuery = 0;
    private boolean isExit = false;
    public JSONObject request = new JSONObject();
    public JSONObject requestParam = new JSONObject();
    public byte[] POST=null;
    public Socket socket;
    public InputStream is;
    public OutputStream os;
    public String contentZapros = "";
    public String message = "";
    public BufferedReader bufferedReader;
    public InputStreamReader inputStreamReader;
    public String Adress;
    public HashMap<String, Object> session=null;

    public HttpResponse(Socket socket, HashMap<String, Object> session) throws IOException {
        this.socket = socket;
        // this.socket.setSoTimeout(TimeOut);
        this.is = socket.getInputStream();
        this.os = socket.getOutputStream();
        this.Adress = socket.getRemoteSocketAddress().toString();
        this.request.put("RemoteIPAdress", Adress);
        this.request.put("charset", "utf-8");
        //this.Json.put("RemoteMacAdress", GetMacClient(this.Adress));
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.is));
        this.inputStreamReader = new InputStreamReader(this.is);
        this.contentZapros = "";
        this.session=session;
    }

    /**
     * Закрытие соединения
     * @throws IOException
     */
    public void close() throws IOException {
        if (socket.isConnected() == false) {
            return;
        }
        is.close();
        os.close();
        socket.close();
    }

    /**
     * Получить статус подключения (устарело)
     * @return
     */
    public boolean getStatusExut() {
        return isExit;
    }

    /***
     * Изменить статус подключения (устарело)
     * @return
     */
    public boolean exit() {
        isExit = !isExit;
        return isExit;
    }

    /***
     * Чтение текста от терминального клиента
     * @return
     * @throws IOException
     */
    public StringBuffer readText() throws IOException {
        int subcharInt;
        StringBuffer sbSubTmp = new StringBuffer();
        StringBuffer sbSub = new StringBuffer();
        while ((subcharInt = is.read()) != -1) {
            if (socket.isConnected() == false) break;
            if (subcharInt == 0) break;
            sbSubTmp.append((char) subcharInt);
            if (sbSubTmp.toString().indexOf("\n") != -1) {
                if (sbSubTmp.toString().length() == 2) {
                    break; // чтение заголовка окончено
                }
                sbSubTmp.setLength(0);
            }
            sbSub.append((char) subcharInt);
        }
        return sbSub;
    }

    /**
     * Получить МАС адрес клиента (работает с ошибками - применять не стоит)
     *
     * @param adress
     * @return
     */
    public String GetMacClient(String adress) {
        String macStr = null;
        try {
            InetAddress address = InetAddress.getByName(adress);
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni != null) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    macStr = sb.toString();
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        return macStr;
    }

    /**
     *  Отправка текста как HTML страницу (Устарело)
     * @param Head
     * @param content
     */
    public void sendHtml(HttpResponse Head, String content) {
        try {
            OutputStream os = Head.os;
            os.write("HTTP/1.1 200 OK\r\n".getBytes());
            // дата создания в GMT
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            // Длина файла
            os.write(("Content-Length: " + content.length() + "\r\n").getBytes());
            os.write(("Content-Type: text/html; charset=utf-8\r\n").getBytes());
            // Остальные заголовки
            os.write("Access-Control-Allow-Origin: *\r\n".getBytes());
            os.write("Access-Control-Allow-Credentials: true\r\n".getBytes());
            os.write("Access-Control-Expose-Headers: FooBar\r\n".getBytes());
            os.write("Connection: close\r\n".getBytes());
            os.write("Server: HTMLserver\r\n\r\n".getBytes());
            os.write(content.getBytes(Charset.forName("UTF-8")));
            os.write(0);
            os.flush();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     *  Отправка бинарного файла клиенту (для браузера)
     * @param Head
     * @param pageFile
     * @throws IOException
     */
    public void sendFile(HttpResponse Head, File pageFile) throws IOException {
        System.out.println(pageFile.getAbsolutePath());
        String TypeCont = ContentType(pageFile);
        byte[] data = Files.readAllBytes(Paths.get(pageFile.getAbsolutePath()));
        os.write("HTTP/1.1 200 OK\r\n".getBytes());
        // дата создания в GMT
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        // Длина файла
        os.write(("Content-Length: " + data.length + "\r\n").getBytes());
        os.write(("Content-Type: " + TypeCont + "; charset=utf-8\r\n").getBytes());
        os.write(("Last-Modified: " + df.format(new Date(pageFile.lastModified())) + "\r\n").getBytes());
        // Остальные заголовки
        os.write("Access-Control-Allow-Origin: *\r\n".getBytes());
        os.write("Access-Control-Allow-Credentials: true\r\n".getBytes());
        os.write("Access-Control-Expose-Headers: FooBar\r\n".getBytes());
        os.write("Connection: close\r\n".getBytes());
        os.write("Server: HTMLserver\r\n\r\n".getBytes());
        os.write(data);
        os.write(0);
        os.flush();
    }
    /**
     * Отправка JSON объекта клиенту браузеру
     * @param json
     */
    public void sendJson(JSONObject json) {
        sendJson( json.toString());
    }

    /**
     * Отправка JSON строки клиенту браузеру
     * @param jsonObject
     */
    public void sendJson(String jsonObject) {
        try {
            os.write("HTTP/1.1 200 OK\r\n".getBytes());
            // дата создания в GMT
            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            // Длина файла
            os.write(("Content-Length: " + jsonObject.length() + "\r\n").getBytes());
            os.write(("Content-Type: application/x-javascript; charset=utf-8\r\n").getBytes());
            // Остальные заголовки
            os.write("Access-Control-Allow-Origin: *\r\n".getBytes());
            os.write("Access-Control-Allow-Credentials: true\r\n".getBytes());
            os.write("Access-Control-Expose-Headers: FooBar\r\n".getBytes());
            os.write("Connection: close\r\n".getBytes());
            os.write("Server: HTMLserver\r\n\r\n".getBytes());
            //Log.d("TAG", jsonObject);
            os.write(jsonObject.getBytes(Charset.forName("UTF-8")));
            // os.write(jsonObject.getBytes(), 0, jsonObject.length());
            os.write(0);
            os.flush();
            // завершаем соединение
            // System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(HttpSrv.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    /***
     *  Отправляем заголовок HTML страниц
     * @param contentType - Указываем тип ответа для  интерпритации браузером (MIME type)
     * @throws IOException
     */
    public void Head(String contentType) throws IOException {
        os.write("HTTP/1.1 200 OK\r\n".getBytes());
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String typeCont = "text/html";
        if(contentType.length()==0) {
            if (request.has("ContentType")) {
                typeCont = request.getString("ContentType");
            }
        }else if (contentType.indexOf(".") != -1) {
            typeCont = ContentType(new File(contentType));
        }
        os.write(("Content-Type: " + typeCont + "; charset=utf-8\r\n").getBytes());
        os.write("Access-Control-Allow-Origin: *\r\n".getBytes());
        os.write("Access-Control-Allow-Credentials: true\r\n".getBytes());
        os.write("Access-Control-Expose-Headers: FooBar\r\n".getBytes());
        os.write("Connection: close\r\n".getBytes());
        os.write("Server: HTMLserver\r\n\r\n".getBytes());
    }

    /***
     * Отправляем заголовок HTML страниц (MIME тип по умолчанию "text/html" )
     * @throws IOException
     * @throws JSONException
     */
    public void Head() throws IOException, JSONException {
        Head("");
    }

    /**
     * Добавляем фрагмент HTML страницы
     * @param content
     * @throws IOException
     * @throws JSONException
     */
    public void Body(String content) throws IOException {
        os.write(content.getBytes(Charset.forName("UTF-8")));
    }

    /**
     *  Добавляем текст в тело HTML ответа
     * @throws IOException
     */
    public void End() throws IOException {
        os.write(0);
        os.flush();
    }

    /**
     * Отправляем текст ответа клиенту, при терминальном подключении к серверу
     * @param text
     * @throws IOException
     */
    public void write(String text) throws IOException {
        os.write(text.getBytes());
        os.write(0);
        os.flush();
    }

    /**
     * Отправляем бинарный ответ клиенту, при терминальном подключении к серверу
     * @param data
     * @throws IOException
     */
    public void write(byte[] data) throws IOException {
        os.write(data);
        os.write(0);
        os.flush();
    }

}
