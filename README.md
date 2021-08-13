# BharatSave

To test using the locally-running [server](https://github.com/Sanskar0520/bharatSaveBackend):
1. Get the server running locally.
2. In [RetrofitModule.kt](app/src/main/java/com/bharatsave/goldapp/di/RetrofitModule.kt), change the `BASE_URL` field from  

<div align="center">

`http://192.168.1.6:8080/`</div>
<div align="center">

to</div>
<div align="center">

`http://<YOUR-PC-IP-ADDRESS>:8080/`</div>

3. Ensure that your pc and the Android device are on the same wifi network.
