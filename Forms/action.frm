<div caption="Примеры использования контролов"  test="{test:1,a3:'w'}"   test2="[1,2,3]">
    <cmpScript>
       <![CDATA[
           Form.onClickBtnGet = function(arguments) {
              var arr = [].slice.call(arguments);
              console.log("GET imput argument fun", arr);
              executeAction("myAction", function() {
                  console.log("OK",getVar('arr'));
              })
           }
           Form.onClickBtnPost = function(arguments) {
              var arr = [].slice.call(arguments);
              console.log("POST imput argument fun", arr);
              executeAction("myAction", function(obj) {
                  console.log("obj",obj);
                  console.log("OK",getVar('arr'));
              },true); // <== признак отправки сообщения POST запросом
           }
       ]]>
    </cmpScript>
    <cmpPanel>
        <cmpButton text="executeAction GET Query" onclick="Form.onClickBtnGet(arguments);"/>
        <cmpButton text="executeAction POST Query" onclick="Form.onClickBtnPost(arguments);"/>
        <cmpTextField  name="MyTime" value="Все компоненты смещены вправо"  width="250"/>
    </cmpPanel>

    <cmpAction name="myAction" >
        <![CDATA[
           from datetime import datetime
           now = datetime.now()
           MyTime = now.strftime("%m/%d/%Y, %H:%M:%S")
           arr=["111","2222","3333"]
           obj={"aaa":"2222"}
           myInt=111
        ]]>
        <items name="MyTime" src="MyTime" srctype="ctrl"  default="0"/>
    </cmpAction>
</div>
