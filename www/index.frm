<!DOCTYPE html>
<html>
   <head>
        <meta charset="utf-8"/>
        <link href="/lib/ExtJS_6.2.0/classic/theme-classic/resources/theme-classic-all.css" rel="stylesheet" />
        <script type="text/javascript" src="/lib/ExtJS_6.2.0/ext-all.js"></script>
        <script type="text/javascript" src="/lib/common.js"></script>
        <script type = "text/javascript">
             document.addEventListener('contextmenu', function(event){event.preventDefault();});
              Ext.onReady(function() {
 if (typeof(window.Win_indexfrm) === 'undefined') window.Win_indexfrm = {};
 if (typeof(window.Win_indexfrm_ListCotrl) === 'undefined') window.Win_indexfrm_ListCotrl = {};
 var indexfrm = {
    "layout": "border",
    "renderTo": Ext.getBody(),
    "mainForm": "wine2679449_3147_437c_b50b_744a55608325",
    "mainInitObject": "indexfrm",
    "id": "wine2679449_3147_437c_b50b_744a55608325",
    "vars": {},
    "title": "Примеры использования контролов",
    "mainList": {},
    "items": [{
        "xtype": "panel",
        "mainForm": "wine2679449_3147_437c_b50b_744a55608325",
        "mainInitObject": "indexfrm",
        "region": "center",
        "items": [{
            "xtype": "textfield",
            "mainForm": "wine2679449_3147_437c_b50b_744a55608325",
            "mainInitObject": "indexfrm"
        }]
    }],
    "mainFile": "index.frm",
    "vars_return": {}
};
 window.Win_indexfrm = Ext.create('Ext.Viewport',indexfrm);
 window.Win_indexfrm;
 registerWinForm('index.frm', window.Win_indexfrm);
 ExtensionMainList(window.Win_indexfrm,window.Win_indexfrm_ListCotrl);
 if ( typeof(window.Win_indexfrm['vars']) !=='undefined') window.Win_indexfrm['vars'] = {};
 window.Win_indexfrm["vars"]//=[[%DataVars%]];
 
});
        </script>
   </head>
   <body></body>
</html>