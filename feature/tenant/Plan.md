### **Tenant Module - Features Overview**
1. **User Authentication**
    - Allow tenants to authenticate using their phone number.
    - Implement role-based access control to ensure tenants are assigned only tenant-specific permissions.

2. **Maintenance Request Management**
    - Tenants can submit maintenance requests (issue description, category, photos/videos).
    - Track request status (pending, inProgress, completed).

3. **Notifications**
    - Notify tenants when a maintenance request is assigned or updated.

4. **Tenant Feedback**
    - Allow tenants to rate the work after completion.

### **Firebase Collections for Tenant Module**

1. **User Collection (for tenants)**:
    - Stores tenant-specific data such as phone number, name, and associated properties.
    - Authentication is handled via Firebase Authentication.
    - Collection: `users`
   ```json
   {
     "id": "auto-generated",        // Unique ID
     "name": "string",              // Tenant's full name
     "phone": "string",             // Tenant's phone number
     "role": "tenant",              // Role-based control (tenant)
     "associatedProperties": ["propertyId1"],  // List of associated property IDs
     "createdAt": "timestamp",      // Creation time
     "updatedAt": "timestamp"       // Last update time
   }
   ```

2. **Property Collection (for tenant association)**:
    - Stores property details and the tenant associated with it.
    - Collection: `properties`
   ```json
   {
     "id": "auto-generated",         // Unique Property ID
     "address": {
       "street": "string",
       "city": "string",
       "state": "string",
       "zipCode": "string"
     },
     "ownerId": "string",            // Landlord ID (reference to `users` collection)
     "currentTenantId": "string",    // Tenant ID (reference to `users` collection)
     "maintenanceRequests": ["requestId1", "requestId2"],  // List of maintenance request IDs
     "createdAt": "timestamp",       // Property creation time
     "updatedAt": "timestamp"        // Last update time
   }
   ```

3. **Maintenance Request Collection (tenant’s maintenance requests)**:
    - Tenants create maintenance requests related to their property.
    - Collection: `maintenanceRequests`
   ```json
   {
     "id": "auto-generated",         // Unique request ID
     "propertyId": "string",         // Property ID (reference to `properties`)
     "tenantId": "string",           // Tenant ID (reference to `users`)
     "assignedStaffId": "string",    // Agency staff assigned to the task
     "workerDetails": {
       "name": "string",
       "phone": "string",
       "trade": "string"             // e.g., Plumber, Electrician, etc.
     },
     "issueDescription": "string",   // Issue description
     "status": "string",             // "pending", "inProgress", "completed"
     "priority": "string",           // "low", "medium", "high"
     "createdAt": "timestamp",       // Request creation time
     "updatedAt": "timestamp",       // Last update time
     "photos": ["photoUrl1", "photoUrl2"],  // Array of photo URLs
     "videos": ["videoUrl1"]        // Array of video URLs
   }
   ```

4. **Notifications Collection (for tenant notifications)**:
    - Keeps track of notifications sent to the tenant.
    - Collection: `notifications`
   ```json
   {
     "id": "auto-generated",         // Unique notification ID
     "userId": "string",             // Reference to `users.id`
     "message": "string",            // Notification message content
     "type": "string",               // "maintenanceUpdate", "taskAssigned", etc.
     "read": "boolean",              // Mark as read/unread
     "createdAt": "timestamp"        // Notification creation time
   }
   ```

5. **Feedback Collection (tenant feedback after task completion)**:
    - Collect feedback from tenants after maintenance work is completed.
    - Collection: `feedback`
   ```json
   {
     "id": "auto-generated",        // Unique feedback ID
     "requestId": "string",         // Reference to `maintenanceRequests.id`
     "tenantId": "string",          // Reference to `users.id`
     "rating": "number",            // Rating (1-5)
     "comments": "string",          // Additional feedback comments
     "createdAt": "timestamp"       // Feedback submission time
   }
   ```

---

### **Features Breakdown**

#### 1. **User Authentication**
- **Phone-based Authentication** using Firebase Authentication:
    - Implement Firebase Authentication in the app to authenticate tenants using their phone numbers.
    - Use Firebase's `PhoneAuthProvider` to enable phone number-based login and registration.

#### 2. **Maintenance Request Management**
- **Create Maintenance Requests**:
    - **UI**: Create a form where tenants can describe the issue, upload photos/videos, and select issue category.
    - **Backend**: On submission, create a new document in `maintenanceRequests` collection with the issue details.
    - **Firebase Functionality**: Use Firebase Firestore to store request details and Firebase Storage for uploading images/videos.

- **Maintenance Request Status Updates**:
    - Tenants can view the status of their requests (e.g., pending, in progress, completed).
    - **Backend**: Update the status in Firestore when the agency staff updates it.

#### 3. **Notifications**
- **Push Notifications via Firebase Cloud Messaging (FCM)**:
    - Notify tenants when a task is assigned to a worker, and when the status of a maintenance request changes (e.g., from pending to in progress).
    - **Implementation**: Implement Firebase Cloud Messaging to send notifications to tenants on request status updates.

#### 4. **Tenant Feedback**
- **Post-Maintenance Feedback**:
    - Once the maintenance request is completed, a feedback form appears for the tenant to rate the work and leave comments.
    - **Backend**: Store the feedback in the `feedback` collection with a reference to the `maintenanceRequests.id`.

---

### **Tech Stack for MVP**

1. **Frontend (Android in Kotlin)**:
    - **Authentication**: Firebase Authentication (Phone-based login).
    - **Firestore**: Firebase Firestore for storing and fetching data (maintenance requests, user data, etc.).
    - **Storage**: Firebase Storage for uploading images/videos for maintenance requests.
    - **Notifications**: Firebase Cloud Messaging (FCM) for sending push notifications.

2. **Backend**:
    - **Firebase Firestore**: Stores all the data (Users, Properties, Maintenance Requests, etc.).
    - **Firebase Functions** (optional): For additional server-side logic, e.g., send notifications, update statuses, etc.

---

### **Next Steps**
1. **Firebase Authentication Integration** for tenant login.
2. **Maintenance Request UI**: Build forms for creating and tracking maintenance requests.
3. **Notification System**: Set up Firebase Cloud Messaging for notifying tenants about status updates.
4. **Feedback Form**: Design a feedback form that tenants can submit after the completion of a maintenance task.

Once these core functionalities are set up for the **tenant module**, the app will have a foundational system for managing tenants, their properties, maintenance requests, and notifications.