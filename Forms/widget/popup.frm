<div extend="Ext.menu.Menu" >
    <cmpScript>
        <![CDATA[
            var mainInitObject=arguments[0]['mainInitObject'];
            var ctrl = eval("window.Win_"+mainInitObject+"_ListCotrl");
            var nameCtrl ;
            if (typeof(arguments[0]['name']) !== 'undefined') {
                 nameCtrl = arguments[0]['name'];
            } else{
                nameCtrl = "Ctrl_"+window.uuidv4();
            }
            var mainForm = null;
            if (typeof(arguments[0]['mainForm']) !== 'undefined') {
                 mainForm =arguments[0]['mainForm'];
            }
            let parseItemsObject = function(ctrl,objInitPanel) {
                var itemPopup = [];
                if (typeof(objInitPanel['items']) !== 'undefined') {
                    for (var i = 0; i < objInitPanel['items'].length; i++) {
                        if (typeof(objInitPanel['items'][i]['text']) === 'undefined') continue;
                        var obj = {};
                        obj['text'] = objInitPanel['items'][i]['text'];
                        if (typeof(objInitPanel['items'][i]['handler']) !== 'undefined') {
                           obj['handler'] = objInitPanel['items'][i]['handler'];
                        }else if ((typeof(objInitPanel['items'][i]['listeners']) !== 'undefined') && (typeof(objInitPanel['items'][i]['listeners']['click']) !== 'undefined')){
                           obj['handler'] = objInitPanel['items'][i]['listeners']['click'];
                        }
                        if (mainForm !=='null') {
                           obj['mainForm'] = mainForm;
                        }
                        if (typeof(objInitPanel['items'][i]['id']) !== 'undefined') {
                           obj['id'] = objInitPanel['items'][i]['name'];
                        } else {
                           obj['id'] =  "main_item_"+window.uuidv4();
                        }
                        if (typeof(objInitPanel['items'][i]['name']) !== 'undefined') {
                           obj['name'] = objInitPanel['items'][i]['name'];
                           ctrl[obj['name']]=obj['id'];
                        }
                        if (typeof(objInitPanel['items'][i]['items']) !== 'undefined') {
                           console.log("objInitPanel['items'][i]['items']",objInitPanel['items'][i]['items']);
                           obj['menu'] = parseItemsObject(ctrl,objInitPanel['items'][i]);
                        }
                        itemPopup.push(obj);
                    }
                }
                return itemPopup
            }
            var itemPopup = parseItemsObject(ctrl,arguments[0]);
            popUpFun = function(){};
            if ((typeof(arguments[0]['listeners']) !== 'undefined') &&(typeof(arguments[0]['listeners']['popup']) !== 'undefined')) {
               popUpFun = arguments[0]['listeners']['popup'];
            }
            ctrl[nameCtrl]=Ext.create('Ext.menu.Menu',{'name':nameCtrl,'items':itemPopup,'popUpFun':popUpFun});
            // ctrl[nameCtrl].showAt(100, 100 );
      ]]>
    </cmpScript>
</div>
