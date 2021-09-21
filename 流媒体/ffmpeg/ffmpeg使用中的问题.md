# Stream #0:0 -> #0:0 (h264 (native) -> h264 (libx264))

Let me clarify what a stream mapping line such as the one below, indicates.

```
Stream #0:0 -> #0:0 (h264 (native) -> h264 (libx264))
```

`Stream #0:0` indicates that the input stream is from the first file #**0**:0 and is the first stream in that file #0:**0**

`-> #0:0` indicates that the input stream is sent to the first stream of the first output file.

`h264 (native)` means that the detected codec of the input stream is H.264 and that ffmpeg will use its native (builtin) decoder to decode the stream.

`h264 (libx264)` means that the codec of the output stream will be H.264 and that ffmpeg will use the `libx264` encoder to generate this stream.

"h264(constrained)" usually seen in the input file dump e.g.

```
Stream #0:0: Video: h264 (Constrained Baseline), yuv420p(progressive)...
```

indicates that the input stream codec is H.264 and the stream [profile](https://en.wikipedia.org/wiki/H.264/MPEG-4_AVC#Profiles) is Constrained Baseline. A profile defines the kind of methods that an encoder can use to generate streams and also a set of methods that a decoder is expected to perform, in order to decode the stream.

With your ffmpeg command, libx264 is re-encoding your video stream with CRF 23 and profile High. If the output looks fine to you, there's nothing to do further.

To experiment, you can use

```
ffmpeg.exe -i [RecordedVideoFileName.mkv] -crf X [OutputVideoFileName.mkv]
```

where X can go from 0 to 51. 18 to 28 is a good range for good quality and file size. Lower is better quality and larger size.

# [What is the different of h264 and libx264](https://superuser.com/questions/1587136/what-is-the-different-of-h264-and-libx264)

There is no encoder named h264. Using `-codec:v h264`/`-c:v h264`/`-vcodec h264` is just an alias that points to which H.264 encoder is the default.

The default encoder depends on how your `ffmpeg` was configured, but for most users it will be libx264.

It is recommended to use the name of the specific H.264 encoder you want. This will avoid ambiguity, so you can be sure which encoder you're using. Especially if you are using the same command on different computers or different `ffmpeg` versions as the default H.264 encoder may be different. For example, use `-c:v libx264` instead of `-c:v h264`.

You can see which encoder is the default with `ffmpeg -h encoder=h264`. This will list all available H.264 encoders supported by your `ffmpeg`. The default encoder is listed first.

Note that there is a specific decoder named h264. It is the built-in FFmpeg H.264 decoder. FFmpeg does not have a built-in H.264 encoder: it uses external libraries instead, such as libx264.

====================================================================================================

As many people have pointed out, H.264 is the video encoding standard published by the Motion Pictures Expert Group (MPEG) and the International Telecommunication Union (ITU).

On the other hand, x264 is a video encoder, a particular implementation of H.264 which compresses video files. It’s an open source project maintained by the [VideoLAN](https://www.videolan.org/developers/x264.html) organization. x264 appears in a variety of products and software.

I’ll add an analogy if it helps.

MP3 is an audio encoding standard published by MPEG. LAME is an audio encoder, an implementation of MP3. It’s an open source project maintained by [SourceForge](http://lame.sourceforge.net/). LAME appears in a variety of products and software.

There are other MP3 encoders and other H.264 encoders. Most of them are proprietary, closed source implementations from private companies like Fraunhofer and DivX. However, LAME and x264 are probably more widely used than any other implementations because they are both very good and they are available for free.

# What do my video output live stream details from ffmpeg mean?

https://api.video/blog/video-trends/what-do-my-video-output-live-stream-details-from-ffmpeg-mean

