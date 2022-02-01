package com.miacomsoft;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class jsonForm {

    // Шаблон JS файла для динамической загрузки ExtJS формы из JS функции  "openForm"
    String TEMP_JS_FORM = " Ext.onReady(function() {\n" +
            " if (typeof(window.Win_{%frmObj%}) === 'undefined') window.Win_{%frmObj%} = {};\n" +
            " if (typeof(window.Win_{%frmObj%}_ListCotrl) === 'undefined') window.Win_{%frmObj%}_ListCotrl = {};\n" +
            " var {%frmObj%} = {%};\n" +
            " window.Win_{%frmObj%} = Ext.create('{%ExtClass%}',{%frmObj%});\n" +
            " window.Win_{%frmObj%}{%showWin%};\n" +
            " registerWinForm('{%formName%}', window.Win_{%frmObj%});\n" +
            " ExtensionMainList(window.Win_{%frmObj%},window.Win_{%frmObj%}_ListCotrl);\n" +
            " if ( typeof(window.Win_{%frmObj%}['vars']) !=='undefined') window.Win_{%frmObj%}['vars'] = {};\n" +
            " window.Win_{%frmObj%}[\"vars\"]//=[[%DataVars%]];\n" +
            " {%cmpScript%}\n" +
            "});";


    String TEMP_HTML_FORM = "" +
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "   <head>\n" +
            "        <meta charset=\"utf-8\"/>\n" +
            "        <link href=\"/lib/ExtJS_6.2.0/classic/theme-classic/resources/theme-classic-all.css\" rel=\"stylesheet\" />\n" +
            "        <script type=\"text/javascript\" src=\"/lib/ExtJS_6.2.0/ext-all.js\"></script>\n" +
            "        <script type=\"text/javascript\" src=\"/lib/common.js\"></script>\n" +
            "        <script type = \"text/javascript\">\n" +
            "             document.addEventListener('contextmenu', function(event){event.preventDefault();});\n" +
            "             {%}\n" +
            "        </script>\n" +
            "   </head>\n" +
            "   <body></body>\n" +
            "</html>";

    String TEMP_JS_WIDGET = "" +
            "{%cmpDataset%}\n" +
            "window.Win_{%frmObj%} = {%}; \n" +
            "window.Win_{%frmObj%}['constructor'] = function() {\n" +
            "   this.renderTo = Ext.getBody();\n" +
            "   this.callParent(arguments);\n" +
            "   window.Win_{%frmObj%} = this;\n" +
            "   window.Win_{%frmObj%}.config = arguments[0];\n" +
            "   {%cmpScript%}\n" +
            "}  \n" +
            "Ext.define('widget.{%widgetName%}',window.Win_{%frmObj%});\n" +
            "\n" +
            "{%style%}";

    private File ROOT_DIR = new File(""); // Корневая папка
    private File FORM_DIR = new File("Forms");// Каталог расположения форм
    private File USER_DIR = new File("UserForms"); // Каталог фрагментами для переопределения форм
    private File WWW_DIR = new File("www"); // Каталог фрагментами для переопределения форм


    private jsonForm() {
        cteateSystemDir();
    }

    public jsonForm(String FORM_DIR) {
        this.FORM_DIR = new File(FORM_DIR);
        cteateSystemDir();
    }

    public jsonForm(String FORM_DIR, String WWW_DIR) {
        this.FORM_DIR = new File(FORM_DIR);
        this.WWW_DIR = new File(WWW_DIR);
        cteateSystemDir();
    }

    public jsonForm(String FORM_DIR, String WWW_DIR, String USER_DIR) {
        this.FORM_DIR = new File(FORM_DIR);
        this.WWW_DIR = new File(WWW_DIR);
        this.USER_DIR = new File(USER_DIR);
        cteateSystemDir();
    }

    /**
     * Функция создания каталогов , которые необходимы для проекта
     */
    private void cteateSystemDir() {
        if (!FORM_DIR.exists()) FORM_DIR.mkdirs();
        if (!WWW_DIR.exists()) WWW_DIR.mkdirs();
        if (!USER_DIR.exists()) USER_DIR.mkdirs();
    }


    public File getWWW_DIR() {
        return WWW_DIR;
    }

    /**
     * ПРоверка наличия контента в каталоге
     *
     * @param url
     * @return
     */
    public boolean existContent(String url) {
        String ext = url.substring(url.lastIndexOf(".") + 1).toLowerCase();
        String formNameBody = url;
        if (ext.length() == 0) {
            ext = "frm";
        } else {
            formNameBody = url.substring(0, url.lastIndexOf("."));
        }

        File cmpFiletmp = new File(WWW_DIR.getAbsolutePath() + "/" + url);
        File cmpFilesrc = new File(FORM_DIR.getAbsolutePath() + "/" + formNameBody + "." + ext);
        if ((url.indexOf("widget/") != -1) && (ext.equals("js"))) {
            formNameBody = formNameBody.split("widget/")[1];
            cmpFilesrc = new File(cmpFilesrc.getAbsolutePath().substring(0, url.lastIndexOf(".")) + ".frm");
            cmpFiletmp = new File(WWW_DIR + "/" + "widget/" + formNameBody + "." + ext);
        }
        if (cmpFilesrc.exists() && cmpFiletmp.exists()) {
            if (cmpFilesrc.lastModified() > cmpFiletmp.lastModified()) {
                return false;
            }
        }
        return cmpFiletmp.exists();
    }

    /**
     * Проверка наличия исходного XML файла
     *
     * @param url
     * @return
     */
    public boolean existFormContent(String url) {
        if (url.indexOf("widget/") != -1) {
            url = "widget/" + url.split("widget/")[1];
            url = url.substring(0, url.lastIndexOf(".")) + ".frm";
        }
        File cmpFiletmp = new File(FORM_DIR.getAbsolutePath() + "/" + url);
        return cmpFiletmp.exists();
    }

    /***
     * Прочитать содержимое файла из каталога www
     * @param url
     * @return
     * @throws IOException
     */
    public byte[] readContent(String url) throws IOException {
        File cmpFiletmp = new File(WWW_DIR.getAbsolutePath() + "/" + url);
        return Files.readAllBytes(Paths.get(cmpFiletmp.getAbsolutePath()));
    }

    /**
     * Функция получения ExtJS форм из каталога Form
     *
     * @param formName
     * @param data
     * @return
     */
    public ArrayList<String> getParsedForm(String formName, JSONObject data) {
        return getSrcSaveTemp(formName, data);
    }


    /***
     * Функция сохранения текста в файл , с созданием директорий
     * @param cmpFiletmp
     * @param htmlText
     */
    public void writeFile(File cmpFiletmp, String htmlText) {
        try {
            if (!cmpFiletmp.getParentFile().exists()) {
                cmpFiletmp.getParentFile().mkdirs();
            }
            FileWriter myWriter = new FileWriter(cmpFiletmp.getAbsolutePath());
            myWriter.write(htmlText);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция сохранения бинарных данных в файл, с созданием директорий
     *
     * @param cmpFiletmp
     * @param data
     */
    public void writeFile(File cmpFiletmp, byte[] data) {
        FileOutputStream fos = null;
        try {
            if (!cmpFiletmp.getParentFile().exists()) {
                cmpFiletmp.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(cmpFiletmp.getAbsolutePath());
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * Стение текстового файла
     *
     * @param cmpFiletmp
     * @return
     */
    private String readFile(File cmpFiletmp) {
        try {
            return new String(Files.readAllBytes(Paths.get(cmpFiletmp.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Функция получения сгенерированной HTML страницы из XML кода. Если страница не созданна, тогда запускается процес его создания из исходного XML файла
     *
     * @param formName
     * @param data
     * @return
     */
    private ArrayList<String> getSrcSaveTemp(String formName, JSONObject data) {
        // System.out.println("================================");
        // System.out.println("data.toString() "+data.toString());
        // System.out.println("ROOT_DIR "+ROOT_DIR.getAbsolutePath());
        ArrayList<String> res = new ArrayList<String>();
        String blockName = "";
        int isHtml = 0;
        if (blockName.indexOf(":") != -1) {
            blockName = formName.split(":")[0];
            formName = formName.split(":")[1];
        }
        String ext = formName.substring(formName.lastIndexOf(".") + 1).toLowerCase();
        String formNameBody = formName;
        if (ext.length() == 0) {
            ext = "frm";
        } else {
            formNameBody = formName.substring(0, formName.lastIndexOf("."));
        }
        if (formName.indexOf("widget/") != -1) {
            ext = "js";
            isHtml = 2;
        }
        if (data.has("type")){
            if (data.getString("type").equals("js")){
                ext = "js";
                isHtml = 1;
            }
        }
        File cmpFiletmp = new File(WWW_DIR.getAbsolutePath() + "/" + formNameBody + "." + ext);
        File cmpFilesrc = new File(FORM_DIR.getAbsolutePath() + "/" + formName);

        if ((formName.indexOf("widget/") != -1) && (ext.equals("js"))) {
            formNameBody = formNameBody.split("widget/")[1];
            cmpFilesrc = new File(cmpFilesrc.getAbsolutePath().substring(0, formName.lastIndexOf(".")) + ".frm");
            cmpFiletmp = new File(WWW_DIR + "/" + "widget/" + formNameBody + blockName + "." + ext);
        }
        String mime = mimeType(new File("." + ext));
        String htmlText = "";
        if (!cmpFiletmp.getParentFile().exists()) {
            cmpFiletmp.getParentFile().mkdirs();
        }
        if ((cmpFiletmp.exists()) && (cmpFilesrc.exists())) { // Проверить дату исходного файла
            if (cmpFilesrc.lastModified() > cmpFiletmp.lastModified()) {
                htmlText = getSrc(formName, data);
                if (isHtml==0){
                    htmlText = TEMP_HTML_FORM.replace("{%}", htmlText);
                }
                writeFile(cmpFiletmp, htmlText);
            } else {
                htmlText = readFile(cmpFiletmp);
            }
        }
        if (htmlText.length() == 0) {
            htmlText = getSrc(formName, data);
            if (isHtml==0){
                htmlText = TEMP_HTML_FORM.replace("{%}", htmlText);
            }
            writeFile(cmpFiletmp, htmlText);
        }

        if (data.has("data")){
            String dataStr = data.getString("data");
            JSONObject dataTmp = new JSONObject(dataStr);
            if (dataTmp.has("vars")){
                data = dataTmp.getJSONObject("vars");
            }
        }
        htmlText = htmlText.replace("//=[[%DataVars%]]","="+data.toString());
        res.add(htmlText);
        res.add(mime);
        return res;
    }

    /**
     * Функция перобразования исходлного XML документа в HTML страницу
     *
     * @param formName
     * @param data
     * @return
     */
    private String getSrc(String formName, JSONObject data) {
        String ext = formName.substring(formName.lastIndexOf(".") + 1).toLowerCase();
        String widgetName = "";
        int isHtml = 0;
        if ((data.has("isModal") && (data.getBoolean("isModal") == true))) {
            isHtml = 1;
        } else if (formName.indexOf("widget/") != -1) {
            formName = "widget/" + formName.substring(0, formName.lastIndexOf(".")).split("widget/")[1] + ".frm";
            widgetName = formName.substring(0, formName.lastIndexOf(".")).split("widget/")[1].toLowerCase();
            ext = "js";
            isHtml = 2;
        }
        if (data.has("type")){
            if (data.getString("type").equals("js")) {
                ext = "js";
                isHtml = 1;
            }
        }
        if (data.has("data")){
            data = new JSONObject(data.getString("data"));
        }
        String frmObj = formName.replace("/", "_").replace(".", "").replace("-", "_");
        JSONArray ServerPathQuery = new JSONArray();
        ServerPathQuery.put(formName);
        if (data.has("ServerPathQuery")) {
            ServerPathQuery = data.getJSONArray("ServerPathQuery");
            data.remove("ServerPathQuery");
        }
        String blockName = "";
        if (blockName.indexOf(":") != -1) {
            blockName = formName.split(":")[0];
            formName = formName.split(":")[1];
        }
        File pathForm, pathUserForm;
        pathForm = new File(FORM_DIR.getAbsolutePath() + "/" + formName);
        pathUserForm = new File(USER_DIR.getAbsolutePath() + "/" + formName);
        if (pathUserForm.exists()) {
            pathForm = pathUserForm;
        }
        Document docSrc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream is = new FileInputStream(pathForm);
            docSrc = db.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // docSrc.getDocumentElement();
        // docSrc.getDocumentElement().normalize();
        // docSrc.getDocumentElement();
        JSONObject ListCotrl = new JSONObject();
        JSONObject jsonFrm = parseElement(docSrc.getDocumentElement(), null, ListCotrl);
        String cmpScript = parseCmpScript(docSrc);
        String styleText = parseStyle(docSrc);
        // ArrayList<String> cmpDataSet = parseCmpDataSet(docSrc, "");
        // NodeList cmpAction = docSrc.getElementsByTagName("cmpAction");
        String jsonDataset = "";
        jsonFrm.put("mainList", ListCotrl);
        jsonFrm.put("mainFile", formName);
        jsonFrm.put("vars_return",new JSONObject());
        jsonFrm.put("vars",new JSONObject());
        if ((isHtml == 1) || (isHtml == 0)) { // создаем HTML или JS файл формы
            String extClass = "Ext.Viewport";
            String showWin = "";
            if (data.has("isModal")) {
                jsonFrm.put("modal", true);
                extClass = "Ext.window.Window";
                showWin = ".show()";
                if (!jsonFrm.has("width")){
                    jsonFrm.put("width",800);
                }
                if (!jsonFrm.has("height")){
                    jsonFrm.put("height",600);
                }
            }
            if (!jsonFrm.has("layout")) {
                jsonFrm.put("layout", "border");
            }
            if (!data.has("'renderTo'")) {
                jsonFrm.put("renderTo", "Ext.getBody()");
            }
            String jsonFrmTxt = jsonFrm.toString(4);
            String html = TEMP_JS_FORM.replace("{%}", jsonFrmTxt).replace("{%ExtClass%}", extClass)
                    .replace("{%cmpDataset%}", jsonDataset)
                    .replace("{%frmObj%}", frmObj)
                    .replace("{%formName%}", formName)
                    .replace("{%cmpScript%}", cmpScript)
                    .replace("{%style%}", styleText)
                    .replace("{%showWin%}", showWin);
            html = jsonFunFromString(html, frmObj);
            return html;
        }
        if (isHtml == 2) { // создаем JS файл widget
            if (!jsonFrm.has("extend")) {
                jsonFrm.put("extend", "Ext.panel.Panel");
            }
            String jsonFrmTxt = jsonFrm.toString(4);
            String html = TEMP_JS_WIDGET.replace("{%}", jsonFrmTxt)
                    .replace("{%widgetName%}", widgetName)
                    .replace("{%cmpDataset%}", jsonDataset)
                    .replace("{%frmObj%}", frmObj)
                    .replace("{%formName%}", formName)
                    .replace("{%cmpScript%}", cmpScript)
                    .replace("{%style%}", styleText);
            html = jsonFunFromString(html, frmObj);
            return html;
        }
        return "";
    }

    /**
     * Фнукция в которую вынесен список автозаменяемых фрагментов
     *
     * @param html
     * @param frmObj
     * @return
     */
    private String jsonFunFromString(String html, String frmObj) {
        String thisName = "window.Win_" + frmObj;
        return html.replace("setVar(", "setVar(" + thisName + ",")
                .replace("getVar(", "getVar(" + thisName + ",")
                .replace("setValue(", "setValue(" + thisName + ",")
                .replace("getValue(", "getValue(" + thisName + ",")
                .replace("setCaption(", "setCaption(" + thisName + ",")
                .replace("openForm(", "openForm(" + thisName + ",")
                .replace("getControl(", "getControl(" + thisName + ",")
                .replace("getDataSet(", "getDataSet(" + thisName + ",")
                .replace("showPopupMenu(", "showPopupMenu(" + thisName + ",")
                .replace("refreshDataSet(", "refreshDataSet(" + thisName + ",")
                .replace("executeAction(", "executeAction(" + thisName + ",")
                .replace("setVisible(", "setVisible(" + thisName + ",")
                .replace("setDisable(", "setDisable(" + thisName + ",")
                .replace("close(", "close(" + thisName + ",")
                .replace("openWindow(", "openWindow(" + thisName + ",")
                .replace("\"Ext.getBody()\"", "Ext.getBody()")
                .replace("Form.", thisName + ".")
                .replace("(--##--)\"", "")
                .replace("\"(--##--)", "")
                ;
    }

    /***
     * Обработка тэгов сmpDataSet в которых содержится список переменных и исполняемый скрипт , для выполнения на стороне сервера
     * @param docSrc
     * @param nodeName
     * @return
     */
    private ArrayList<String> parseCmpDataSet(Document docSrc, String nodeName) {
        ArrayList<String> res = new ArrayList<String>();
        NodeList cmpDataSet = docSrc.getElementsByTagName("cmpDataSet");
        for (int i = 0; i < cmpDataSet.getLength(); i++) {
            Node node = cmpDataSet.item(i);
            if (node != null) {
                if ((nodeName.length() > 0) && (!node.getAttributes().getNamedItem("name").getNodeValue().equals(nodeName)))
                    continue;

            }
        }
        return res;
    }

    /***
     * Обработка тэгов в которых содержится список переменных и исполняемый скрипт , для выполнения на стороне сервера
     * @param docSrc
     * @param nodeName
     * @return
     */
    private ArrayList<String> parseCmpAction(Document docSrc, String nodeName) {
        ArrayList<String> res = new ArrayList<String>();
        NodeList cmpDataSet = docSrc.getElementsByTagName("cmpDataSet");
        for (int i = 0; i < cmpDataSet.getLength(); i++) {
            Node node = cmpDataSet.item(i);
            if (node != null) {
                if ((nodeName.length() > 0) && (!node.getAttributes().getNamedItem("name").getNodeValue().equals(nodeName)))
                    continue;

            }
        }
        return res;
    }

    /**
     * Обработка  cmpScript в XML
     *
     * @param docSrc
     * @return
     */
    private String parseCmpScript(Document docSrc) {
        StringBuffer sb = new StringBuffer();
        NodeList cmpScript = docSrc.getElementsByTagName("cmpScript");
        for (int i = 0; i < cmpScript.getLength(); i++) {
            Node node = cmpScript.item(i);
            if (node != null) {
                NamedNodeMap attributes = node.getAttributes();
                int numAttrs = attributes.getLength();
                for (int j = 0; j < numAttrs; j++) {
                    Attr attr = (Attr) attributes.item(j);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                }
                sb.append(node.getTextContent());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Функция склеивания текста содержащегося в тэгах style, для дальнейшей инициализации HTML стиля через JS код
     *
     * @param docSrc
     * @return JS код , который необходимо поместить на HTML форму
     */
    private String parseStyle(Document docSrc) {
        StringBuffer sb = new StringBuffer();
        NodeList cmpScript = docSrc.getElementsByTagName("style");
        for (int i = 0; i < cmpScript.getLength(); i++) {
            Node node = cmpScript.item(i);
            if (node != null) {
                NamedNodeMap attributes = node.getAttributes();
                int numAttrs = attributes.getLength();
                for (int j = 0; j < numAttrs; j++) {
                    Attr attr = (Attr) attributes.item(j);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                }
                sb.append(node.getTextContent());
                sb.append("\n");
            }
        }
        if (sb.toString().length() == 0) return "";
        return "\n var css = `" + sb.toString() + "`,head = document.head || document.getElementsByTagName('head')[0],style = document.createElement('style'); head.appendChild(style); style.type = 'text/css'; if (style.styleSheet) {style.styleSheet.cssText = css;} else {style.appendChild(document.createTextNode(css));}\n";
    }

    /***
     * Определение класса ExtJS объекта по имени тэга
     * @param res - объект в который помещается результат
     * @param root - объект исходник
     * @param rootParent - родительский объект, если родительского объекта нет, тогда инициализация класса пропускается
     */
    private void getTagName(JSONObject res, Object root, JSONObject rootParent) {
        String tagName = "";
        if (root.getClass().getSimpleName().equals("DeferredElementImpl")) {
            tagName = ((Element) root).getNodeName();
            if (rootParent == null) {
                res.put("layout", "border");
                String uuid = UUID.randomUUID().toString().replace("-", "_");
                res.put("id", "win" + uuid);
                res.put("mainForm", res.getString("id"));
                // res.put("mainFormObject", "window.Win_{%frmObj%}");
                res.put("mainInitObject", "{%frmObj%}");
                return;
            }

            if ((tagName.length() > 3) && (tagName.substring(0, 3).equals("cmp"))) {
                res.put("xtype", tagName.substring(3, tagName.length()).toLowerCase());
            } else {
                if ((tagName.toLowerCase().equals("item")) || (tagName.toLowerCase().equals("items"))) return;
                res.put("autoEl", tagName);
            }
            if (rootParent.has("mainForm")) {
                res.put("mainForm", rootParent.getString("mainForm"));
            }
            //if (rootParent.has("mainFormObject")){
            //    res.put("mainFormObject", rootParent.getString("mainFormObject"));
            //}
            if (rootParent.has("mainInitObject")) {
                res.put("mainInitObject", rootParent.getString("mainInitObject"));
            }
            // System.out.println("rootParent " + rootParent);
            // System.out.println("root " + root);
        }
    }

    private static String getChildElementValue(final String tagName, final Element element) {
        Node tagNode = element.getElementsByTagName(tagName).item(0);
        if (tagNode == null)
            return null;
        NodeList nodes = tagNode.getChildNodes();
        Node node = (Node) nodes.item(0);

        return node.getNodeValue();
    }

    /***
     * Функция  преобразования  атрибутов XML тэга в параметры JSON объекта
     * @param res
     * @param root
     */
    private void parseAttribute(JSONObject res, Object root, JSONObject ListCotrl) {
        if (root.getClass().getSimpleName().equals("DeferredElementImpl")) {
            NamedNodeMap attributes = ((Element) root).getAttributes();
            int numAttrs = attributes.getLength();
            for (int j = 0; j < numAttrs; j++) {
                Attr attr = (Attr) attributes.item(j);
                String attrName = attr.getNodeName();
                String attrValue = attr.getNodeValue();
                if (attrName.equals("caption")) attrName = "text";
                // (((Element) root).getNodeName() + "   " + attrName + " = " + attrValue);
                if (attrValue.length() == 0) continue;
                if (attrName.equals("listeners")) {
                    if (!res.has("listeners")) {
                        res.put("listeners", new JSONObject());
                    }
                } else if (attrName.substring(0, 2).equals("on")) {
                    if (!res.has("listeners")) {
                        res.put("listeners", new JSONObject());
                    }
                    if (attrName.equals("onshow")) attrName = "onrender";
                    // if (attrName.equals("onclick")) attrName = "onhandler";

                    if ((attrValue.trim().length() > "Form.".length()) && (attrValue.trim().substring(0, "Form.".length()).indexOf("Form.") != -1)) {
                        attrValue = "function(){ " + attrValue + "} ";
                    } else if (attrValue.trim().length() < "function(".length()) {
                        attrValue = "function(){ " + attrValue + "} ";
                    } else if (attrValue.trim().substring(0, "function(".length()).indexOf("function(") == -1) {
                        attrValue = "function(){ " + attrValue + "} ";
                    }
                    res.getJSONObject("listeners").put(attrName.substring(2), "(--##--)" + attrValue + "(--##--)");

                } else if (attrValue.matches("-?\\d+(\\.\\d+)?")) {
                    res.put(attrName, new String("(--##--)" + attrValue + "(--##--)"));
                } else if (attrValue.trim().substring(0, 1).equals("[") && attrValue.trim().substring(attrValue.length() - 1).equals("]")) {
                    res.put(attrName, new JSONArray(attrValue.trim()));
                } else if (attrValue.trim().substring(0, 1).equals("{") && attrValue.trim().substring(attrValue.length() - 1).equals("}")) {
                    res.put(attrName, new JSONObject(attrValue.trim()));
                } else {
                    res.put(attrName, attrValue);
                }
            }
            if (res.has("name")) {
                ListCotrl.put(res.getString("name"), res.getString("name"));
            }
            if (res.has("popupmenu")) {
                if (!res.has("listeners")) {
                    res.put("listeners", new JSONObject());
                }
                String popupmenu = res.getString("popupmenu");
                String menuJsCode = "Ext.get(this.id).on('contextmenu', function(){  let arr = [].slice.call(arguments);  let ctrlPopUp = getControl('" + popupmenu + "'); ctrlPopUp.popUpFun(arguments); ctrlPopUp.showAt(arr[0].getX(),arr[0].getY()); return false; }); ";
                if (res.getJSONObject("listeners").has("render")) {
                    String onRenderFun = res.getJSONObject("listeners").getString("render");
                    onRenderFun = onRenderFun.substring(0, onRenderFun.indexOf("{")) + "{" + menuJsCode + ";\r\n" + onRenderFun.substring(onRenderFun.indexOf("{") + 1);
                    res.getJSONObject("listeners").put("render", onRenderFun);
                } else {
                    res.getJSONObject("listeners").put("render", "(--##--)function(){ " + menuJsCode + "}(--##--)");
                }
            }
            if (res.has("style")) {
                res.put("bodyStyle", res.getString("style"));
                res.remove("style");
            }
            if (res.has("class")) {
                res.put("bodyCls", res.getString("class"));
                res.remove("class");
            }
            if (res.has("pos") && !res.has("region")) {
                JSONObject tagList = new JSONObject("{'up':'north','left':'west','down':'south','right':'east','center':'center'}");
                if (tagList.has(res.getString("pos").toLowerCase())) {
                    res.put("region", tagList.getString(res.getString("pos").toLowerCase()));
                    res.remove("pos");
                }
            }
            if (((Element) root).hasChildNodes()) {
                String html = ((Element) root).getFirstChild().getTextContent();
                if ((html != null) && (html.replace("\r", "").replace("\n", "").trim().length() > 0)) {
                    JSONObject lab = new JSONObject();
                    lab.put("xtype", "label");
                    lab.put("html", html);
                    if (res.has("items")) {
                        res.getJSONArray("items").put(lab);
                    } else {
                        res.put("items", lab);
                    }
                }
                String htmlLast = ((Element) root).getLastChild().getTextContent(); // получить текст после вложенных тэгов
                if ((htmlLast != null) && (html.replace("\r", "").replace("\n", "").trim().length() > 0)) {
                    // JSONObject lab = new JSONObject();
                    // lab.put("xtype","label");
                    // lab.put("html",htmlLast);
                    // if (res.has("items")){
                    //     res.getJSONArray("items").put(lab);
                    // }else{
                    //     res.put("items",lab);
                    // }

                    // if (res.has("html")) {
                    //     res.put("html", res.getString("html") + htmlLast);
                    // } else {
                    //     res.put("html", htmlLast);
                    // }
                }
            }
        }

    }

    /***
     * Функция определения списка тэгов, которые необходимо пропустить
     * @param root
     * @return
     */
    private boolean skipTag(Object root) {
        if (root.getClass().getSimpleName().equals("DeferredElementImpl")) {
            String tagName = ((Element) root).getNodeName();
            if (tagName.equals("cmpAction")) return true;
            if (tagName.equals("cmpDataSet")) return true;
            if (tagName.equals("cmpScript")) return true;
            if (tagName.equals("style")) return true;
        }
        return false;
    }

    /**
     * Конвертируем XML Элемент в JSON объект
     *
     * @param root
     * @param rootParent
     * @return
     */
    private JSONObject parseElement(Object root, JSONObject rootParent, JSONObject ListCotrl) {
        if (skipTag(root)) { // Проверяем список тэгов, которые не обрабатываем в основном объекте
            return null;
        }
        JSONObject res = new JSONObject();
        getTagName(res, root, rootParent); // Инициализируем тип объекта по имени тэга
        parseAttribute(res, root, ListCotrl); // конвертируем  атрибуты тэга в JSON атрибуты (ExtJS параметры)
        parseChildNodes(res, root, rootParent, ListCotrl); // обрабатываем вложенные  тэги
        return res;
    }


    /***
     * Конвертируем вложеные тэги XML документа в JSON массив
     * @param res - результат обработки дописывается в этот объект
     * @param root - обрабатываемый объект, в котором происходит поиск дочерних объектов
     * @param rootParent - родительский объект (устарел и будет удалень)
     */
    private void parseChildNodes(JSONObject res, Object root, Object rootParent, JSONObject ListCotrl) {
        if (root.getClass().getSimpleName().equals("DeferredElementImpl")) {
            if (((Element) root).hasChildNodes()) {
                NodeList nodeList = ((Element) root).getChildNodes();
                JSONArray arr = new JSONArray();
                for (int count = 0; count < nodeList.getLength(); count++) {
                    Node tempNode = nodeList.item(count);
                    if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                        // Обрабатываем вложенные тэги по отдельности (рекурсионно)
                        // System.out.println(" --------14444-----" + tempNode.toString() + "  " + nodeList.getLength() + "--------------------");
                        JSONObject sub = parseElement(tempNode, res, ListCotrl);
                        if (sub != null) {
                            arr.put(sub);
                        }
                    } else {
                        // помещяем текст  между тэгами в свойство HTML
                        if (tempNode.toString().substring("[#text:".length()).indexOf("[#text:") != -1) {
                            // String html = tempNode.getNodeValue();
                            String html = tempNode.getFirstChild().getTextContent();
                            if (html.replace("\r", "").replace("\n", "").trim().length() > 0) {
                                // res.put("html", tempNode.getNodeValue());
                                JSONObject lab = new JSONObject("");
                                lab.put("xtype", "label");
                                lab.put("html", tempNode.getNodeValue());
                                arr.put(lab);
                            }
                        }
                    }
                }
                if ((nodeList.getLength() > 0) && (arr.length() > 0)) {
                    res.put("items", arr);
                }
            }
        }
    }

    /**
     * Получение MIME Типа по расширению файла
     *
     * @param pageFile
     * @return
     */
    public String mimeType(File pageFile) {
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
        if ((rashirenie.equals("frm")) || (rashirenie.equals("html")) || (rashirenie.equals("htm")) || (rashirenie.equals("shtml")) || (rashirenie.equals("shtm")) || (rashirenie.equals("stm")) || (rashirenie.equals("sht"))) {
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

    public String mimeType(String pageFile) {
        return mimeType(new File(pageFile));
    }

}