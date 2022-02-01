<div caption="Примеры использования контролов"  onshow="console.log('layout.frm',this);">

    <!-- https://docs.sencha.com/extjs/6.2.0/classic/Ext.container.Viewport.html -->

    <cmpScript>
        <![CDATA[
           Form.testAlert = function() {
               alert(2);
           }
           Form.popUptest = function(arg) {
               console.log("arg",arg);
           }
        ]]>
    </cmpScript>
    <cmpPanel pos="up" collapsible="true" width="150">
        ssssssssss
        <cmpTextField  name="MyTime" value="Все компоненты смещены вправо"  width="250"  popupmenu="myPopUp"/>
    </cmpPanel>
    <cmpPanel pos="center"  popupmenu="myPopUp">11111111111</cmpPanel>

    <cmpTreePanel popupmenu="myPopUp"  pos="left"  collapsible="true" width="350">

    </cmpTreePanel>


    <cmpPanel pos="right" collapsible="true" width="350">6666</cmpPanel>
    <cmpPanel pos="down"  collapsible="true" width="350" >
        <cmpButton caption="ddddddd"  onclick="Form.testAlert(1)"/>
        22
    </cmpPanel>

    <cmpPopup name="myPopUp" onpopup="Form.popUptest(arguments);" >
        <item caption="открыть" onclick="openForm('Tutorial/layout', true);"/>
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
