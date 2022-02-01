<div caption="Примеры использования контролов" >
     <!-- https://docs.sencha.com/extjs/7.0.0/classic/Ext.tree.Panel.html  -->
    <cmpDataSet name="MyTree">
      <![CDATA[
          # data = { 'expanded': True,'children': [{'text': 'SFO  &nbsp;✈&nbsp; DFW','duration': '6h 55m','expanded': True,'children': [{'text': 'SFO &nbsp;✈&nbsp; PHX','duration': '2h 04m','leaf': True}, {'text': 'PHX layover','duration': '2h 36m','isLayover': True,'leaf': True}, {'text': 'PHX &nbsp;✈&nbsp; DFW','duration': '2h 15m','leaf': True }] }]
          # data = [{ 'text': 'SFO','expanded': True,'children': [{'text': 'SFO'}] }]
          data = { 'text': 'SFO','expanded': True}
        }
      ]]>
    </cmpDataSet>
    <cmpTreepanel width="500"    rootVisible="false" >
        <![CDATA[
        {
            expanded: true,
            children: [{
                text: 'SFO  &nbsp;✈&nbsp; DFW',
                duration: '6h 55m',
                expanded: true,
                children: [{
                    text: 'SFO &nbsp;✈&nbsp; PHX',
                    duration: '2h 04m',
                    leaf: true
                }, {
                    text: 'PHX layover',
                    duration: '2h 36m',
                    isLayover: true,
                    leaf: true
                }, {
                    text: '!!!PHX &nbsp;✈&nbsp; DFW',
                    expanded: false,
                    children: [{
                        text: 'SFO &nbsp;✈&nbsp; PHX',
                        duration: '2h 04m',
                        leaf: true
                    }, {
                        text: 'PHX layover',
                        duration: '2h 36m',
                        isLayover: true,
                        leaf: true
                    }, {
                        text: 'PHX &nbsp;✈&nbsp; DFW',
                        duration: '2h 15m',
                        leaf: true
                    }]
                }]
            }]
        }
        ]]>
        <colum field='text'      caption='поле 1'  width="100"/>
        <colum field='duration'      caption='поле 2'  width="200"/>
        <colum field='isLayover' caption='поле 2'  width="200"/>
    </cmpTreepanel>
</div>
