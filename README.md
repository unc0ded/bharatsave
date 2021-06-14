# Drogon

To test using the locally-running [server](https://github.com/epicadk/drogon-backend):
1. Get the server running according to the instructions [here](https://github.com/epicadk/drogon-backend#running-server-locally)
1. In `app/src/main/java/com/dev/in/drogon/di/RetrofitModule.kt`, change the `BASE_URL` field from  

<div align="center">

`http://192.168.1.6:8080/`</div>
<div align="center">

to</div>
<div align="center">

`http://<YOUR-PC-IP-ADDRESS>:8080/`</div>

3. Ensure that your pc and the Android device are on the same wifi network.
