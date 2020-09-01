# SlideButton

Slide button inspired from grab app.

<img src="sb.gif" height="540" width="300"/>


## Installation

Include the following dependency build.gradle file

```
me.thet.slideButtonView:SlideButtonView:1.0.0
```

## Usage

Add SlideButtonView widget in your view

```
  <me.thet.slidebutton.SlideButtonView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```

## Customization

| No. | XML Attribute | Default Value |
|:---|:---|:---|
| 1 |sb_cursor_icon | default cursor icon |
| 2 |sb_cursor_icon_size | 20dp |
| 3 |sb_progress_icon_size | 24dp |
| 4 |sb_label |-|
| 5 |sb_label_size | 14sp |
| 6 |sb_primary_color |green|
| 7 |sb_primary_color_light | green |
| 8 |sb_color_on_primary |white|


## Adding a drag listener

```
//inside Activity or Fragment
protected void onCreate (Bundle savedInstanceState){
    slideButtonView.setOnDragListener(new SlideButtonView.OnDragListener() {
      @Override
      public void onDragCompleted() {
          //drag is completed
      }
  });
}
```

## MIT License

Copyright (c) 2020 thetpaingtun

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


