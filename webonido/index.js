
let inputData = {};
let xhr = new XMLHttpRequest();
let jurl = require("./firebase-admin.json");
let url = new URL(jurl.url);


$(function() {

    function sendHTTP() {
        xhr.open('GET', url);
        xhr.send();
        xhr.onload = function() {
            if (xhr.status !== 200) {
                alert(`Ошибка ${xhr.status}: ${xhr.statusText}`);
            } else {
                renderJson() ;
            }
        };
        xhr.onerror = function() {
            alert("Запрос не удался");
        };
    }

    function renderJson() {
        url.searchParams.set('text', 'allin');
        xhr.open('GET', url);
        xhr.send();
        xhr.onload = function() {
            if (xhr.status !== 200) {
                alert(`Ошибка ${xhr.status}: ${xhr.statusText}`);
            } else {
                inputData = JSON.parse(xhr.response) ;
            }
        };
        xhr.onerror = function() {
            alert("Запрос не удался");
        };

        var options = {
            collapsed: true,
            rootCollapsable: false
        };
        $('#json-renderer').jsonViewer(inputData, options);
    }

    function deleteQRmetka() {
        let guild = $('#guild').val();
        let unit = $('#unit').val();
        let idMetka = $('#idMetka').val();
        url.searchParams.set('text', 'del');
        url.searchParams.set('guild', guild);
        url.searchParams.set('unit', unit);
        url.searchParams.set('idMetka', idMetka);
        sendHTTP();
    }

    function clonQRmetka() {
        let cln1guild = $('#cln1guild').val();
        let cln1unit = $('#cln1unit').val();
        let cln1idMetka = $('#cln1idMetka').val();
        let cln2guild = $('#cln2guild').val();
        let cln2unit = $('#cln2unit').val();
        let cln2idMetka = $('#cln2idMetka').val();
        url.searchParams.set('text', 'cln');
        url.searchParams.set('cln1guild', cln1guild);
        url.searchParams.set('cln1unit', cln1unit);
        url.searchParams.set('cln1idMetka', cln1idMetka);
        url.searchParams.set('cln2guild', cln2guild);
        url.searchParams.set('cln2unit', cln2unit);
        url.searchParams.set('cln2idMetka', cln2idMetka);
        sendHTTP();
    }

    function addQRmetka() {
        let guild = $('#addGuild').val();
        let unit = $('#addUnit').val();
        let idMetka = $('#addIdMetka').val();
        let label = $('#addLabel').val();
        url.searchParams.set('text', 'addM');
        url.searchParams.set('guild', guild);
        url.searchParams.set('unit', unit);
        url.searchParams.set('idMetka', idMetka);
        url.searchParams.set('label', label);
        sendHTTP();
    }

    function delPC() {
        let guild = $('#delGuild').val();
        let unit = $('#delUnit').val();
        let idMetka = $('#delIdMetka').val();
        let pointControl = $('#delPointControl').val();
        url.searchParams.set('text', 'delPC');
        url.searchParams.set('guild', guild);
        url.searchParams.set('unit', unit);
        url.searchParams.set('idMetka', idMetka);
        url.searchParams.set('pointControl', pointControl);
        sendHTTP();
    }

    function addPoint() {
        let guild = $('#addPGuild').val();
        let unit = $('#addPUnit').val();
        let idMetka = $('#addPIdMetka').val();
        let addP0 = $('#addP0').val();
        let addP1 = $('#addP1').val();
        let addP2 = $('#addP2').val();
        let addP3 = $('#addP3').val();
        url.searchParams.set('text', 'addPC');
        url.searchParams.set('guild', guild);
        url.searchParams.set('unit', unit);
        url.searchParams.set('idMetka', idMetka);
        url.searchParams.set('addP0', addP0);
        url.searchParams.set('addP1', addP1);
        url.searchParams.set('addP2', addP2);
        url.searchParams.set('addP3', addP3);
        sendHTTP();
    }


    function clonPC() {
        let cln1guild = $('#clnP1guild').val();
        let cln1unit = $('#clnP1unit').val();
        let cln1idMetka = $('#clnP1idMetka').val();
        let cln2guild = $('#clnP2guild').val();
        let cln2unit = $('#clnP2unit').val();
        let cln2idMetka = $('#clnP2idMetka').val();
        let cln1idPC = $('#cln1idPC').val();
        let cln2idPC = $('#cln2idPC').val();
        url.searchParams.set('text', 'clnPC');
        url.searchParams.set('cln1guild', cln1guild);
        url.searchParams.set('cln1unit', cln1unit);
        url.searchParams.set('cln1idMetka', cln1idMetka);
        url.searchParams.set('cln2guild', cln2guild);
        url.searchParams.set('cln2unit', cln2unit);
        url.searchParams.set('cln2idMetka', cln2idMetka);
        url.searchParams.set('cln1idPC', cln1idPC);
        url.searchParams.set('cln2idPC', cln2idPC);
        sendHTTP();
    }



    // Generate on click
    $('#btn-json-viewer').click(renderJson);
    $('#btnDeleteMetka').click(deleteQRmetka);
    $('#btnClonMetka').click(clonQRmetka);
    $('#btnAddMetka').click(addQRmetka);
    $('#btnDelPC').click(delPC);
    $('#btnAddPoint').click(addPoint);
    $('#btnClonPC').click(clonPC);

    $('#delMetka').hide();
    $('#clnMetka').hide();
    $('#delPoint').hide();
    $('#newMetka').hide();
    $('#newPoint').hide();
    $('#clnPoint').hide();

    renderJson();
});

function openCity(evt, cityName) {

    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");

    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    document.getElementById(cityName).style.display = "block";

    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    evt.currentTarget.className += " active";
}