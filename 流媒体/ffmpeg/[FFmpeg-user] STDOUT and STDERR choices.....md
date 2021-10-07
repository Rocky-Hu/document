```
-A normally running ffmpeg task seems to send all it's output
(even when there are no errors) to STDERR even with no errors.

This depends on what you mean with "output":
ffmpeg sends all diagnostic messages (the "console output")
to stderr because its actual output (the media stream) can go
to stdout and mixing the diagnostic messages with the media
stream would brake the output.

I suppose I would base my definition within the constraints of the console itself. Since there are two forms of output (STDOUT and STDERR) that suggests Error Output and Non-Error Output. Therefore let's call "normal" anything that does not result in an error.

Non-Error (normal operation) output would go to STDOUT.
Error (non-normal) output would go to STDERR.

As for the media stream possibly going to STDOUT (sometimes), isn't this already a problem because some diagnostic info currently goes to STDOUT already? So the issue of 'mixing' diag and media already exists?
```

