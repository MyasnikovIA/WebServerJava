<div caption="Примеры использования контролов" >

    <cmpScript>
       <![CDATA[
           Form.activeMap = null;
           Form.onPOP = function(arguments) {
              Form.activeMap = getControl('MyOsm');
              var jsonInfo = Form.activeMap.getInfo();
              setCaption('objectInfo', JSON.stringify(jsonInfo,null, 4))
           }

           // Установить  метку на карте
           Form.onSetLabel = function(){
              var map = getControl('MyOsm');
              map.appLabel("<br/><h3>текст метки</h3>","подсказка",function(){

              });
           };
           Form.onDeleteLabel = function() {
              getControl('MyOsm').delLabel();
           }
           Form.onClrLabel = function() {
              getControl('MyOsm').delLabels();
           }

           //  получить информацию об выбранном гео-объекте
           Form.onGetInfo = function() {
                var jsonInfo = getControl('MyOsm').getInfo()
                setCaption('objectInfo', JSON.stringify(jsonInfo,null, 4))
           }
           Form.onFoundObjectText = function() {
                var map = getControl('MyOsm');
                var jsonResult = map.foundObject(getValue('foundObjectText'))
                for (var ind = 0; ind < jsonResult.length; ++ind) {
                    var tmpObj = {'lat':jsonResult[ind]['lat'],'lng':jsonResult[ind]['lon']};
                    map.appLabel( tmpObj, jsonResult[ind]['display_name']);
                    console.log(  'tmpObj',tmpObj );
                    console.log( ' jsonResult[ind]', jsonResult[ind] );
                }
           }

       ]]>
    </cmpScript>
    <cmpPanel pos="left" >
        <cmpTextField  name="MyTime" value="Все компоненты смещены вправо"  width="250"/>
        <cmpTextField  name="MyTime" value="Все компоненты смещены вправо"  width="250"/>
        <cmpTextField  name="MyTime" value="Все компоненты смещены вправо"  width="250"/>
        <div>33333333333333</div>
    </cmpPanel>
    <cmpOSM pos="center" name="MyOsm"  split="true"  popupmenu="myPopUp" />

    <cmpPopup name="myPopUp" onpopup="Form.onPOP(arguments);" >
        <item caption="test1" onclick="setVisible('delItem', false);"/>
        <item caption="test2" onclick="setVisible('delItem', true);"/>
        <item name="delItem" caption="test3" onclick="alert('test3')"/>
        <item caption="test4" onclick="alert('test4')"/>
        <item caption="test5" onclick="alert('test4')">
            <item caption="test1" onclick="setVisible('delItem', false);"/>
            <item caption="test2" onclick="setVisible('delItem', true);"/>
            <item name="delItem2" caption="test3" onclick="alert('test3')"/>
            <item caption="test4" onclick="alert('test4')">
                <item caption="test1" onclick="setVisible('delItem', false);"/>
                <item caption="test2" onclick="setVisible('delItem', true);"/>
                <item name="delItem3" caption="test3" onclick="alert('test3')"/>
                <item caption="test4" onclick="alert('test4')"/>
            </item>
        </item>
    </cmpPopup>

</div>
