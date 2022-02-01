<div extend="Ext.panel.Panel" >
    <cmpScript>
       <![CDATA[
        // https://docs.sencha.com/extjs/6.2.0/classic/Ext.button.Cycle.html
        var buttonCycleObj = {};
        buttonCycleObj['showText']= true;
        if (typeof(arguments[0]['text']) !== 'undefined') {
           buttonCycleObj['prependText'] = arguments[0]['text'];
        }
        buttonCycleObj['menu']={};
        buttonCycleObj['menu']['id']='view-type-menu';
        buttonCycleObj['menu']['items']=[];
        if (typeof(arguments[0]['items']) !== 'undefined') {
            buttonCycleObj['menu']['items'] = arguments[0].items;
        }
        if (typeof(arguments[0]['listeners']) === 'object') {
            for (var key in arguments[0]['listeners']) {
                if (key === 'change') {
                   Form.isChangeFin =  this.config['listeners']['change'] ;
                   buttonCycleObj['changeHandler']=function(cycleBtn, activeItem) {Form.isChangeFin(cycleBtn, activeItem); }
                   continue;
                }
                buttonCycleObj['listeners'][key] = arguments[0]['listeners'][key];
            }
        }
        this.add( Ext.create('Ext.button.Cycle', buttonCycleObj) );
       ]]>
    </cmpScript>
</div>
