<div extend="Ext.panel.Panel" >
    <cmpScript>
       <![CDATA[
        var pickerObj = {
             // listeners: {select: function(picker, selColor) {alert(selColor);}}
        }
        if (typeof(arguments[0].value) !== 'undefined') {
            pickerObj.value = arguments[0].value;
        } else {
            pickerObj.value = 0;
        }
        pickerObj['listeners'] = {}
        if (typeof(arguments[0]['listeners']) === 'object') {
            for (var key in arguments[0]['listeners']) {
                if (key === 'select') {
                   Form.selColorFin =  this.config['listeners']['select'] ;
                   pickerObj['listeners']['select'] = function(picker, selColor) { this.value = selColor;  console.log("this",this); Form.selColorFin(picker, selColor) }
                   continue;
                }
                pickerObj['listeners'][key] = arguments[0]['listeners'][key];
            }
        }
        this.add(Ext.create('Ext.picker.Color', pickerObj))
        console.log("this",this.value);
       ]]>
    </cmpScript>
</div>
