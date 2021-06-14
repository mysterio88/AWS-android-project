var myHeaders = new Headers();
myHeaders.set('Cache-Control', 'no-store');
var urlParams = new URLSearchParams(window.location.search);
var tokens;
var domain = "";
var region = "";
var appClientId = "";
var userPoolId = "";
var redirectURI = "https://.cloudfront.net/index.html";

const form = document.querySelector('form');
var textBox;

//jsonfile
var file1;

//USERPOOLTESTVARIABLES
var s3;
var albumBucketName = "";
var bucketRegion = "";
var IdentityPoolId = "";





//Convert Payload from Base64-URL to JSON
const decodePayload = payload => {
  const cleanedPayload = payload.replace(/-/g, '+').replace(/_/g, '/');
  const decodedPayload = atob(cleanedPayload)
  const uriEncodedPayload = Array.from(decodedPayload).reduce((acc, char) => {
    const uriEncodedChar = ('00' + char.charCodeAt(0).toString(16)).slice(-2)
    return `${acc}%${uriEncodedChar}`
  }, '')
  const jsonPayload = decodeURIComponent(uriEncodedPayload);

  return JSON.parse(jsonPayload)
}

//Parse JWT Payload
const parseJWTPayload = token => {
    const [header, payload, signature] = token.split('.');
    const jsonPayload = decodePayload(payload)

    return jsonPayload
};

//Parse JWT Header
const parseJWTHeader = token => {
    const [header, payload, signature] = token.split('.');
    const jsonHeader = decodePayload(header)

    return jsonHeader
};

//Generate a Random String
const getRandomString = () => {
    const randomItems = new Uint32Array(28);
    crypto.getRandomValues(randomItems);
    const binaryStringItems = randomItems.map(dec => `0${dec.toString(16).substr(-2)}`)
    return binaryStringItems.reduce((acc, item) => `${acc}${item}`, '');
}

//Encrypt a String with SHA256
const encryptStringWithSHA256 = async str => {
    const PROTOCOL = 'SHA-256'
    const textEncoder = new TextEncoder();
    const encodedData = textEncoder.encode(str);
    return crypto.subtle.digest(PROTOCOL, encodedData);
}

//Convert Hash to Base64-URL
const hashToBase64url = arrayBuffer => {
    const items = new Uint8Array(arrayBuffer)
    const stringifiedArrayHash = items.reduce((acc, i) => `${acc}${String.fromCharCode(i)}`, '')
    const decodedHash = btoa(stringifiedArrayHash)

    const base64URL = decodedHash.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
    return base64URL
}

// Main Function
async function main() {
  var code = urlParams.get('code');

  //If code not present then request code else request tokens
  if (code == null){

    // Create random "state"
    var state = getRandomString();
    sessionStorage.setItem("pkce_state", state);

    // Create PKCE code verifier
    var code_verifier = getRandomString();
    sessionStorage.setItem("code_verifier", code_verifier);

    // Create code challenge
    var arrayHash = await encryptStringWithSHA256(code_verifier);
    var code_challenge = hashToBase64url(arrayHash);
    sessionStorage.setItem("code_challenge", code_challenge)

    // Redirtect user-agent to /authorize endpoint
    location.href = "https://"+domain+".auth."+region+".amazoncognito.com/oauth2/authorize?response_type=code&state="+state+"&client_id="+appClientId+"&redirect_uri="+redirectURI+"&scope=openid&code_challenge_method=S256&code_challenge="+code_challenge;
  } else {

    // Verify state matches
    state = urlParams.get('state');
    if(sessionStorage.getItem("pkce_state") != state) {
        alert("Invalid state");
    } else {

    // Fetch OAuth2 tokens from Cognito
    code_verifier = sessionStorage.getItem('code_verifier');
  await fetch("https://"+domain+".auth."+region+".amazoncognito.com/oauth2/token?grant_type=authorization_code&client_id="+appClientId+"&code_verifier="+code_verifier+"&redirect_uri="+redirectURI+"&code="+ code,{
  method: 'post',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  }})
  .then((response) => {
    return response.json();
  })
  .then((data) => {

    // Verify id_token
    tokens=data;
    var idVerified = verifyToken (tokens.id_token);
    Promise.resolve(idVerified).then(function(value) {
      if (value.localeCompare("verified")){
        alert("Invalid ID Token - "+ value);
        console.log("EI VALIDI IDTOKENI");
        return;
      }
    });

    //PHOTOALBUMTESTITESTIIII
    AWS.config.region = bucketRegion;
    AWS.config.credentials = new AWS.CognitoIdentityCredentials({
      IdentityPoolId: IdentityPoolId,
      Logins: {
        '': tokens.id_token
      }
    });

    AWS.config.credentials.get(function(err) {
      if (err) console.log(err);
      else console.log(AWS.config.credentials);
    });

    s3 = new AWS.S3({
      apiVersion: "2006-03-01",
      params: { Bucket: albumBucketName }
    });

    console.log(tokens.id_token);
  });

    // Fetch from /user_info
    await fetch("https://"+domain+".auth."+region+".amazoncognito.com/oauth2/userInfo",{
      method: 'post',
      headers: {
        'authorization': 'Bearer ' + tokens.access_token
    }})
    .then((response) => {
      return response.json();
    })
    .then((data) => {
      // Display user information
    });
  }}}

  //PHOTOALBUMTESTI

  
  function listAlbums() {
    var file1;
    var params = {
      Bucket: albumBucketName, 
      Key: "resources/db.json"
    };

    const url = s3.getSignedUrl('getObject', {
      Bucket: albumBucketName,
      Key: "resources/db.json"
      //Expires: signedUrlExpireSeconds
    })

    console.log(url);
    
    s3.listObjects({ Delimiter: "/" }, function(err, data) {
      if (err) {
        return alert("There was an error listing your albums: " + err.message);
      } else {
        var albums = data.CommonPrefixes.map(function(commonPrefix) {
          var prefix = commonPrefix.Prefix;
          var albumName = decodeURIComponent(prefix.replace("/", ""));
          console.log(albumName);
          return getHtml([
            "<li>",
            //"<span onclick=\"deleteAlbum('" + albumName + "')\">X</span>",
            "<span class='albumListItem' onclick=\"viewAlbum('" + albumName + "')\">",
            albumName,
            "</span>",
            "</li>"
          ]);
        });
        var message = albums.length
          ? getHtml([
              "<p>Click on an album name to view it.</p>",
              //"<p>Click on the X to delete the album.</p>"
            ])
          : "<p>You do not have any albums. Please Create album.";
        var htmlTemplate = [
          "<h2>Albums</h2>",
          message,
          "<ul>",
          getHtml(albums),
          "</ul>",
          "<button onclick=\"createAlbum(prompt('Enter Album Name:'))\">",
          "Create New Album",
          "</button>"
        ];
        document.getElementById("app").innerHTML = getHtml(htmlTemplate);
      }
    });
    

  }

  function createAlbum(albumName) {
    albumName = albumName.trim();
    if (!albumName) {
      return alert("Album names must contain at least one non-space character.");
    }
    if (albumName.indexOf("/") !== -1) {
      return alert("Album names cannot contain slashes.");
    }
    var albumKey = encodeURIComponent(albumName);
    s3.headObject({ Key: albumKey }, function(err, data) {
      if (!err) {
        return alert("Album already exists.");
      }
      if (err.code !== "NotFound") {
        return alert("There was an error creating your album: " + err.message);
      }
      s3.putObject({ Key: albumKey }, function(err, data) {
        if (err) {
          return alert("There was an error creating your album: " + err.message);
        }
        alert("Successfully created album.");
        viewAlbum(albumName);
      });
    });
  }
  
  function viewAlbum(albumName) {

    const template = [];
    var indexTemplate = -1;

    var params = {
      Bucket: albumBucketName, 
      Key: "resources/db.json"
    };

    s3.getObject(params, function(err, data) {
      if (err) console.log(err, err.stack); // an error occurred
      else{
        console.log(data);           // successful response
        file1 = JSON.parse(data.Body);
        console.log(file1);
        console.log(file1.diat1.length);
        if (albumName == "diat1"){
          for(var i = 0; i < file1.diat1.length; i++){
            template[i] = `
            <form id="textboxes${i}">
            <div class="cardTitle">
            <textarea name="title" required placeholder='${file1.diat1[i].title}'>${file1.diat1[i].title}</textarea>
            </div>
            <div class="cardBody">
            <textarea name="body" required placeholder='${file1.diat1[i].body}'>${file1.diat1[i].body}</textarea>
            <button type="button" onclick="createPost(${i}, '${albumName}')">Create</button>
            </div>
            </form>
            `
          }
        }else if (albumName == "diat2"){
          for(var i = 0; i < file1.diat2.length; i++){ 
            template[i] = `
            <form id="textboxes${i}">
            <div class="cardTitle">
            <textarea name="title" required placeholder='${file1.diat2[i].title}'>${file1.diat2[i].title}</textarea>
            </div>
            <div class="cardBody">
            <textarea name="body" required placeholder='${file1.diat2[i].body}'>${file1.diat2[i].body}</textarea>
            <button type="button" onclick="createPost(${i}, '${albumName}')">Create</button>
            </div>
            </form>
            `
          }
        }    
      }     

    });
    
    var albumPhotosKey = encodeURIComponent(albumName) + "/";
    console.log(albumPhotosKey);

    s3.listObjects({ Prefix: albumPhotosKey }, function(err, data) {
      if (err) {
        return alert("There was an error viewing your album: " + err.message);
      }
      // 'this' references the AWS.Response instance that represents the response
      var href = this.request.httpRequest.endpoint.href;
      var bucketUrl = "https://.cloudfront.net/"; //href + albumBucketName + "/";
      
      var photos = data.Contents.map(function(photo) {
        indexTemplate += 1;
        console.log(indexTemplate);

        var photoKey = photo.Key;
        var photoUrl = bucketUrl + encodeURIComponent(photoKey);
        return getHtml([                    //Uusi addphoto nappi täällä
          "<div class='cards'>", //id='cards" + indexTemplate + "'>"  
          "<span>",
          "<div class='cardPic'>",
          '<img width="250" height="250" src="' + photoUrl + '"/>',
          "<span class='deletePhotoX' onclick=\"deletePhoto('" +
            albumName +
            "','" +
            photoKey +
            "')\">",
          "X",
          "</span>",

          "</div>",
          "<div class='uploadPhotoDiv'>",
          
          '<input id="photoupload' + indexTemplate + '" type="file" accept="image/jpg">', //accept="image/*"
          '<button id="addphoto" onclick="addPhoto(\'' + albumName + "','" + indexTemplate + "')\">",
          "Add Photo",
          "</button>",

          "<span>",
          photoKey.replace(albumPhotosKey, ""),
          "</span>",
          "</div>",
          template[indexTemplate],

          "</span>",
          "</div>"
        ]);
      });

      indexTemplate += 1;

      var message = photos.length
        ? "<p id='titleMessage'>Click on the X to delete the photo</p>"
        : "<p id='titleMessage'>You do not have any photos in this album. Please add photos.</p>";
      var htmlTemplate = [
        "<h2 id='albumTitle'>",
        "Album: " + albumName,
        "</h2>",
        message,
        "<div class='allCards'>",
        getHtml(photos),
        "</div>",//Tässä oli addphotonappi!
        "<div class='bottomButtons'>",
        '<input id="photoupload' + indexTemplate + '" type="file" accept="image/jpg">', //accept="image/*"
        '<button id="addphoto" onclick="addPhoto(\'' + albumName + "','" + indexTemplate + "'); createPost(\'" + indexTemplate + "','" + albumName + "')\">",
        "Add Photo",
        "</button>",
        //'<button type="button" onclick="createPost(\'' + indexTemplate + "','" + albumName + "')\">",
       // "Create",
        //"</button>",
        "</div>",
        '<button onclick="listAlbums()">',
        "Back To Albums",
        "</button>"
      ];
      document.getElementById("app").innerHTML = getHtml(htmlTemplate);
      
    });
  }

  //create and upload JSON to S3
  function createPost(numberOfButton, albumName) {
    
    console.log(numberOfButton);



    if (albumName == "diat1"){
      if (file1.diat1.length == numberOfButton){
        const doc1 = {
          title: "title",
          body: "body",
          tag: albumName
        }
        file1.diat1.push(doc1);
      } else {
        textBox = document.getElementById("textboxes" + numberOfButton).elements;
        console.log(textBox.title.value);
        file1.diat1[numberOfButton].body = textBox.body.value;
        file1.diat1[numberOfButton].title = textBox.title.value;
        file1.diat1[numberOfButton].tag = albumName;
        console.log(file1.diat1[numberOfButton].title);
        console.log(file1.diat1[numberOfButton].body);
      }
    }else if (albumName == "diat2"){
      if (file1.diat2.length == numberOfButton){
        const doc1 = {
          title: "title",
          body: "body",
          tag: albumName
        }
        file1.diat2.push(doc1);
      } else {
        textBox = document.getElementById("textboxes" + numberOfButton).elements;
        console.log(textBox.title.value);
        file1.diat2[numberOfButton].body = textBox.body.value;
        file1.diat2[numberOfButton].title = textBox.title.value;
        file1.diat2[numberOfButton].tag = albumName;
        console.log(file1.diat2[numberOfButton].title);
        console.log(file1.diat2[numberOfButton].body);
      }
    }
  
    var upload = new AWS.S3.ManagedUpload({
      params: {
        Bucket: albumBucketName,
        Key: "resources/db.json",
        Body: JSON.stringify(file1)
      }
    });
  
    var promise = upload.promise();
  
    promise.then(
      function(data) {
        alert("Successfully uploaded photo.");
        viewAlbum(albumName);
      },
      function(err) {
        return alert("There was an error uploading your photo: ", err.message);
      }
    );

  }
  
  function addPhoto(albumName, indexOfButton) {
    var files = document.getElementById("photoupload"+indexOfButton).files;

    if (!files.length) {
      return alert("Please choose a file to upload first.");
    }


    var file = files[0];


    var fileName = albumName + indexOfButton + ".jpg";//file.name;+
    var albumPhotosKey = encodeURIComponent(albumName) + "/";
    var photoKey = albumPhotosKey + fileName;

    console.log(photoKey);

    // Use S3 ManagedUpload class as it supports multipart uploads
    var upload = new AWS.S3.ManagedUpload({
      params: {
        Bucket: albumBucketName,
        Key: photoKey,
        Body: file
      }
    });

    console.log(upload);

    var promise = upload.promise();
  
    promise.then(
      function(data) {
        alert("Successfully uploaded photo.");
        viewAlbum(albumName);
      },
      function(err) {
        return alert("There was an error uploading your photo: ", err.message);
      }
    );
  }
  
  function deletePhoto(albumName, photoKey) {
    s3.deleteObject({ Key: photoKey }, function(err, data) {
      if (err) {
        return alert("There was an error deleting your photo: ", err.message);
      }
      alert("Successfully deleted photo.");
      viewAlbum(albumName);
    });
  }
  
  function deleteAlbum(albumName) {
    var albumKey = encodeURIComponent(albumName) + "/";
    s3.listObjects({ Prefix: albumKey }, function(err, data) {
      if (err) {
        return alert("There was an error deleting your album: ", err.message);
      }
      var objects = data.Contents.map(function(object) {
        return { Key: object.Key };
      });
      s3.deleteObjects(
        {
          Delete: { Objects: objects, Quiet: true }
        },
        function(err, data) {
          if (err) {
            return alert("There was an error deleting your album: ", err.message);
          }
          alert("Successfully deleted album.");
          listAlbums();
        }
      );
    });
  }

  main();