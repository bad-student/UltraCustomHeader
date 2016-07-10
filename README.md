项目采用android-Ultra-Pull-To-Refresh框架，在该框架下自定义各种headerview
======
   如下图所示，是我做的一个模仿京东下拉刷新头

![Alt Text](https://github.com/bad-student/UltraCustomHeader/blob/master/gif/courierheader.gif)

   刚开始做上图的效果时，卡在那个快递员跑动效果上。知道是个帧动画，一是对Ultra框架理解程度还不够，源码也一直在看，后面郁闷了一两天，就get了。还有就是帧动画原先不动卡在那，后天再UI线程中刷新view才正常起来。
整个过程对我意义还是蛮大的，后续会继续自定义HeaderView,提交到项目中。


   这个效果原理不难，直接看源码就行。还有就是项目中直接有Ultra源码，这样方便修修改改更好的理解框架。
   参考资料中效果也很好，但是我喜欢的是Ultra框架的深度抽象以及支持各种view的下拉刷新（示例中用的是textview）,实现相同效果，有各种方法，思路最重要。


[参考资料](http://blog.csdn.net/nugongahou110/article/details/50000911)


