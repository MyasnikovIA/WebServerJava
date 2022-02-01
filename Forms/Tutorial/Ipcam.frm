<div title="Примеры использования контролов"   >
    <cmpScript>
        <![CDATA[
            Form.okAuth = function() {
               setVisible('login', false);
               var confVar = {}
               confVar['IpAddress'] = getValue("IpAddress");
               confVar['PortIp'] = getValue("PortIp");
               confVar['UserName'] = getValue("UserName");
               confVar['UserPass'] = getValue("UserPass");
               var ocx = getControl('MyCam');
               ocx.ipaddress = confVar['IpAddress'];
               ocx.hostport = confVar['PortIp'];
               ocx.username = confVar['UserName'];
               ocx.password = confVar['UserPass'];
               ocx.runViewCam();
               localStorage.setItem('ipConfig',JSON.stringify(confVar));
            }

            Form.runViewCam = function() {
                var ocx = getControl('MyCam');
                ocx.runViewCam();
            }

            Form.onLogin = function() {
               getControl('login').show();
            }

            Form.getRecord = function() {
                var ocx = getControl('MyCam');
                ocx.getRecord();
            }
            Form.getAllcam = function(){
                var ocx = getControl('MyCam');
                ocx.getViewCam(25);
                // ocx.getViewAllCam()
            }
            Form.get_9_cam = function(){
                var ocx = getControl('MyCam');
                ocx.getViewCam(9);
            }
            Form.getTest = function(){
                var ocx = getControl('MyCam');
                ocx['ocx'].StartRealPlay(1,0,0);
            }

            Form.Login = function() {
                setVar("conf",localStorage.getItem('ipConfig'));
                if (getVar("conf")==null){
                   getControl('login').show();
                } else {
                   try {
                      var confVar = JSON.parse(getVar("conf"))
                      var ocx = getControl('MyCam');
                      ocx.ipaddress = confVar['IpAddress'];
                      ocx.hostport = confVar['PortIp'];
                      ocx.username = confVar['UserName'];
                      ocx.password = confVar['UserPass'];
                      setValue("IpAddress",confVar['IpAddress']);
                      setValue("PortIp",confVar['PortIp']);
                      setValue("UserName",confVar['UserName']);
                      setValue("UserPass",confVar['UserPass']);
                      ocx.runViewCam();
                   } catch(e) {
                      setVar("conf",null);
                   }
                }
            }
            console.log("this",this);

            Form.Login();
        ]]>
    </cmpScript>
    <cmpPanel pos="left" title="Список камер" width= "250" flex="2"  collapsible="true"  >
        <cmpButton caption="Login"             onclick="Form.onLogin()"/>
        <cmpButton caption="Включить камеры"   onclick="Form.runViewCam()"/>
        <cmpButton caption="Архив записей"     onclick="Form.getRecord()"/>
        <cmpButton caption="Все камеры"        onclick="Form.getAllcam()" />
        <cmpButton caption="9 камер"           onclick="Form.get_9_cam()" />
        <item/>
        <cmpButton caption="1"     onclick="Form.getTest()" />
    </cmpPanel>
    <cmpIpCam pos="center"  name="MyCam" title="Панфиловцев 6" width= "250" flex="4" />

    <cmpWindow name="login" title="Авторизация" modal="true">
        <cmpPanel pos="up" >
            <cmpLabel caption="Address"/>
            <cmpTextField  name="IpAddress"  value=""  width="250"/>
            <cmpLabel caption="Port"/>
            <cmpNumberField  name="PortIp"  value="36006"  width="250" maxValue="66000" minValue="0"/>
            <cmpLabel caption="User"/>
            <cmpTextField  name="UserName"  value=""  width="250"/>
            <cmpLabel caption="Pass"/>
            <cmpTextField  name="UserPass" value=""  inputType="password"  width="250"/>
        </cmpPanel>
        <cmpButton caption="Запустить"     onclick="Form.okAuth()" />
    </cmpWindow>

</div>
