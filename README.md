# Popular Movies, Stage 1
Note that you have to put your API_KEY in the gradle.properties.

In the global gradle.properties you add the api key like this:

```
     MyApiKey = "your_api_key_goes_here"
```

then, you reference it from build.gradle like this:

```
buildTypes.each {

    it.buildConfigField 'String', 'API_KEY', MyApiKey
    
}
```

and finally you access it from the code by using:

```
BuildConfig.API_KEY
```
