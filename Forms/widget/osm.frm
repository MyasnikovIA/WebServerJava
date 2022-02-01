<div extend="Ext.panel.Panel" >
    <cmpScript>
        <![CDATA[
                   var localConfig = arguments[0];
                   var localElement = this.element;
                   console.log("this",this.id)
                   localObj = this
                   loadCSS("/lib/OpenStreetMap/Leaflet.css").then(function(){
                     loadScript("/lib/OpenStreetMap/Leaflet.js").then(function(){
                         if (!window.L) {
                             alert('No Leaflet library');
                         } else {
                             var map = L.map(localObj['id']);
                             // var osmUrl = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
                             var osmUrl = '/openstreetmap/{s}/{z}/{x}/{y}.png'; // адрес кэширования загруженой карты
                             var osmAttrib = '';
                             var popup = L.popup();
                             var mapclick = function(e){}
                             var osm = new L.TileLayer(osmUrl, {
                                 minZoom: 0,
                                 maxZoom: 18,
                                 attribution: osmAttrib
                             });
                             map.setView(new L.LatLng(53.3594112 , 83.6796416), 12, { reset: true });
                             map.addLayer(osm);

                             //var marker = L.marker([53.3594112, 83.6796416], {
                             //    icon: L.icon({
                             //        iconUrl: '/images/geo_point.png',
                             //        iconSize: [41, 41],
                             //        iconAnchor: [12, 40],
                             //        popupAnchor: [0, -35]
                             //    })
                             //}).addTo(map);
                             //marker.bindPopup('<b>Baldwin City, KS</b><br>').openPopup();

                             // http://leaflet.github.io/Leaflet.label/
                             //  var p = L.polyline([
                             //      [-37.7612, 175.2756],
                             //      [-37.7702, 175.2796],
                             //      [-37.7802, 175.2750]
                             //  ],{ weight: 12, color: '#fe57a1' }).bindLabel('Even polylines can have labels.', { direction: 'auto' }).addTo(map);


                             var panelOSM = document.querySelectorAll(".leaflet-tile-container");
                             for (var ind = 0; ind < panelOSM.length; ++ind) {
                                panelOSM[ind].style['cursor'] = 'default';
                             }
                             var controls = document.querySelectorAll(".leaflet-control-attribution");
                             for (var ind = 0; ind < controls.length; ++ind) {
                                controls[ind].style['display'] = 'none';
                             }
                             localObj['localObj'] ={};
                             localObj['selectMarker'] = null;
                             localObj['setPoint']=function(){}
                             function onMapClick(e) {
                                alert("You clicked the map at " + e.latlng);
                             }
                             if (typeof(localConfig['listeners']) !== 'undefined'){
                                 for (let key in localConfig['listeners']) {
                                    if (['mapclick'].includes(key)) {
                                      mapclick = localConfig['mapclick'][key]
                                    }
                                    if (['click','dblclick'].includes(key)) {
                                      map.on(key, function(pointClick) {
                                          localObj.clickPoint = pointClick;
                                          mapclick(pointClick)
                                          localConfig['listeners'][key](pointClick);
                                      });
                                      continue;
                                    }
                                    if (['itemcontextmenu','contextmenu'].includes(key)) {
                                      map.on('contextmenu', function(pointClick) {
                                          localObj.clickPoint = pointClick;
                                          localConfig['listeners'][key](pointClick);
                                      });
                                      continue;
                                    }
                                    map.on(key, localConfig['listeners'][key]);
                                 }
                             }
                             localObj['map'] = map;
                             localObj['labelList'] = [];
                             localObj['clickPoint'] = null;
                             localObj['selectMarker'] = null;
                             // Установить новую точку на карте
                             localObj['appLabel'] = function() {
                                   let html="";
                                   let title = "";
                                   let icon = "";
                                   let lat = 0;
                                   let lng = 0;
                                   if ((typeof(localObj['clickPoint']) !== 'undefined') && (typeof(localObj['clickPoint']['latlng']) !== 'undefined')) {
                                       lat = localObj['clickPoint']['latlng']['lat'];
                                       lng = localObj['clickPoint']['latlng']['lng'];
                                   }
                                   let arr = [].slice.call(arguments); // Перегружаем все аргументы в массив
                                   if (typeof(arr[0]) === 'object'){
                                        objLocation= arr.splice(0, 1)[0];
                                        if (typeof(objLocation['lat']) !== 'undefined') {
                                            lat = objLocation['lat'];
                                        }
                                        if (typeof(objLocation['lng']) !== 'undefined') {
                                            lng = objLocation['lng'];
                                        }
                                        if (typeof(objLocation['lon']) !== 'undefined') {
                                            lng = objLocation['lon'];
                                        }
                                   }
                                   if (typeof(arr[0]) === 'string')  html = arr.splice(0, 1)[0];
                                   if (typeof(arr[0]) === 'string')  title = arr.splice(0, 1)[0];
                                   if (typeof(arr[0]) === 'string')  icon = arr.splice(0, 1)[0];

                                   if (typeof(icon) === 'undefined') {
                                      icon = '/images/geo_point.png';
                                   }
                                    let marker = new L.Marker([lat,lng], {
                                       icon: new L.DivIcon({
                                           //iconUrl: icon,
                                           iconSize: [62, 62],
                                           iconAnchor: [12, 40],
                                           //popupAnchor: [0, -35],
                                           // className: 'my-div-icon',
                                           html: html
                                       })
                                   }).addTo(map)
                                   .on('click', function(){
                                      debugger
                                       localObj['clickPoint'] = arguments[0]
                                       localObj['selectMarker'] = marker;
                                   });
                                   // marker.bindPopup(title).openPopup();
                                   localObj['labelList'].push(marker);
                             }

                             // Удалить выбранную метку
                             localObj['delLabel'] = function() {
                                if (localObj['selectMarker'] !== null) {
                                    map.removeLayer(localObj['selectMarker'])
                                    const index = localObj['labelList'].indexOf(localObj['selectMarker']);
                                    if (index > -1) {
                                      localObj['labelList'].splice(index, 1);
                                    }
                                    localObj['selectMarker'] = null;
                                }
                             }

                             // Удалить все метки
                             localObj['delLabels'] = function() {
                                 for (let ind = 0; ind < localObj['labelList'].length; ++ind) {
                                    map.removeLayer(localObj['labelList'][ind]);
                                    const index = localObj['labelList'].indexOf(localObj['labelList'][ind]);
                                    if (index > -1) {
                                      localObj['labelList'].splice(index, 1);
                                    }
                                 }
                                 localObj['selectMarker'] = null;
                             }

                             // Получить список точек на карте
                             localObj['getLabels'] = function() {
                                 return localObj['labelList'];
                             }

                             // получить путь между  геоточками (Дописать)
                             localObj['getRoutePoint'] = function() {

                             }

                             localObj['drawLine'] = function(arr) {
                                  var p = L.polyline([[-37.7612, 175.2756], [-37.7702, 175.2796],[-37.7802, 175.2750]],{ weight: 12, color: '#fe57a1' }).bindLabel('Even polylines can have labels.', { direction: 'auto' }).addTo(map);
                             }

                             // Поиск геообъекта по  слову
                             localObj['foundObject'] = function (FoundObjectText) {
                                var urlLoad = 'http://nominatim.openstreetmap.org/?format=json&addressdetails=1&q=' + encodeURIComponent(FoundObjectText);
                                return getJsonUrl(urlLoad);
                            }

                            localObj['getInfo'] = function (latlng) {
                                if (typeof(latlng) === 'undefined') {
                                    latlng = localObj['clickPoint'];
                                }
                                var locatObjUrl = "http://nominatim.openstreetmap.org/reverse?format=json&lat=" + latlng.latlng['lat'] + "&lon=" + latlng.latlng['lng'] + "&zoom=18&addressdetails=1";
                                return getJsonUrl(locatObjUrl);
                             }
                             // Показать текстовую информацию на карте
                             localObj['setInfo'] = function(e,txt) {
                                popup.setLatLng(e.latlng)
                                .setContent(txt)
                                .openOn(map);
                             }

                             // Показать текстовую информацию на карте
                             localObj['setInfo'] = function(e,txt) {
                                popup.setLatLng(e.latlng)
                                .setContent(txt)
                                .openOn(map);
                             }
                             // Получение позиции устройства
                             let markerPosition, circlePosition;
                             localObj['getPosition'] = function (position) {
                                if (typeof(Android) === 'undefined') {
                                    if (!navigator.geolocation) {
                                        console.log("Your browser doesn't support geolocation feature!")
                                    } else {
                                        navigator.geolocation.getCurrentPosition(getPosition)
                                    };
                                    function getPosition(position) {
                                        // console.log(position)
                                        lat = position.coords.latitude
                                        long = position.coords.longitude
                                        accuracy = position.coords.accuracy
                                        if (markerPosition) {
                                            map.removeLayer(markerPosition)
                                        }
                                        if (circlePosition) {
                                            map.removeLayer(circlePosition)
                                        }
                                        markerPosition = L.marker([lat, long])
                                        circlePosition = L.circle([lat, long], { radius: accuracy })
                                        var featureGroup = L.featureGroup([markerPosition, circlePosition]).addTo(map)
                                        map.fitBounds(featureGroup.getBounds())
                                        // console.log("Your coordinate is: Lat: " + lat + " Long: " + long + " Accuracy: " + accuracy)
                                    }
                                } else {
                                   // Получит координаты из датчиков Андроид устройства
                                }
                            }


                             //map.on('click', onMapClick);
                             console.log("localConfig",localConfig)
                             console.log("localObj",localObj)
                             // дописать инициализацию компонента
                         }
                     },function(error){ console.log(error);})
                 },function(error){console.log(error);})
       ]]>
    </cmpScript>
</div>