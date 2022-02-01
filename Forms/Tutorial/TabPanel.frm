<div caption="Примеры использования контролов" >

        <cmpScript>
        <![CDATA[
           Form.onTypeChange = function(arguments) {
              console.log("arguments",arguments);
           }
       ]]>
    </cmpScript>
   <cmpTabPanel region="center" activeTab="0" margin="3 0 15 0" width="100%"  onTabchange="Form.onTypeChange(arguments);">
        <item title="tab1" bodyPadding="10" html='A simple tab'>
            fasdfsadfsa
            asdfasdf
            safdasdf
        </item>
        <item title="tab2" bodyPadding="10" html='A simple tab'/>
        <item title="tab2" bodyPadding="10" html='A simple tab'  layout="card">
              <iframe src="index.html"   width="100%" height="100%" ></iframe>
        </item>
   </cmpTabPanel>
</div>
