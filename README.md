# RestAPIExample
<br>
Rest API Example - Contact : Creation, Modification, Listing and Deletion.<br>
<br>
You can download the jar file directly from above and to execute it use the following command:

```
java -jar RestAPIExample-1.0-SNAPSHOT.jar
```
<br>

###After starting above jar file. <br>
<br>
* To Create a Contact please use this cURL command :

```
curl -L -X POST 'http://localhost:8080/api/create_contact' -H 'Content-Type: application/json' --data-raw '{
    "phoneNumber" : 123456789,
    "userName" : "User Name",
    "phNumberType" : "HOME"
}'
```
<br>
* To Modify a Contact please use this cURL command :

```
curl -L -X PUT 'http://localhost:8080/api/123456789' -H 'Content-Type: application/json' --data-raw '{
    "phoneNumber" : 123456789,
    "userName" : "User Name Modified",
    "phNumberType" : "WORK"
}'
```
<br>
* To View all Contacts please use this cURL command :

```
curl -L -X GET 'http://localhost:8080/api/all_contacts'
```
<br>
* To View single Contact please use this cURL command :

```
curl -L -X GET 'http://localhost:8080/api/123456789'
```
<br>
* To Delete a Contact please use this cURL command :

```
curl -L -X DELETE 'http://localhost:8080/api/123456789'
```
<br>
<br>