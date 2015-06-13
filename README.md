# BTCe Client for Android
This is an Android-Studio project. Most of the BTCe-API functionality is in place.
Only the screen for typing in your secret-api-key is missing. The key is hardcoded in the API-Class.

The BTCE.java is not my work. I found it in another Github-Project. It provides some methods for the use of the BTCe api. Because its implementaition is fully single threaded, I made the facade-class AsyncBtcApi.java which uses the external BTCE.java and executes its methods asynchronous and failsafe.
