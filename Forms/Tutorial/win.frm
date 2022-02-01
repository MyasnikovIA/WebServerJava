<div caption="Примеры использования контролов"  onshow="console.log('layout.frm',this);">
    <cmpScript>
        <![CDATA[
           Form.popUptest = function(arg) {
               console.log("arg",arg);
           }
        ]]>
    </cmpScript>
    <cmpPanel pos="up" collapsible="true" width="150">
        ssssssssss
        <cmpTextField  name="MyTime" value="Все компоненты смещены вправо"  width="250"  popupmenu="myPopUp"/>
        <cmpButton caption="открыть модальное окно" onclick="openForm('Tutorial/winModal.frm', true,{'vars':{myVar1:111,myVar2:222},'onclose':function(mod){ console.log('OK',mod); }} );"/>
    </cmpPanel>
    <cmpPopup name="myPopUp" onpopup="Form.popUptest(arguments);" >
        <item caption="открыть" onclick="openForm('Tutorial/winModal.frm', true,{'vars':{myVar1:111,myVar2:222},'onclose':function(mod){ console.log('OK',mod); }} );"/>
    </cmpPopup>
</div>
