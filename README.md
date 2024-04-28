# Park GT
Park GT is a parking lot search service that utilizes crowd sourced geolocation information from its users to estimate the availability of Georgia Tech parking lots.

## How to Run
Open the project in IntelliJ. Configure the datasource and redis parameters according to your setup. If using our database, replace the password with the current one (Due to KMS rotates password every week, please consult us for a new password if you want to test our application). Build the application using the App configuration. Run the app to establish connection to the database. Run web application by installing the dependencies and start react web application using `set HTTPS=true&&npm start` (For specific instruction for launching react web application, please refer to this [README](./gt_parking-ui/README.md). Open the application at localhost on the specified port Alternatively, connect via a mobile device by connecting to the device you are running the application on.

---
The Swagger service can be accessed at https://localhost:8443/swagger-ui.html# while running the application.

## Infrastructure
<img width="848" alt="Screenshot 2024-04-28 at 11 31 40 AM" src="https://github.com/HarrisonPW/GTParking/assets/32474200/b0c52507-54ad-46e2-b39a-f80acffdefa5">

## Technical Details
### Synchronization of data between computer and phone
The requirement from HTTPS protocol force every connection established from our application to be HTTPS connection as well. Therefore, all our database API query need to be through HTTPS connection. For example, our application would be able to query the database using the url `https://localhost:8082/parking-lot` but not using the url `http://localhost:8082/parking-lot`. However, HTTPS requires a CA certificate which we would not be able to get since it cost money and we don't own the domain of localhost. One solution that we tried is using a self-signed certificate. While it is easy to installed the self-signed certificate on the computer, it is really difficult to install it on the phone and for phone's browser to recognize this self-signed certificate. We have tried many methods but the phone keeps returning `invalid certificate` error. Therefore, one approach that we come up with to work around this issue is to using a [remote storage API](https://github.com/FrigadeHQ/remote-storage) and for the computer to access and continously fetch from the database. The computer will then cache the fetched data in the remote storage, which the phone will directly access the cached data instead of through HTTPS connection. This way, we achieved database access from the phone while bypassing the limitation of HTTPS. This section of the code can be found in [here](./gt_parking-ui/src/components/Home.js).
