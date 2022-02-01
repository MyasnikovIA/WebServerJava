<div caption="Отрисовка графиков" >
    <cmpScript>
        <![CDATA[
           Form.onTypeChange = function(arguments) {
                var userVal = getControl('myInputValue').value;
                var conteyner = getControl('blockReport');
                var valuesArr = userVal.split(',').map(val => Number(val))
                var config = {
                        xtype: 'container',
                        layout: 'hbox',
                        items: [{
                            xtype: 'label',
                            html: arguments[1].text + "   " +valuesArr.join(', ')
                        }, {
                            xtype: arguments[1].value,
                            values: valuesArr,
                            height: 50,
                            width: 1000
                        }]
                };
                conteyner.add(config);
           }
       ]]>
    </cmpScript>
    <cmpPanel region="center"   >

        <cmpPanel region="nort"   >
            <cmpTextfield value = '6,10,4,-3,7,2' name="myInputValue"/>
            <cmpSegmentedbutton  name="myButton" ontoggle="Form.onTypeChange(arguments);" >
                 <item value="sparklineline" text="Line"/>
                 <item value="sparklinebox" text="Box"/>
                 <item value="sparklinebullet" text="Bullet"/>
                 <item value="sparklinediscrete" text="Discrete"/>
                 <item value="sparklinepie" text="Pie"/>
                 <item value="sparklinetristate" text="TriState"/>
           </cmpSegmentedbutton>
        </cmpPanel>

        <cmpPanel region="nort"  name="blockReport"  autoScroll="true" >
           <cmpContainer>
               <cmpLabel>Pie:</cmpLabel>
               <cmpSparklinepie values="[6,10,4,-3,7,2]" height="100" width="100"/>
           </cmpContainer>
           <cmpContainer>
               <cmpLabel>Line:</cmpLabel>
               <cmpsparklineline values="[6,10,4,-3,7,2]" height="100" width="100"/>
           </cmpContainer>
           <cmpContainer>
               <cmpLabel>Box:</cmpLabel>
               <cmpsparklinebox values="[6,10,4,-3,7,2]" height="100" width="1000"/>
           </cmpContainer>
           <cmpContainer>
               <cmpLabel>Discrete:</cmpLabel>
               <cmpSparklinediscrete values="[6,10,4,-3,7,2]" height="100" width="1000"/>
           </cmpContainer>
           <cmpContainer>
               <cmpLabel>TriState:</cmpLabel>
               <cmpSparklinetristate values="[6,10,4,-3,7,2]" height="100" width="1000"/>
           </cmpContainer>
        </cmpPanel>
    </cmpPanel>


</div>
