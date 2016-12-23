# AutoUpdate

android自动更新

android studio导入方式

        compile 'cn.turbo.autoupdate:update:0.4.0'
        
使用方法

      AutoUpdateUtils.update(context, true,versionCode, updateMessage, updateUrl);
注：Demo里使用里leanCloud保存数据
   
        