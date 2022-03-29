let admin = require("firebase-admin");
let serviceAccount = require("./firebase-adminsdk.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://otdo-xxxxx.firebaseio.com"
});
const db = admin.database();
const ref = db.ref();


let body ={
    "what": 1,
    "ever": "you",
    "need": "to send as the body"
}

let pin;
pin = body.what;
console.log(pin);