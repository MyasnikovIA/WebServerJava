<div caption="Примеры использования контролов" >
    <cmpScript>
       <![CDATA[
           Form.onclickItem = function(arguments) {
              var arr = [].slice.call(arguments);
              console.log(arr);
           }
       ]]>
    </cmpScript>

    <cmpTreepanel width="500" rootVisible="false" onitemclick=" console.log(arguments);  " onitemcontextmenu="Form.onclickItem(arguments)" >
         <colum field='text' caption='поле 1' />
         <colum field='duration'  caption='поле 2' >
            <colum field='text' caption='поле 1--------------'/>
            <colum field='text' caption='поле 1' />
            <colum field='text' caption='поле 1' />
         </colum>
         <colum field='isLayover' caption='поле 2' />
    </cmpTreepanel>

</div>
