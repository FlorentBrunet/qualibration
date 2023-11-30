# Qualibration

It's been years since I felt the need for a handy GUI/tool to calibrate cameras.

For now, this tool is very basic: select calibration images, enter target parameters, auto-detect corners, click on
the "Calibrate" button and that's it!

Technically, it's "just" a wrapper around a subset of the calibration methods provided
by [BoofCV](https://boofcv.org/index.php?title=Main_Page).
It's intended to grow with new features: region-of-interest related stuff, handling of very small calibration targets,
nice visualizations, tools around distortions, ...

![Screenshot of the main screen](/doc/main_screen.png)
![Screenshot of the undistortion margins](/doc/undist_margins.png)

## References

- [BoofCV](https://boofcv.org/index.php?title=Main_Page)
- [Bouguet's Camera Calibration Toolbox](http://robots.stanford.edu/cs223b04/JeanYvesCalib/)
- [Zhang's calibration method paper](https://www.microsoft.com/en-us/research/project/a-flexible-new-technique-for-camera-calibration-2/?from=https://research.microsoft.com/en-us/um/people/zhang/Calib/&type=exact)

## Prerequisites

- Java 17

## Compilation

```shell
mvn clean package
```

## Run

```shell
java -jar target/qualibration-X.Y.Z.jar
```

## Disclaimer

This code is not an example of clean code. It compiles, it works, it is readable, but it is definitely not an example to
follow in terms of JavaFX development, nor is it an example of UI/UX design :) And by the way, where are the tests?!

## License

[MIT](LICENSE)
