package com.miacomsoft;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args){
        HttpSrv srv = new HttpSrv(9092);
        jsonForm frm = new jsonForm(new File("Forms").getAbsolutePath());

        srv.onPage((HttpResponse Head) -> {
            if (Head.contentZapros.length() == 0) {
                ArrayList<String> txt2 = frm.getParsedForm("index.html", Head.requestParam);
                Head.Head(txt2.get(1));
                Head.Body(txt2.get(0));
                Head.End();
                return;
            }

            if (frm.existFormContent(Head.contentZapros)) {
                ArrayList<String> txt2 = frm.getParsedForm(Head.contentZapros, Head.requestParam);
                Head.Head(txt2.get(1));
                Head.Body(txt2.get(0));
                Head.End();
                return;
            }

            if (frm.existContent(Head.contentZapros)) {
                System.out.println("Head.contentZapros " + Head.contentZapros);
                Head.Head(frm.mimeType(Head.contentZapros));
                Head.Body(frm.readContent(Head.contentZapros));
                Head.End();
                return;
            }
            if (Head.contentZapros.indexOf("openstreetmap/") != -1) {
                // /openstreetmap/{s}/{z}/{x}/{y}.png
                String[] param = Head.contentZapros.split("/");
                if (param.length == 5) {
                    File cmpFiletmp = new File(frm.getWWW_DIR().getAbsolutePath() + "/" + Head.contentZapros);
                    if (!cmpFiletmp.exists()) {
                        String url = "https://" + param[1] + ".tile.openstreetmap.org/" + param[2] + "/" + param[3] + "/" + param[4];
                        srv.downloadHttpsFile(url, cmpFiletmp.getAbsolutePath());
                    }
                    byte[] data = frm.readContent(Head.contentZapros);
                    Head.Head(frm.mimeType(Head.contentZapros));
                    Head.Body(data);
                    Head.End();
                }
            }
        });
        srv.start();
    }
}