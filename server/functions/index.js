const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.database();
const ref = db.ref();

exports.addMessage = functions.https.onRequest(async (req, res) => {

    let timenow =  Math.round(Date.now() / 1000 - 21600);
    let array = [];

    if (req.body.what === "how") {
        ref.child("labelRYG").orderByKey().startAt(timenow.toString()).once("value", function(data) {
            data.forEach(function (data2) {
                array.push({"num": parseInt(data2.key)});
            });
            res.send(array);
        });
    }

    if (req.body.what === "send") {
        let num = req.body.num;
        ref.child("labelRYG/"+num).once("value", function(data) {
  //          data.forEach(function (data2) {
                array.push(data);
  //          });
            ref.child("pointRYG/"+num).once("value", function (data) {
                data.forEach(function (data2) {
              //      data2.forEach(function (data3) {
                        array.push(data2);
              //      })
                });
                res.send(array);
            });

        });
    }
});

exports.QRcod = functions.https.onRequest(async (req, res) => {
    if (req.query.text === "allin"){
        ref.child("QRCodes").once("value", function (data) {
            res.set('Access-Control-Allow-Origin', '*');
            res.json(data.val());
        });
    }
    if (req.query.text === "del"){
        let guild = req.query.guild;
        let unit = req.query.unit;
        let idMetka = req.query.idMetka;
        if (guild !== "" && unit !== "" &&idMetka !== ""){
            ref.child("QRCodes").child(guild).child(unit).child(idMetka).remove();
            res.set('Access-Control-Allow-Origin', '*');
            res.send("del good");
        } else {
            res.set('Access-Control-Allow-Origin', '*');
            res.send("del bad");
        }

    }
    if (req.query.text === "cln"){
        let cln1guild = req.query.cln1guild;
        let cln1unit = req.query.cln1unit;
        let cln1idMetka = req.query.cln1idMetka;
        let cln2guild = req.query.cln2guild;
        let cln2unit = req.query.cln2unit;
        let cln2idMetka = req.query.cln2idMetka;


        if (cln1guild !== "" && cln1unit !== "" && cln1idMetka !== "" &&
            cln2guild !== "" && cln2unit !== "" && cln2idMetka !== ""
        ){
            ref.child("QRCodes").child(cln1guild).child(cln1unit).child(cln1idMetka)
                .once("value", function (data) {
                    ref.child("QRCodes").child(cln2guild).child(cln2unit).child(cln2idMetka).
                        set(data.val());
                    res.set('Access-Control-Allow-Origin', '*');
                    res.send("cln good");
            });
        } else {
            res.set('Access-Control-Allow-Origin', '*');
            res.send("cln bad");
        }

    }
    if (req.query.text === "addM"){
        let guild = req.query.guild;
        let unit = req.query.unit;
        let idMetka = req.query.idMetka;
        let label = req.query.label;

        if (guild !== "" && unit !== "" && idMetka !== "" &&
            label !== ""){
            ref.child("QRCodes").child(guild).child(unit).child(idMetka).child("label").
            set(label);
            res.set('Access-Control-Allow-Origin', '*');
            res.send("add good");
        } else {
            res.set('Access-Control-Allow-Origin', '*');
            res.send("add bad");
        }

    }
    if (req.query.text === "delPC"){
        let guild = req.query.guild;
        let unit = req.query.unit;
        let idMetka = req.query.idMetka;
        let pointControl = req.query.pointControl;
        if (guild !== "" && unit !== "" &&idMetka !== "" && pointControl !== ""){
            ref.child("QRCodes").child(guild).child(unit).child(idMetka).child("node").child(pointControl).remove();
            res.set('Access-Control-Allow-Origin', '*');
            res.send("del good");
        } else {
            res.set('Access-Control-Allow-Origin', '*');
            res.send("del bad");
        }
    }
    if (req.query.text === "clnPC"){
        let cln1guild = req.query.cln1guild;
        let cln1unit = req.query.cln1unit;
        let cln1idMetka = req.query.cln1idMetka;
        let cln2guild = req.query.cln2guild;
        let cln2unit = req.query.cln2unit;
        let cln2idMetka = req.query.cln2idMetka;
        let cln1idPC = req.query.cln1idPC;
        let cln2idPC = req.query.cln2idPC;

        if (cln1guild !== "" && cln1unit !== "" && cln1idMetka !== "" &&
            cln2guild !== "" && cln2unit !== "" && cln2idMetka !== "" &&
            cln1idPC !== "" && cln2idPC !== ""
        ){
            ref.child("QRCodes").child(cln1guild).child(cln1unit).child(cln1idMetka)
                .child("node").child(cln1idPC).once("value", function (data) {
                    ref.child("QRCodes").child(cln2guild).child(cln2unit).child(cln2idMetka)
                        .child("node").child(cln2idPC).set(data.val());
                    res.set('Access-Control-Allow-Origin', '*');
                    res.send("cln good");
                });
        } else {
            res.set('Access-Control-Allow-Origin', '*');
            res.send("cln bad");
        }

    }
    if (req.query.text === "addPC"){
        let guild = req.query.guild;
        let unit = req.query.unit;
        let idMetka = req.query.idMetka;
        let addP0 = req.query.addP0;
        let addP1 = req.query.addP1;
        let addP2 = req.query.addP2;
        let addP3 = req.query.addP3;

        if (guild !== "" && unit !== "" && idMetka !== "" && addP0 !== ""){
            ref.child("QRCodes").child(guild).child(unit).child(idMetka)
                .child("node").once("value", function (data) {
                let num = data.numChildren();
                let arr = [addP0, addP1, addP2, addP3];

                ref.child("QRCodes").child(guild).child(unit).child(idMetka)
                    .child("node").child(num).set(arr);
                res.set('Access-Control-Allow-Origin', '*');
                res.send("add good");
            });
        } else {
            res.set('Access-Control-Allow-Origin', '*');
            res.send("add bad");
        }

    }
});



// let pin = JSON.parse(req.body, function(k, v) {
//      if (k === 'what') return v
//  });

// if(JSON.parse())
//    ref.child("temp").push().set(req.body);
//    ref.child("temp").push().set(req.body.what);
//
//    ref.child("labelRYG").orderByKey().startAt(timenow.toString()).once("value", function(data) {
//        data.forEach(function (data2) {
//            //      console.log("data2: "+JSON.stringify(data2));
//            array.push(data2);
//
// //           console.log("array: " + JSON.stringify(array));
//        });
//        ref.child("pointRYG").orderByKey().startAt(timenow.toString()).once("value", function (data) {
//            data.forEach(function (data2) {
//                data2.forEach(function (data3) {
//                    //  console.log("data3: "+JSON.stringify(data3));
//                    array.push(data3);
//                    //  console.log("array: "+JSON.stringify(array));
//                })
//
//            });
//  //          console.log("data: " + array);
//            res.send(array);
//        });
//    });