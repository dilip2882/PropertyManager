const admin = require("firebase-admin");
const { log } = require("firebase-functions/logger");
// Initialize Firebase Admin SDK
admin.initializeApp();

const functions = require("firebase-functions/v1");
const { topic } = require("firebase-functions/v1/pubsub");
const db = admin.firestore();


























//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
exports.notifyOnNewData = functions.database.ref('/notifications/users/{userId}').onCreate(async (snapshot, context) => {
  try {
    const title = "New Data Added! -> " + snapshot.val().name;
    const body = "Name - " + snapshot.val().name + "\nAge - " + snapshot.val().age + "\nCity - " + snapshot.val().city;

    // Notification payload
    const message = {
      notification: {
        title: title,
        body: body,
      },
      topic: "broadcast", // Send to all devices subscribed to the 'broadcast' topic
    };

    // Send the notification
    const response = await admin.messaging().send(message);
    log("Successfully sent message:", response);

    return response;
  } catch (error) {
    console.error("Error sending notification:", error);
    return null;
  }
});


// used to add data from powershell

/*
# Firebase Realtime Database URL
 $firebaseUrl = "https://propertymanagement-1067d-default-rtdb.firebaseio.com/notifications"

 # Authentication Token (JWT) for an authenticated user
 $authToken = "dHK1w11WgD7BSMzceTkT7tVK2pyxU0ZjWU1s3ZZe"  # Replace with a valid Firebase JWT token

 # Data to be added
 $data = @{
     "name" = "vraj"
     "age" = 22
     "city" = "New York"
 }

 # Convert data to JSON
 $jsonData = $data | ConvertTo-Json

 # Firebase path (using the authentication token in the query string)
 $firebasePath = "/users.json?auth=$authToken"

 # Send data to Firebase via a POST request
 $response = Invoke-RestMethod -Uri ($firebaseUrl + $firebasePath) -Method Post -Body $jsonData -ContentType "application/json"
 
 # Output the response from Firebase (which contains the generated ID for the new data)
 $response
 */
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------









//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// Function for sending notification to manager when the maintenance request is generated
exports.maintenancereqgen = functions.firestore.document("maintenance_requests/{docId}").onCreate(async (snapshot, context) => {
  try {
    log('request has been generated')
    const token = [];

    // Notification payload
    const message = {
      notification: {
        title: 'Maintenance Request Generated',
        body: 'A new issue has been raised',
      },
    };

    const usersRef = db.collection('users');
    const querysnapshot = await usersRef.where('role', '==', 'MANAGER').get();

    if (querysnapshot.empty) {
      log("No MANAGER users found!");
      return null;
    }

    querysnapshot.forEach(doc => {
      const userData = doc.data();
      if (userData.token && Array.isArray(userData.token)) {
        token.push(...userData.token); // Push all tokens for this user
      }
    });

    if (token.length === 0) {
      log("No tokens found for MANAGER users.");
      return null;
    }

    log("before msg", token)

    const sendPromises = token.map((tok) =>
      admin.messaging().send({
        token: tok,
        notification: message.notification,
      },
        log('send token->', tok)
      ));

    Promise.all(sendPromises)
      .then((responses) => {
        log('Successfully sent messages:', responses);
      })
      .catch((error) => {
        console.error('Error sending messages:', error);
      });

  } catch (error) {
    console.error("Error sending notification:", error);
    return null;
  }
});
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------







//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
// Function to send a broadcast notification
exports.sendBroadcastNotification = functions.https.onRequest(async (req, res) => {
  try {
    // Check if the body contains required fields
    const { title, body } = req.body;
    if (!title || !body) {
      return res.status(400).send({
        success: false,
        message: "Missing 'title' or 'body' in the request payload.",
      });
    }
    // Notification payload
    const message = {
      notification: {
        title: title,
        body: body,
      },
      topic: "broadcast",
    };


    // Send notification
    const response = await admin.messaging().send(message);
    log("Successfully sent message:", response);

    // Send success response
    res.status(200).send({
      success: true,
      message: "Broadcast notification sent successfully!",
      response,
    });
  } catch (error) {
    console.error("Error sending broadcast notification:", error);

    // Send error response
    res.status(500).send({
      success: false,
      error: error.message,
    });
  }
});


// You can fire notification by sending post request from powershell or terminal 

/*
 $headers = @{
     "Content-Type" = "application/json"
 }
 $body = @{
     "title" = "Test Notification fired"
     "body" = "this is the broadcast notification for testing purpose \nso please do not panic and try to cooperate"
 } | ConvertTo-Json -Depth 10

 Invoke-WebRequest -Uri "https://sendbroadcastnotification-fyj7ieilba-uc.a.run.app" `   // This Uri can be achived from firebase console -> Functions -> deployed project
                   -Method POST `
                   -Headers $headers `
                   -Body $body
                   */
//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------






//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
exports.broadcasttenant = functions.https.onRequest(async (req, res) => {
  try {
    const token = [];  // Initialize as an array for storing tokens


    // Check if the body contains required fields
    const { title, body } = req.body;
    if (!title || !body) {
      return res.status(400).send({
        success: false,
        message: "Missing 'title' or 'body' in the request payload.",
      });
    }

    // Notification payload
    const message = {
      notification: {
        title: title,
        body: body,
      },
    };

    const usersRef = db.collection('users');
    const snapshot = await usersRef.where('role', '==', 'TENANT').get();

    if (snapshot.empty) {
      return res.status(404).send('No TENANT users found!');
    }

    snapshot.forEach(doc => {
      const userData = doc.data();
      if (userData.token && Array.isArray(userData.token)) {
        token.push(...userData.token); // Push all tokens for this user
      }
    });

    if (token.length === 0) {
      return res.status(404).send('No tokens found for TENANT users.');
    }

    log("before msg", token)
    // Send notification to all tokens
    const response = await admin.messaging().sendEachForMulticast({
      tokens: token, // Array of FCM tokens
      notification: message.notification,
    })
    log("after msg", token)

    log("Successfully sent message:", response);

    res.status(200).send({
      success: true,
      message: "Broadcast notification sent successfully!",
      response,
    });


  } catch (error) {
    console.error("Error sending broadcast notification to tenant:", error);

    // Send error response
    res.status(500).send({
      success: false,
      error: error.message,
    });
  }
});


// // this code is used in cmd to fire notifications to only Tenents

/*
$headers = @{
     "Content-Type" = "application/json"
 }
 $body = @{
     "title" = "HELLO TENANT!!! HOWS YOUR DAY"
     "body" = "this is a reminder that you are a tenant incase you need any help contact manager"
 } | ConvertTo-Json -Depth 10

 Invoke-WebRequest -Uri "https://us-central1-propertymanagement-1067d.cloudfunctions.net/broadcasttenant" `
                   -Method POST `
                   -Headers $headers `
                   -Body $body
*/
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------







//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
exports.broadcastmanager = functions.https.onRequest(async (req, res) => {
  try {
    const token = [];  // Initialize as an array for storing tokens


    // Check if the body contains required fields
    const { title, body } = req.body;
    if (!title || !body) {
      return res.status(400).send({
        success: false,
        message: "Missing 'title' or 'body' in the request payload.",
      });
    }

    // Notification payload
    const message = {
      notification: {
        title: title,
        body: body,
      },
    };

    const usersRef = db.collection('users');
    const snapshot = await usersRef.where('role', '==', 'MANAGER').get();

    if (snapshot.empty) {
      return res.status(404).send('No MANAGER users found!');
    }

    snapshot.forEach(doc => {
      const userData = doc.data();
      if (userData.token && Array.isArray(userData.token)) {
        token.push(...userData.token); // Push all tokens for this user
      }
    });

    if (token.length === 0) {
      return res.status(404).send('No tokens found for MANAGER users.');
    }

    log("before msg", token)
    // Send notification to all tokens
    const response = await admin.messaging().sendEachForMulticast({
      tokens: token, // Array of FCM tokens
      notification: message.notification,
    })
    log("after msg", token)

    log("Successfully sent message:", response);

    res.status(200).send({
      success: true,
      message: "Broadcast notification sent successfully!",
      response,
    });


  } catch (error) {
    console.error("Error sending broadcast notification to manager:", error);

    // Send error response
    res.status(500).send({
      success: false,
      error: error.message,
    });
  }
});


// // this code is used in cmd to fire notifications to only MANAGERS
/*
$headers = @{
     "Content-Type" = "application/json"
 }
 $body = @{
     "title" = "HELLO MANAGER!!! HOWS YOUR DAY"
     "body" = "this is a reminder that you are a manager and you need to solve problems"
 } | ConvertTo-Json -Depth 10

 Invoke-WebRequest -Uri "https://us-central1-propertymanagement-1067d.cloudfunctions.net/broadcastmanager" `
                   -Method POST `
                   -Headers $headers `
                   -Body $body
*/
//----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------












exports.getinprogressmaintenancereq = functions.firestore.document("maintenance_requests/{docId}").onUpdate(async (snapshot, context) => {
  try {
    const token = [];
    const beforeData = snapshot.before.data();
    const afterData = snapshot.after.data();



    // Check if the status changed to "inprogress"
    if (beforeData.status !== 'In Progress' && afterData.status === 'In Progress') {
      const tenantId = afterData.tenantId; // Assuming tenantId is in the document

      log('Maintenance request is now in progress:', snapshot.after.id);
      log('Tenant ID:', tenantId);

      const userDocRef = admin.firestore().collection('users').doc(tenantId);
      const userDoc = await userDocRef.get();
      if (userDoc.exists) {
        const userdata = userDoc.data();

        if (userdata.token) {
          token.push(...userdata.token);
        } 
        if (token.length === 0) {
          console.error('No valid tokens to send messages to.');
          return null;
        }
        log(`your tokens are ${token}`)

        const message = {
          notification: {
            title: 'Request is in progress',
            body: `we are looking after your request of ${userdata.issueCategory} - ${userdata.issueDescription}`,
          },
        };
        

        const maintenancePromises = token.map((tok) =>
          admin.messaging().send({
            token: tok,
            notification: message.notification,
          },
            log('send maintenance token->', tok)
          ));

        Promise.all(maintenancePromises)
          .then((responses) => {
            log('Successfully sent messages:', responses);
          })
          .catch((error) => {
            console.error('Error sending messages:', error);
          });

      } else {
        log('user does not exist');
      }
    }

    return null;

  } catch (error) {
    log(`error has occured ${error.message}`)
  }
});
