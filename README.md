[![Build Status](https://travis-ci.org/sameersingh/scalaplot.svg?branch=master)](https://travis-ci.org/sameersingh/scalaplot)

scalaplot
=========

This is a library for quick and easy plotting of simple plots (such as XY line plots, scatter plots) and supports outputs using different engines (currently Gnuplot and JFreeGraph).

**Note:** The project is still in *beta*. If you just need a clean way to interface Java with gnuplot, see [gnujavaplot](http://gnujavaplot.sourceforge.net/JavaPlot/About.html).

## Requirements

- maven
- *for gnuplot*: [gnuplot 4.6](http://www.gnuplot.info/) with pdf support (on the mac+[homebrew](http://mxcl.github.com/homebrew/), `brew install pdflib-lite gnuplot`)

## Installation

### Maven dependency

The easiest (and recommended) way to use scalaplot is as a maven dependency. Insert the following in your `pom` file:

```xml
<dependencies>
  ...
  <dependency>
    <groupId>org.sameersingh.scalaplot</groupId>
    <artifactId>scalaplot</artifactId>
    <version>0.0.4</version>
  </dependency>
  ...
</dependencies>
```
### SBT dependency
Or to use scalaplot with SBT, add the following dependency to your `build.sbt` file:

```
libraryDependencies += "org.sameersingh.scalaplot" % "scalaplot" % "0.0.4"
```

## Creating Charts

Currently, the library supports line and point (scatter) charts. Let's start with a simple, complete example:

```scala
import org.sameersingh.scalaplot.Implicits._

val x = 0.0 until 2.0 * math.Pi by 0.1
output(PNG("docs/img/", "test"), xyChart(x ->(math.sin(_), math.cos(_))))
```

which produces

![Example scalaplot](https://github.com/sameersingh/scalaplot/raw/master/docs/img/test.png)

while

```scala
output(ASCII, xyChart(x ->(math.sin(_), math.cos(_))))
```

produces

```
    1 BBBB------+--AAAAAA-+---------+--------+---------+---------BBB------++
      +   BB    +AA      AAA        +        +         +       BB+         +
  0.8 ++    BB AA           AA                               BB           ++
      |      AA               A                            BB              |
  0.6 ++    A  BB              A                          B               ++
      |    A     B              AA                       B                 |
  0.4 ++  A       B               A                     B                 ++
      |  A         B               A                   B                   |
  0.2 ++A           B               A                 B                   ++
    0 AA             B               A               B                    ++
      |               B               A             B              A       |
 -0.2 ++               B               A           B              A       ++
      |                 B               A        BB              A         |
 -0.4 ++                 BB              A      B               A         ++
      |                    B             A     B               A           |
 -0.6 ++                    B             AA  B              AA           ++
      |                      B              AB              A              |
 -0.8 ++                      BB           B AA           AA              ++
      +         +         +     BBB +    BB  + AA      +AA       +         +
   -1 ++--------+---------+--------BBBBBB----+---AAAAAAA---------+--------++
      0         1         2         3        4         5         6         7
```

As another example to introduce a bit of customization:

```scala
import org.sameersingh.scalaplot.Implicits._

val x = 0.0 until 10.0 by 0.01
val rnd = new scala.util.Random(0)

output(PNG("docs/img/", "scatter"), xyChart(
  x -> Seq(Y(x, style = XYPlotStyle.Lines),
           Y(x.map(_ + rnd.nextDouble - 0.5), style = XYPlotStyle.Dots))))
```

produces

![Example scatter](https://github.com/sameersingh/scalaplot/raw/master/docs/img/scatter.png)

### Output Formats

The library, of course, supports different output formats. Most of these also produce an accompanying Gnuplot source file, allowing archival and further customization if needed. The current list of formats are:

```scala
output(ASCII, xyChart(...)) // returns the string as above
output(SVG, xyChart(...)) // returns the SVG text, which can be embedded in html or saved as a SVG file
output(PDF(dir, name), xyChart(...)) // produces dir/name.gpl as the gnuplot source, and attempts dir/name.pdf
output(PNG(dir, name), xyChart(...)) // produces dir/name.gpl as the gnuplot source, and attempts dir/name.png
output(GUI, xyChart(...)) // opens a window with the plot, which can be modified/exported/resized/etc.
```

Note that scalaplot calls the `gnuplot` command to render the image in `dir/name.EXT`, but in case it fails, do the following:

```shell
$ cd dir/
$ gnuplot name.gpl
```

which will create `name.EXT`, where `EXT` is one of `PDF` or `PNG`.

### XYChart

The `xyChart` function is the main entry point for creating charts. The first argument of plot requires a `XYData` object, that we will describe in the next section. The rest of the arguments customize the aspects of the chart that are not data-specific.

```scala
val d: XYData = ...
xyChart(d)
xyChart(d, "Chart Title!")
xyChart(d, x = Axis(label = "Age"), y = Axis(log = true))
```

Here are the relevant definitions and default parameters that you can override:

```scala
def xyChart(data: XYData, title: String = "",
            x: NumericAxis = new NumericAxis,
            y: NumericAxis = new NumericAxis,
            pointSize: Option[Double] = None,
            legendPosX: LegendPosX.Type = LegendPosX.Right,
            legendPosY: LegendPosY.Type = LegendPosY.Center,
            showLegend: Boolean = false,
            monochrome: Boolean = false,
            size: Option[(Double, Double)] = None): XYChart
def Axis(label: String = "",
         backward: Boolean = false,
         log: Boolean = false,
         range: Option[(Double, Double)] = None): NumericAxis
```

### Data

The data is the first argument of the plot function, and can be specified in many different ways, depending on the format your data is available in. Primarily, `XYData` consists of multiple sequences of `(Double,Double)` pairs, where each sequence forms a single series (line in line plots). Here are some ways of data can be specified.

If you have a single `x` sequence and multiple `y` sequences, you can use:

```scala
// data
val x = (1 until 100).map(_.toDouble)
val y1 = (1 until 100).map(j => math.pow(j, 1))
val y2 = (1 until 100).map(j => math.pow(j, 2))
val y3 = (1 until 100).map(j => math.pow(j, 3))

xyChart(x ->(y1, y2, y3))
xyChart(x ->(math.sin(_), math.cos(_))) // inline definition
xyChart(x -> Seq(Y(y1, "1"), Y(y2, "2"), Y(y3, "3"))) // with labels and other possible customizations
xyChart(x -> Seq(Yf(math.sin, "sin"), Yf(math.cos, color = Color.Blue), Yf(math.tan, lw = 3.0))) // Yf for functions
```

where each series can be fully customized using the following:

```scala
def Y(yp: Seq[Double],
      label: String = "Label",
      style: XYPlotStyle.Type = XYPlotStyle.LinesPoints,
      color: Option[Color.Type] = None,
      ps: Option[Double] = None,
      pt: Option[PointType.Type] = None,
      lw: Option[Double] = None,
      lt: Option[LineType.Type] = None,
      every: Option[Int] = None)
def Yf(f: Double => Double,
       label: String = "Label",
       style: XYPlotStyle.Type = XYPlotStyle.LinesPoints,
       color: Option[Color.Type] = None,
       ps: Option[Double] = None,
       pt: Option[PointType.Type] = None,
       lw: Option[Double] = None,
       lt: Option[LineType.Type] = None,
       every: Option[Int] = None)
```

If you have sequences of `(x,y)` pairs as your data, or if you want to use different `x` for each series:

```scala
xyChart(List(x -> Y(y1), x -> Y(y2)))
xyChart(List(x -> Y(y1, "1"), x -> Y(y2, color = Color.Blue)))

val xy1 = x zip y1
val xy2 = x zip y2
xyChart(List(XY(xy1), XY(xy2)))
xyChart(List(XY(xy1, "1"), XY(xy2, "2")))
```

where the customization is similar to above:

```scala
def XY(points: Seq[(Double, Double)],
       label: String = "Label",
       style: XYPlotStyle.Type = XYPlotStyle.LinesPoints,
       color: Option[Color.Type] = None,
       ps: Option[Double] = None,
       pt: Option[PointType.Type] = None,
       lw: Option[Double] = None,
       lt: Option[LineType.Type] = None,
       every: Option[Int] = None)
```

### Other Implicits

Scalaplot also supports a number of other implicits to make things easier to use.

```scala
val d: XYData = x ->(y1, y2, y3)
val c: XYChart = d // automatic conversion from data to chart

// series
val s1: XYSeries = x -> y1
val s2: XYSeries = x zip y2
val f1 = math.sin(_)
val s1f: XYSeries = x -> f1
val s2f: XYSeries = x -> Yf(math.sin)

// series to data
val d1: XYData = s1
val d2: XYData = Seq(s1, s2)
val d2l: XYData = s1 :: s2 :: List()
```


## Explicit Data Structures

For even further customization of the charts, you will need to dive into the API instead of relying on the above *implicits*.

### XY Line Charts

First step is to get your data into `Seq[Double]`.

```scala
val x = (1 until 100).map(_.toDouble)
val y = x.map(i => i*i)
```

Create a dataset that represents these sequences.

```scala
val series = new MemXYSeries(x, y, "Square")
val data = new XYData(series)
```

You can add more series too.

```scala
data += new MemXYSeries(x, x.map(i => i*i*i), "Cube")
```

Let's create the chart.

```scala
val chart = new XYChart("Powers!", data)
chart.showLegend = true
```

## Rendering Charts

Even though multiple backends are being supported to render the charts, gnuplot is the most actively developed and supported since it allows post plotting customizations (editing the script files), may possible output formats, and ease of use.

### Gnuplot

Generates gnuplot scripts that will need to be run to actually generate the images.

```scala
val plotter = new GnuplotPlotter(chart)
plotter.writeToPdf("dir/", "name")
```
	
The output looks like

![Example gnuplot output](https://github.com/sameersingh/scalaplot/raw/master/docs/img/gnuplot.png)

### JFreegraph

JFreegraph can also be called similarly to produce pdf plots (use `JFGraphPlotter`).
However, it also supports a `gui()` option for when you just want to see the graph.

```scala
val plotter = new JFGraphPlotter(chart)
plotter.gui()
```

produces

![Example jfreegraph output](https://github.com/sameersingh/scalaplot/raw/master/docs/img/jfreegraph.png)
