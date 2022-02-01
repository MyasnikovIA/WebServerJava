<div extend="Ext.panel.Panel" >
    <cmpScript>
        <![CDATA[
                var ocxID = "ocx_"+this.id;
                chatHtml='<object id="'+ocxID+'" type="application/nptest-plugin" height="100%" width="100%"></object>';
                var panel = this;
                if (panel.body) {
                    panel.body.insertHtml("beforeEnd", chatHtml);
                } else {
                    panel.html += chatHtml;
                }
                panel['ocx']= eval(ocxID);
                panel.ipaddress = "";// если камера установлена в локальной сети и страница запускается в ней
                panel.hostport = 36006;// Порт IP камеры
                panel.serverIP = "xmeye.net";// сервер произвадителя IP камеры, через него транслируется видеопоток
                panel.serverPort = 8000;// порт сервера производителя
                panel.username = '';
                panel.password = '';
                // try {
                //     var comActiveX = new ActiveXObject("WEB.WebCtrl.1");
                // } catch (e) {
                //     console.log('Ошибка инициализации ActiveXObject:', e);
                // }
                panel.runViewCam = function(mac) {
                     panel['ocx'].SetSpecialParamEx2(2, "Russian", 0, 0);// выбор языка
                     var resConnect;
                     if (typeof(mac) ==='undefined') {
                        resConnect =  panel['ocx'].Login(panel.ipaddress, panel.hostport, panel.username, panel.password);
                     } else {
                        resConnect =  panel['ocx'].Login(mac, panel.hostport, panel.username, panel.password);
                     }
                     if (resConnect > 0) {
                        // panel['ocx'].SetSpecialParam(2);
                        // panel['ocx'].PlayAll();
                        // panel['ocx'].StartRealPlay(0,0,0);
                     }else{
                        alert("error: "+resConnect);
                     }
                     panel['ocx'].PlayAll();
                }
                panel.getRecord = function() { //Запись
                    panel['ocx'].ShowPlayBack();
                }
                panel.getViewCam = function(numCam) { // показать только одну камеру
                    panel['ocx'].ChangeShowWndNum(numCam,true,0,0);
                }
                panel.getViewAllCam = function(numCam) { //все камеры
                    panel['ocx'].PlayAll();;
                }
                panel.getFullscreen = function(numCam) { //полный экран
                    panel['ocx'].PlayAll();;
                }
                panel.getSetupCam  = function(numCam) { //Setup IpCam
                    panel['ocx'].ShowDeviceConfig();
                }
                panel.getConfigCam = function(numCam) { //Config IpCam
                    panel['ocx'].ShowDeviceConfig();
                }
                panel.getLogoutCam = function(numCam) { //Config IpCam
                    panel['ocx'].Logout();
                }
                var nameCtrl ;
                if (typeof(arguments[0]['name']) !== 'undefined') {
                     nameCtrl = arguments[0]['name'];
                } else{
                    nameCtrl = "Ctrl_"+window.uuidv4();
                }
                var mainInitObject=arguments[0]['mainInitObject'];
                var ctrl = eval("window.Win_"+mainInitObject+"_ListCotrl");
                ctrl[nameCtrl]=panel;
        ]]>
    </cmpScript>
</div>