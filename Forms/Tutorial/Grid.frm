<div caption="Примеры использования контролов" >
    <cmpScript>
       <![CDATA[
           // https://metanit.com/web/extjs/7.9.php
           Form.mylert = function(){
              alert(3333);
           }
       ]]>
    </cmpScript>
    <cmpPanel text="Expander component"  title='Центральная панель' region="north" margin='5 5 5 5'>
         <!--
                  https://ilyaut.ru/extjs/how-modx-extras-work-4/
                {
                    xtype: 'grid',
                    columns: [ // Добавляем ширину и заголовок столбца
                        {dataIndex: 'id', width: 330, header: 'ID'},
                        {dataIndex: 'name', width: 670, header: 'Name'}
                    ],
                    autoHeight: true, // Высота таблицы вычисляется автоматически
                    viewConfig: {
                        forceFit: true, // Растягиваем таблицу на всю ширину
                        scrollOffset: 0 // Убираем вертикальный скролл (у нас же автовысота)
                    },
                    store: new Ext.data.ArrayStore({
                        fields: ['id','name'],
                        data: [
                            [1, 'Pencil'],
                            [2, 'Umbrella'],
                            [3, 'Ball'],
                        ]
                    })
                }
          -->
        <cmpDataSet name="DS_COMBO" activateoncreate="false">
           <![CDATA[
               data = []
               data.append({value:10,text:f"Входящяя переменная {DATE_FROM}" })
               data.append({value:20,text:f" Значение из контрола {MyEdit}" })
               data.append({value:30,text:"------" })
           ]]>
           <items name="LPU" src="LPU" srctype="session"/>
           <items name="DATE_FROM" src="DATE_FROM" srctype="var" default="111"/>
           <items name="MyEdit" src="MyEdit" srctype="ctrl"/>
        </cmpDataSet>


         <cmpGrid title="Пользователи" height="150" width="500" viewConfig="{ 'forceFit': true, 'scrollOffset': 0 }" autoHeight="true" onitemclick=" console.log(arguments);  " >
              <![CDATA[
                   [ {id:1,name:"11111111"}
                     ,{id:2,name:"2222222222222"}
                     ,{id:3,name:'3333333'}
                   ]
              ]]>
             <colum dataIndex='id' width="330" header='ID' />
             <colum dataIndex='name' width="670" header='Name' />
         </cmpGrid>

         <cmpGrid title="Пользователи" height="150" width="500" viewConfig="{ 'forceFit': true, 'scrollOffset': 0 }" autoHeight="true"  url="/json/tutorialdata.json">
             <colum field='value'  caption='поле 1'  width="330"/>
             <colum field='text'   caption='поле 2'  width="670"/>
         </cmpGrid>

         <cmpGrid title="Пользователи" height="150" width="500" fields="value,text" autoHeight="true"  url="/json/tutorialdata.json">
             <colum  caption='поле 3'  width="330"/>
             <colum  caption='поле 4'  width="670"/>
         </cmpGrid>

         <cmpGrid caption="Пользователи" height="150" width="500" fields="value,text" columns="поле 3,поле 4" autoHeight="true"  url="/json/tutorialdata.json"/>

    </cmpPanel>
</div>
