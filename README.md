# 1. 项目简介
可爱的Q版小部件。

# 2. 使用方法
在项目res/layout/布局文件中加入以下代码：
```
<com.qiwonn.cutewidget.CuteLabelSwitch 
	xmlns:app="http://schemas.android.com/apk/res-auto"
	android:id="@+id/cute_label_button" 
	android:layout_width="128dp" 
	android:layout_height="64dp" 
	android:layout_gravity="center_horizontal"
	android:layout_margin="2dp" 
	android:textSize="16sp" 
	android:clickable="true"
	app:on="false" 
	app:textOn="开" 
	app:textOff="关" 
	app:colorOn="#2bc48b" 
	app:colorOff="#000000" 
	app:colorTextOn="#000000" 
	app:colorTextOff="#FFFFFF" 
	app:colorThumb="#FFFFFF" 
	app:colorBorder="#00c4a6"/>
```

# 3. 添加依赖
在项目的app目录build.gradle文件添加依赖：
```
dependencies {
    // ...
    implementation 'com.github.qiwonn:CuteQWidget:1.0.0'
}
```

# 4. 添加代码
```
CuteLabelSwitch cuteButton = findViewById(R.id.cute_label_button);
cuteButton.setClickable(true);
cuteButton.setOnToggledListener(new CuteLabelSwitch.OnToggledListener(){
	@Override
	public void onSwitched(View view, boolean isOn)
	{
		// TODO: Implement this method
		if (isOn) {
			// 开
		} else {
			// 关
		}
	}
});
```
