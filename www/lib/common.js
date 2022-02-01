'use strict'

 /**
       loadScript("/engine/classes/js/jquery.pickmeup.min.js").then(function(){
           console.log("js ready");
       },function(error){
           console.log(error);
       })

       loadCSS("/engine/classes/css/pickmeup.min.css").then(function(){
           console.log("css ready");
       },function(error){
           console.log(error);
       })
 */

    window.loadScript = function(src,_timeout) {
           return new Promise(function(resolve, reject){
               if(!src){
                   reject(new TypeError("filename is missing"));
                   return;
               }
               var script=document.createElement("script"), timer, head=document.getElementsByTagName("head")[0];
               head.appendChild(script);
               function leanup(){
                   clearTimeout(timer);
                   timer=null;
                   script.onerror=script.onreadystatechange=script.onload=null;
               }
               function onload(){
                   leanup();
                   if(!script.onreadystatechange||(script.readyState&&script.readyState=="complete")){
                       resolve(script);
                   }
               }
               script.onerror=function(error){
                   leanup();
                   head.removeChild(script);
                   script=null;
                   reject(new Error("network"));
               };

               if (script.onreadystatechange === undefined) {
                   script.onload = onload;
               } else {
                   script.onreadystatechange = onload;
               }
               timer=setTimeout(script.onerror,_timeout||30000);
               script.setAttribute("type", "text/javascript");
               script.setAttribute("src", src);
           });
       }

       function loadCSS (src, _timeout) {
           var css = document.createElement('link'), c = 1000;
           document.getElementsByTagName('head')[0].appendChild(css);
           css.setAttribute("rel", "stylesheet");
           css.setAttribute("type", "text/css");
           return new Promise(function(resolve, reject){
               var c=(_timeout||10)*100;
               if(src) {
                   css.onerror = function (error) {
                       if(timer)clearInterval(timer);
                       timer = null;

                       reject(new Error("network"));
                   };

                   var sheet, cssRules, timer;
                   if ('sheet' in css) {
                       sheet = 'sheet';
                       cssRules = 'cssRules';
                   }
                   else {
                       sheet = 'styleSheet';
                       cssRules = 'rules';
                   }
                   timer = setInterval(function(){
                       try {
                           if (css[sheet] && css[sheet][cssRules].length) {
                               clearInterval(timer);
                               timer = null;
                               resolve(css);
                               return;
                           }
                       }catch(e){}

                       if(c--<0){
                           clearInterval(timer);
                           timer = null;
                           reject(new Error("timeout"));
                       }
                   }, 10);

                   css.setAttribute("href", src);
               }else{
                   reject(new TypeError("filename is missing"));
               }
           });
       }


function loadScriptSyn(file,callback){
    var head=document.getElementsByTagName("head")[0];
    var script=document.createElement('script');
    script.src=file;
    script.type='text/javascript';
    script.onload=callback;
    script.onreadystatechange = function() {
        if (this.readyState == 'complete') {
            callback();
        }
    }
    head.appendChild(script);
}

function loadScriptSyn_OLD(src, onloadFunction) {
   function loadError(oError) {
     throw new URIError("The script " + oError.target.src + " didn't load correctly.");
   }
   var newScript = document.createElement("script");
   newScript.setAttribute("type", "text/javascript");
   newScript.onerror = loadError;
   if (onloadFunction) {
       newScript.onload = function() {
           onloadFunction();
       };
   }
   document.head.appendChild(newScript);
   newScript.setAttribute("src", src);
   newScript.src = src;
}

 window.uuidv4 = function () {
  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
  );
}


// Преобразование строки в функцию
if (typeof String.prototype.parseFunction != 'function') {
    String.prototype.parseFunction = function () {
        var funcReg = /function *\(([^()]*)\)[ \n\t]*{(.*)}/gmi;
        var match = funcReg.exec(this.replace(/\n/g, ' '));
        if(match) {
            return new Function(match[1].split(','), match[2]);
        }
        return null;
    };
}

// Преобразование  функций объекта в строку (рекурсия)
function objectToStr(obj) {
    getProp(obj);
    function getProp(o) {
        for(var prop in o) {
            if(typeof(o[prop]) === 'object') {
                getProp(o[prop]);
            } else {
               if (typeof(o[prop]) === 'function') {
                  o[prop] = o[prop].toString()
               }
            }
        }
    }
}

// Рекурсивно проходим по всему объекту и  конвертируем строку в JS функцию
function objectPreprocessing(obj) {
    getProp(obj);
    function getProp(o) {
        for(var prop in o) {
            if(typeof(o[prop]) === 'object') {
                getProp(o[prop]);
            } else {
               if ((typeof(o[prop]) === 'string') && (o[prop].substr(0, 9) === 'function(' )) {
                  o[prop] =o[prop].parseFunction();
               }
            }
        }
    }
}


function getJsonUrl(url) {
    var xhr = new CreateRequest();
    xhr.open('GET', url, false);
    xhr.send();
    if (xhr.status != 200) {
      console.log( xhr.status + ': ' + xhr.statusText );
      return {'error':xhr.status + ' : ' + xhr.statusText} ;
    } else {
      return JSON.parse(xhr.response);
    }
}

function CreateRequest() {
    var Request = false;
    if (window.XMLHttpRequest) {
        //Gecko-совместимые браузеры, Safari, Konqueror
        Request = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        //Internet explorer
        try {
             Request = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (CatchException) {
             Request = new ActiveXObject("Msxml2.XMLHTTP");
        }
    }
    if (!Request) {
        alert("Невозможно создать XMLHttpRequest");
    }
    return Request;
}
function sendSrv(url, data, colbackFun) {
    if (typeof(data) === 'undefined') data = null;
    if (typeof(colbackFun) === 'undefined') colbackFun = null;
    var request = new XMLHttpRequest(); // CreateRequest();
    //request.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
    if (typeof(colbackFun) === 'function'){
       request.open('POST', url, true);  // `false` makes the request synchronous
       request.setRequestHeader('Content-type', 'application/json');
       // request.setRequestHeader('Content-type', 'application/json');
       request.onreadystatechange = function() {
          if (this.statusText === "OK") {
             colbackFun(this.responseText);
             return;
          }
       };
       request.send(JSON.stringify(data));
       return request;
    } else {
        request.open('POST', url, false);  // `false` makes the request synchronous
        request.setRequestHeader('Content-type', 'application/json; charset=utf-8');
        request.send(JSON.stringify(data));
        if (request.status === 200) {
          return request.responseText;
        }
    }
}

function ctrlDataSetExecute(_dom,datsetName) {
   console.log("ctrlDataSetExecute,_dom",_dom)
   var winObj = Ext.getCmp(_dom['mainForm'])
   return new Ext.data.Store({
        fields: [], // Поля, доступные в массиве данных
        data: []
    })
}

function getExtDataStore(_dom,data) {

    Ext.Ajax.request({
        method: "POST",
        url: url,
        jsonData:  obj,
        success: function (response) {
            alert('success: ' + response.responseText);
        },
        failure: function (e, jqxhr) {
            alert('failure!');
            alert(e.status);
        }
    });

   console.log("ctrlDataSetExecute,_dom",_dom)
   var winObj = Ext.getCmp(_dom['mainForm'])
   return new Ext.data.Store({
        fields: [], // Поля, доступные в массиве данных
        data: []
    })
}


window.ExtObj={};
function getVar(_dom,propName,value) {
   if (typeof(value) === 'undefined') {
      value = null;
   }
   var winObj = _dom;
   if (typeof(winObj) === 'undefined') {
       return value;
   }
   if (typeof(winObj['vars']) === 'undefined') {
       winObj['vars'] = {};
       return value;
   }
   if (typeof(winObj['vars'][propName]) === 'undefined') {
       return value;
   }
   return winObj['vars'][propName];
}

function setVar(_dom,propName,value) {
   var winObj = _dom
   if (typeof(winObj['vars']) === 'undefined') {
       winObj['vars'] = {};
   }
   if (typeof(value) === 'undefined') {
      value = null;
   }
   winObj['vars'][propName] = value
}

function setValue(_dom,propName,value){
    if (typeof(value) === 'undefined') {
      value = null;
    }
    var ctrlObj = _dom.query('[name='+propName+']');
    if (ctrlObj) {
       ctrlObj[0].setValue(value);
       return
    }
    console.log("error","контрол с именем "+propName+" нет на форме");
}


function setCaption(_dom,propName,value){
    if (typeof(value) === 'undefined') {
      value = null;
    }
    // var winObj = Ext.getCmp(_dom['mainForm'])
    var ctrlObj = _dom.query('[name='+propName+']');
    if (ctrlObj.length > 0) {
       ctrlObj[0].setText(value);
       return
    }
    console.log("error","контрол с именем "+propName+" нет на форме");
}

function getValue(_dom,propName){
   var ctrlObj = _dom.query('[name='+propName+']');
   if (ctrlObj.length > 0) {
      return ctrlObj[0].getValue();
   }
   return undefined
   // console.log("error","контрол с именем "+propName+" нет на форме");
   // return value
}

function setVisible(_dom, ctrl, isVisible) {
   if (typeof(ctrl) === "string") {
      ctrl = getControl(_dom, ctrl);
   }
   if (ctrl === null ) {
     console.log("error","контрола нет на форме");
     return
   }
   if (isVisible === true) {
     ctrl.show();
   } else {
     ctrl.hide();
   }
   //pop.items.items[0].hide();
   // pop.items.items[0].show();
}

function parse_query_string(query) {
  var vars = query.split("&");
  var query_string = {};
  for (var i = 0; i < vars.length; i++) {
    var pair = vars[i].split("=");
    var key = decodeURIComponent(pair[0]);
    var value = decodeURIComponent(pair[1]);
    // If first entry with this name
    if (typeof query_string[key] === "undefined") {
      query_string[key] = decodeURIComponent(value);
      // If second entry with this name
    } else if (typeof query_string[key] === "string") {
      var arr = [query_string[key], decodeURIComponent(value)];
      query_string[key] = arr;
      // If third or later entry with this name
    } else {
      query_string[key].push(decodeURIComponent(value));
    }
  }
  return query_string;
}


function setDisable(_dom, ctrl, isDisabled) {
   if (typeof(ctrl) === "string") {
      ctrl = getControl(_dom, ctrl);
   }
   if (ctrl === null ) {
     console.log("error","контрола нет на форме");
     return
   }
   ctrl.setDisabled(isDisabled);
}


function foundItemsName(_dom, propName) {
   if (typeof(_dom["name"]) !== 'undefined') {
      if (_dom["name"] === propName){
         return _dom;
      }
   }
   if (typeof(_dom["items"]) !== 'undefined') {
        if (Array.isArray(_dom["items"]) == true) {
           for (var i = 0; i < _dom["items"].length; i++) {
             console.log('_dom["items"][i]',i , _dom["items"][i])
             var subDom =  foundItemsName( _dom["items"][i], propName)
             if (subDom !== null){
                return subDom
             }
           }
        } else if (typeof(_dom["items"]) === 'object') {
            var subDom = foundItemsName( _dom["items"], propName)
             if (subDom !== null){
                return subDom
             }
        }
   }
   return null;
}

function getControl(_dom, propName) {
   var mainObject = _dom;
   for (var key in _dom['mainList']) {
      if (key === propName ) {
          if (typeof(_dom['mainList'][key]) === 'object'){
            return _dom['mainList'][key];
          }else{
             var ctrlObj = Ext.getCmp(_dom['mainList'][key]);
             if (typeof(ctrlObj) === 'object'){
                return ctrlObj;
             }
             ctrlObj = _dom.query('[name='+propName+']');
             if (ctrlObj.length > 0) {
                return ctrlObj[0];
             }
          }
      }
   }
   var ctrlObj = _dom.query('[name='+propName+']');
   if (ctrlObj.length > 0) {
      return ctrlObj[0];
   }
   for (var key in _dom['dataSetList']) {
      if (key === propName ) {
          return _dom['dataSetList'][key];
      }
   }
   var retObj = foundItemsName(_dom, propName);
   if (retObj !== null) {
      return null;
   }
   console.log("error","контрол с именем "+propName+" нет на форме");
   return null;
}

function close(_dom,mod) {
   var ctrlObj = _dom
   console.log("_dom",ctrlObj['mainFile'])
   ctrlObj.destroy()
   // mainParentFile
   console.log('****************************************mainParentFile' )
   console.log('_dom',_dom)
   console.log('window.ExtObj["FormsObject"][_dom["mainFile"]',window.ExtObj["FormsObject"])
   var panetnFile =window.ExtObj["FormsObject"][ctrlObj['mainFile']]['objectChildren']['mainParentFile'];
   var parentObject = window.ExtObj["FormsObject"][panetnFile]['objectChildren']

   if (typeof(parentObject['objectOnEvent']) !=='undefined')

   console.log('panetnFile = ',parentObject);

   // window.ExtObj["FormsObject"][formName]['objectOnEvent']
   /*
   var parentFormName = window.parentWin["mainFile"]
   console.log("window.parentWin",window.parentWin);
   console.log("window.childrenWin",window.childrenWin);
   console.log(" parentFormName ",parentFormName);
   var obj=window.ExtObj["FormsObject"][parentFormName];
   console.log("obj",obj);

      //   window.ExtObj["FormsObject"][formName]['mainParentFile']    = _domParent['mainFile'];
      //   window.ExtObj["FormsObject"][formName]['mainParentWin']     = _domParent;
      //   window.ExtObj["FormsObject"][formName]['mainParentOnEvent'] = objectOnEvent;

   // console.log("22")
   // console.log("_dom",_dom)
   if (typeof(_dom['mainParentOnEvent']) !== 'undefined') {
      var parentOnEvent = _dom['mainParentOnEvent'];
      if (typeof(parentOnEvent['onclose']) === 'function') {
          parentOnEvent['onclose'](mod);
      }
   }
   if (typeof(ctrlObj.close) === 'function') {
      ctrlObj.close();
   } else {
      ctrlObj.destroy()
   }
   */
}

function getDataSet(){
     // получить объект Dataset
     if (typeof(window.ExtObj)==='undefined') window.ExtObj = {};
     if (typeof(window.ExtObj["FormsObject"])==='undefined') window.ExtObj["FormsObject"] = {};
     if (arguments.length === 0) return null;
     var datasetName = "";
     var arr = [].slice.call(arguments);
     var _domParent = null;
     if (typeof(arr[0]) === 'object') {
        _domParent = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'string') {
        datasetName = arr.splice(0, 1)[0];
     }
     if(datasetName.length === 0) return null;
     if (_domParent == null)  return null;
     // var ctrlObj = Ext.getCmp(_domParent['mainForm'])
     var ctrlObj = _domParent;
     if (typeof(ctrlObj)==='undefined') return null;
     if (typeof(ctrlObj['dataSetList'])==='undefined') return null;
     if (typeof(ctrlObj['dataSetList'][datasetName])==='undefined') return null;
     return ctrlObj['dataSetList'][datasetName]
}
function startProgress(){
    Ext.MessageBox.show({
                    msg : 'Load...',
                    progressText : 'Load...',
                    width : 300,
                    wait : true,
                  });
}
function stopProgress(){
    Ext.MessageBox.hide();
}


function ExtensionMainList(mainObject, listCtrl) {
    if (typeof(mainObject['mainList']) === 'undefined'){
        mainObject['mainList'] = {};
    }
    for (var key in listCtrl) {
       mainObject['mainList'][key] = listCtrl[key];
    }
}


function refreshDataSet(){
     // Функция получения массива данных
     if (typeof(window.ExtObj)==='undefined') window.ExtObj = {};
     if (typeof(window.ExtObj["FormsObject"])==='undefined') window.ExtObj["FormsObject"] = {};
     if (arguments.length === 0) return;
     var datasetName = "";
     var objectQuery = {};
     var renderTo = Ext.getBody();
     var arr = [].slice.call(arguments); // Перегружаем все аргументы в массив
     var colbackFun = null;
     var formName = "";
     var _domParent = null;
     var isPostQuery = false;
     if (typeof(arr[0]) === 'object') {
        _domParent = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'string') {
        datasetName = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'object') {
        objectQuery = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'function') {
        colbackFun = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'boolean') {
        isPostQuery = arr.splice(0, 1)[0];
     }
     if(datasetName.length === 0) {
        console.log("error", "Не указано имя dataset")
        return;
     }
     if (_domParent == null)  {
        console.log("error", "Не определен контекст вызова")
        return;
     }
     var parentFrom = null;
     formName = _domParent["formName"];
     if (typeof(_domParent['dataSetList']) === 'undefined')  {
        console.log("error","Не определен списко dataset на форме")
        return;
     } ;
     if (typeof(_domParent['dataSetList'][datasetName]) === 'undefined') {
        console.log("error", "dataset с именем "+datasetName+" отсутствует на форме")
        return;
     }
     var mainObject = Ext.getCmp(_domParent['mainForm']);
     if ((typeof(_domParent['dataSetVarList'])!=="undefined") && (typeof(_domParent['dataSetVarList'][datasetName])!=="undefined") ) {
        for (var key in _domParent['dataSetVarList'][datasetName]) {
            var srcName = _domParent['dataSetVarList'][datasetName][key]['src']
            var defaultValue = _domParent['dataSetVarList'][datasetName][key]['default']
            if (_domParent['dataSetVarList'][datasetName][key]['srctype'] == "ctrl") {
                objectQuery[key] = getValue(_domParent,srcName)
                if (typeof(objectQuery[key]) === 'undefined') {
                    if (formName !== _domParent['mainForm']){
                        objectQuery[key] = getValue(mainObject,srcName);
                    }else{
                       objectQuery[key] = defaultValue
                    }
                }
            }
            if (_domParent['dataSetVarList'][datasetName][key]['srctype'] == "var") {
                objectQuery[key] = getVar(_domParent,srcName)
                if (objectQuery[key] == null){
                    objectQuery[key] = getVar(mainObject,srcName,defaultValue)
                }
            }
        }
     }
     var storeObj = _domParent['dataSetList'][datasetName];
     var url = "dataset.php";
     if ((typeof(_domParent['ServerPathQuery']) !== 'undefined') && (_domParent['ServerPathQuery'].length>1)) {
         url = _domParent['ServerPathQuery'][0]+"://"+_domParent['ServerPathQuery'][1]+"/"+url;
     }
     if (!isPostQuery) {
         startProgress();
         loadScript(url+"?Form="+formName+"&dataset="+datasetName+"&data="+JSON.stringify(objectQuery)).then(function(script) {
            if (typeof(colbackFun) ==='function') {
                     var records = storeObj.getData().items;
                     stopProgress();
                     colbackFun(records);
            }
         },function(error) {
            console.log(error);
         });
     } else {
         /*
         // получение данных через Proxy (GET) запрос
         storeObj.getProxy()['url'] = "dataset.php?Form="+formName+"&dataset="+datasetName;
         for(var key in objectQuery){
            storeObj.getProxy().setExtraParam(key,objectQuery[key]);
         }
         storeObj.load({
            callback: function(records, operation, success) {
                if (typeof(colbackFun) === 'function'){
                   colbackFun(records);
                }
                storeObj["records"] = records;
            }
        });
        */
         // var ctrlObj = Ext.getCmp(_domParent['mainForm'])
         var ctrlObj = _domParent;
         var request = new XMLHttpRequest(); // CreateRequest();
         //request.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
         request.open('POST', url, true);  // `false` makes the request synchronous
         request.setRequestHeader('Content-type', 'application/json');
         var countQuery = 0; // необходимо уточнить почему производится 3 запроса, вмнсто одного
         startProgress();
         request.onreadystatechange = function() {
            stopProgress();
           if (request.status === 200) {
                countQuery++;
                if (countQuery == 2) {
                    var bin = JSON.parse(request.responseText);
                    storeObj.loadData(bin);
                    if (typeof(colbackFun) === "function") {
                        colbackFun(bin);
                    }
                }
           }
         };
         request.send(JSON.stringify({'Form':formName,'dataset':datasetName, 'data':objectQuery}));
         return request;
     }
}

function setData() {
     // Функция загрузки store данных в JS коде
     if (typeof(window.ExtObj)==='undefined') window.ExtObj = {};
     if (typeof(window.ExtObj["FormsObject"])==='undefined') window.ExtObj["FormsObject"] = {};
     if (arguments.length === 0) return;
     var datasetName = "";
     var objectQuery = [];
     var renderTo = Ext.getBody();
     var arr = [].slice.call(arguments); // Перегружаем все аргументы в массив
     var colbackFun = null;
     var formName = "";
     var _domParent = null;
     if (typeof(arr[0]) === 'object') {
        _domParent = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'string') {
        datasetName = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'object') {
        objectQuery = arr.splice(0, 1)[0];
     }
     if(datasetName.length === 0) {
        console.log("error","Не указано имя dataset")
        return;
     }
     if (_domParent == null) {
        console.log("error","Не определен контекст вызова")
        return;
     }
     var parentFrom = null;
     formName = _domParent["formName"];
     // var ctrlObj = Ext.getCmp(_domParent['mainFormName'])
     var ctrlObj = _domParent;
     if (typeof(ctrlObj['dataSetList']) === 'undefined') {
        console.log("error","Не определен списко dataset на форме")
        return;
     } ;
     if (typeof(ctrlObj['dataSetList'][datasetName]) === 'undefined') {
        console.log("error","dataset с именем "+datasetName+" отсутствует на форме")
        return;
     }
     var storeObj = ctrlObj['dataSetList'][datasetName];
     storeObj.loadData(objectQuery);
}

function showPopupMenu(){
     if (arguments.length === 0) return null;
     var x=0;
     var y=0;
     var arr = [].slice.call(arguments);
     var _domParent = null;
     var menuName="";
     if (typeof(arr[0]) === 'object') {
        _domParent = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'string') {
        menuName = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'number') {
        x = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'number') {
        y = arr.splice(0, 1)[0];
     }
     // var ctrlObj = Ext.getCmp(_domParent['mainForm'])
     var ctrlObj = _domParent;
     if (typeof(ctrlObj['mainList']) === 'undefined')  return;
     if (typeof(ctrlObj['mainList'][menuName]) === 'undefined') {
        console.log("error","меню с именем "+menuName+" отсутствует на форме")
        return;
     }
     var storeObj = ctrlObj['mainList'][menuName];
     storeObj.showAt(x,y);
     // Ext.getCmp('testCtrl').showAt(x,y);
}

function executeAction(){
     if (typeof(window.ExtObj)==='undefined') window.ExtObj = {};
     if (typeof(window.ExtObj["FormsObject"])==='undefined') window.ExtObj["FormsObject"] = {};
     if (arguments.length === 0) return;
     var datasetName = "";
     var objectQuery = {};
     var renderTo = Ext.getBody();
     var arr = [].slice.call(arguments); // Перегружаем все аргументы в массив
     var colbackFun = null;
     var formName = "";
     var _domParent = null;
     var isPostQuery = false;
     if (typeof(arr[0]) === 'object') {
        _domParent = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'string') {
        datasetName = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'object') {
        objectQuery = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'function') {
        colbackFun = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'boolean') {
        isPostQuery = arr.splice(0, 1)[0];
     }
     if(datasetName.length === 0) return;
     if (_domParent == null)  return;
     var parentFrom = null;
     formName = _domParent["formName"];
     var mainObject = Ext.getCmp(_domParent['mainForm']);
     if ((typeof(_domParent['actionVarList'])!=="undefined") && (typeof(_domParent['actionVarList'][datasetName])!=="undefined") ) {
        for (var key in _domParent['actionVarList'][datasetName]) {
            var srcName = _domParent['actionVarList'][datasetName][key]['src']
            var defaultValue = _domParent['actionVarList'][datasetName][key]['default']
            if (_domParent['actionVarList'][datasetName][key]['srctype'] == "ctrl") {
                var varVol = getValue(_domParent,srcName);
                if (typeof(varVol) === 'undefined') {
                    if (formName !== _domParent['mainForm']) {
                       varVol = getValue(mainObject,srcName);
                    }else{
                       varVol = defaultValue;
                    }
                }
                objectQuery[key] = varVol;
            }
            if (_domParent['actionVarList'][datasetName][key]['srctype'] == "var") {
                objectQuery[key] = getVar(_domParent,srcName)
                if (objectQuery[key] == null){
                    objectQuery[key] = getVar(mainObject,srcName,defaultValue)
                }
            }
        }
     }
     var url = "action.php";
     if ((typeof(_domParent['ServerPathQuery']) !== 'undefined') && (_domParent['ServerPathQuery'].length>1)) {
         url = _domParent['ServerPathQuery'][0]+"://"+_domParent['ServerPathQuery'][1]+"/"+url;
     }
     if (!isPostQuery) {
         var colbackFunText = "";
         if (typeof(colbackFun) === 'function') {
             colbackFunText = "&colbackFun="+colbackFun.toString();
         }
         loadScript(url+"?Form="+formName+"&dataset="+datasetName+"&data="+JSON.stringify(objectQuery)+colbackFunText).then(function(script) {
         },function(error) {
             console.log(error);
         });
     }else{
         // var ctrlObj = Ext.getCmp(_domParent['mainForm'])
         var ctrlObj = _domParent;
         var request = new XMLHttpRequest(); // CreateRequest();
         //request.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8");
         request.open('POST', url, true);  // `false` makes the request synchronous
         request.setRequestHeader('Content-type', 'application/json');
         var countQuery = 0; // необходимо уточнить почему производится 3 запроса, вмнсто одного
         request.onreadystatechange = function() {
           if (request.status === 200) {
                countQuery++;
                if (countQuery == 2) {
                    ctrlObj['actionList'][datasetName] = JSON.parse(request.responseText);
                    for (var key in ctrlObj['actionList'][datasetName]) {
                        var ctrlObjFild = ctrlObj.query('[name='+key+']');
                        if ((ctrlObjFild) && (ctrlObjFild.length>0)) {
                           ctrlObjFild[0].setValue(ctrlObj['actionList'][datasetName][key]);
                           delete ctrlObj['actionList'][datasetName][key];
                        } else {
                           if (typeof(ctrlObj['vars']) == "undefined") ctrlObj['vars'] = {};
                           ctrlObj['vars'][key]=ctrlObj['actionList'][datasetName][key]
                        }
                    }
                    if (typeof(colbackFun) === 'function') {
                        colbackFun(ctrlObj['actionList'][datasetName]);
                    }
                }
           }
         };
         request.send(JSON.stringify({'Form':formName,'dataset':datasetName, 'data':objectQuery}));
         return request;
     }
}
function registerWinForm(formName, _domChildren) {
     if (typeof(window.ExtObj) === 'undefined') {
        window.ExtObj = {};
     }
     if (typeof(window.ExtObj["FormsObject"]) === 'undefined') {
        window.ExtObj["FormsObject"] = {};
     }
     if (typeof(window.ExtObj["FormsObject"][formName]) === 'undefined') {
        window.ExtObj["FormsObject"][formName] = {};
     }
     if (typeof(window.ExtObj["FormsObject"][formName]['objectChildren']) === 'undefined') {
        window.ExtObj["FormsObject"][formName]['objectChildren'] = _domChildren;
     }
}

// функция открытия формы через подгрузку JS файла (работает долго)
function openForm() {
     if (typeof(window.ExtObj)==='undefined') window.ExtObj = {};
     if (typeof(window.ExtObj["FormsObject"])==='undefined') window.ExtObj["FormsObject"] = {};
     if (arguments.length === 0) return;
     var formName = "";
     var isModalWin = false;
     var objectQuery = {};
     var renderTo = Ext.getBody();
     var arr = [].slice.call(arguments); // Перегружаем все аргументы в массив
     var _domParent = null;
     var containerObject = null;
     var containerObjectId = "";
     if (typeof(arr[0]) === 'object') {
        _domParent = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'string') {
        formName = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'boolean') {
        isModalWin = arr.splice(0, 1)[0];
     }
     if (typeof(arr[0]) === 'object') {
        objectQuery = arr.splice(0, 1)[0];
     }
     if ((""+arr[0]) === '[object HTMLDivElement]') {
        containerObject = arr.splice(0, 1)[0];
        if (typeof(containerObject.id) === 'undefined') {
           containerObject.id = "dom_"+makeid();
           containerObjectId = "&containerObjectId="+containerObject.id;
        }
     }
     if(formName.length === 0) return;
     var objectOnEvent = {};
     for (var key in objectQuery) {
        if (typeof(objectQuery[key]) === 'function') {
           if (key.substr(0, 2) === 'on' ) {
              objectOnEvent[key] = objectQuery[key];
              delete objectQuery[key]
           }
        }
     }
     if (isModalWin){
        objectQuery['isModal'] = true;
     }
     objectQuery["parentFrom"] = null;
     if (_domParent!=null) {
        objectQuery["parentFrom"] = _domParent["mainForm"];
     }
     var rightNow = new Date();
     var dateText = rightNow.toISOString().slice(0,10).replace(/-/g,"");
     objectToStr(objectQuery); // конвертируем JS функции в строку
     if (formName.indexOf('.') === -1) {formName+=".frm"}
     if (typeof(window.ExtObj["FormsObject"][formName]) === 'undefined') {
        window.ExtObj["FormsObject"][formName] = {};
     }
     window.ExtObj["FormsObject"][formName]['objectOnEvent'] = objectOnEvent; // пробрасываем события между модальными окнами внутри одного физического окна
     // localStorage.setItem("ExtJsFormVars:"+formName, JSON.stringify(objectQuery)); // необходимо для проброса переменных между окнами
     loadScriptSyn("/"+formName+"?type=js"+containerObjectId+"&data="+JSON.stringify(objectQuery), function(){
         if ((!isModalWin) && (_domParent !== null ) && (typeof(_domParent["mainForm"]) !=='undefined')) {
             Ext.getCmp(_domParent["mainForm"]).destroy(); // уничтожаем родителя
         }
         var winCildren = window.ExtObj["FormsObject"][formName]['objectChildren'];
         _domParent['objectOnEvent'] = objectOnEvent;
         window.ExtObj["FormsObject"][formName]['objectChildren']['mainParentFile'] = _domParent['mainFile'];



         /*
         window.parentWin = _domParent;
         window.childrenWin = winCildren;
         winCildren['mainParentFile'] = _domParent['mainFile'];
         winCildren['mainParentWin'] = _domParent;
         winCildren['mainParentOnEvent'] = objectOnEvent;
         window.ExtObj["FormsObject"][formName]['mainParentFile']    = _domParent['mainFile'];
         window.ExtObj["FormsObject"][formName]['mainParentWin']     = _domParent;
         window.ExtObj["FormsObject"][formName]['mainParentOnEvent'] = objectOnEvent;
         console.log('window.ExtObj["FormsObject"][formName]', window.ExtObj["FormsObject"][formName]);
         */
     });
}


function getRandomString(length) {
    var randomChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var result = '';
    for ( var i = 0; i < length; i++ ) {
        result += randomChars.charAt(Math.floor(Math.random() * randomChars.length));
    }
    return result;
}

function makeid() {
  var text = "";
  var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  for (var i = 0; i < 5; i++) {
     text += possible.charAt(Math.floor(Math.random() * possible.length));
  }
  return text;
}
