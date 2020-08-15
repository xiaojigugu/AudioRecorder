# AudioRecorder

[![](https://jitpack.io/v/xiaojigugu/AudioRecorder.svg)](https://jitpack.io/#xiaojigugu/AudioRecorder)


1. This is an audio recording lib for Android which is based on [lame]("https://lame.sourceforge.io/").
2. AudioRecord is used to gain pcm data.
3. lame lib is used to encode pcm file into mp3 file.


How to use:

```groovy
//Step 1. Add the JitPack repository to your build file
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

//Step 2. Add the dependency
dependencies {
	        implementation 'com.github.xiaojigugu:AudioRecorder:v1.0'
	}
```

```xml
<!-- Step 3. Add permission -->
 <uses-permission android:name="android.permission.RECORD_AUDIO" />
```

```groovy
// step 4. Add abiFilters in build.gradle(app)
        ndk{
            abiFilters "armeabi-v7a"//,"arm64-v8a","x86","x86_64"
        }
```

```java
// Step 5. get start
    AudioRecorderManager.record(context);

or:
//define the mp3 file path
    String outputpath=context.getFilesDir() + File.separator + "audio_record";
//define max record time in ms
    int max_time_ms=6_000;
    AudioRecorderManager.record(context,inputpath,max_time_ms);


// Step 6. get result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = AudioRecorderManager.obtainResult(requestCode, resultCode, data);
        //Path will be like this:/data/user/0/com.junt.audiorecorder/files/audio_record/record.mp3
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
    }

```

Thanks for:  
[ZlwAudioRecorder]("https://github.com/zhaolewei/ZlwAudioRecorder")

