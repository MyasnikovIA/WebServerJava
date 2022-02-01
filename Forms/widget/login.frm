<div extend="Ext.window.Window"
     closable="true"
     closeAction="hide"
     modal="true"
     buttons="[{'text':'Ок','handler':'Form.testFun()' ,'test':true},{'text':'Отмена','handler':'Form.onCansel()' ,'test':true}]">

    <cmpScript>
       <![CDATA[
          //this.show();
          Form.testFun = function() {
             executeAction("myAction");
             refreshDataSet('DS_COMBO',function(){ alert('ok'); });
             this.close();
          }
          Form.onCansel = function() {
             this.close();
          }
       ]]>
    </cmpScript>
    <cmpTextField  allowBlank="false" fieldLabel="Name:" name="MyTime" emptyText="Name ID" />
    <cmpTextField  allowBlank="false" fieldLabel="Password:" name="password" emptyText="password" inputType="password"/>
    <cmpcheckbox  fieldLabel="Sex" />
    <cmpAction name="myAction" >
        <![CDATA[
           from datetime import datetime
           now = datetime.now()
           MyTime = now.strftime("%m/%d/%Y, %H:%M:%S")
           MyTime = f"{MyTime}--{foundObjectText}"
        ]]>
        <items name="MyTime"  src="MyTime" srctype="ctrl"  default="0"/>
        <items name="MyTime2" src="MyTime2" srctype="ctrl"  default="------"/>
        <items name="foundObjectText" src="foundObjectText" srctype="ctrl"  default="0"/>
    </cmpAction>
    <cmpDataSet name="DS_COMBO" activateoncreate="false">
       <![CDATA[
           LPU = "66666"
           data = []
           data.append({'value':10,'text':f"Входящяя переменная {DATE_FROM}" })
           data.append({'value':20,'text':f"--{LPU}----" })
           data.append({'value':30,'text':"------" })
           data.append({'value':40,'text':"------" })
           data.append({'value':50,'text':"------" })
       ]]>
       <items name="LPU" src="LPU" srctype="session"  default="4444"/>
       <items name="DATE_FROM" src="DATE_FROM" srctype="var" default="111"/>
    </cmpDataSet>
</div>