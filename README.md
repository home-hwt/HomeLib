##自主切换用户中间件接入说明
注：本SDK应用可以正常使用 基于返回第三方SDK openid 游戏可以找回对应的角色，需要游戏服务器做相应的处理。开启切换功能后游戏需要将用户中心，切换帐号等功能做相应处理

###资源文件如下图：
![image](./image/image1.png)

###SDK接入环境配置
1、添加资源文件AlipaySDK.bundle,AlipaySDK.framework,ChangeUser.framwork到游戏项目中进行引用，如下图所示：
![image](./image/image2.png)
2、在工程项目info.plist添加网络权限<br>
```
<key>NSAppTransportSecurity</key>
<dict>
	<key>NSAllowsArbitraryLoads</key>
	<true/>
</dict>
```
###SDK接口说明：(具体调用可参考Demo)
####1、初始化接口：
	
	(void) init:(NSString *)appId appKey:(NSString *)appKey channel:(NSString *)channel baseUrl:(NSString *)baseUrl delegate:(id<SdkDelegate>)delegate;
	
######参数说明：
参数			 	| 		说明			|
------------ 	| ------------- 	|
appId 			| 9133参数appId	  	|
appKey			| 9133参数appKey  	|
channel 		| channel	  		|
baseUrl			| 服务器地址 		 	|
delegate 		| SDK回调代理	  	|

####2、添加用户数据：
	<br>需在进入游戏时调用，确保可以保存和获取到相应的数据 （如同一账号下有多个角色，请将角色ID传入username）<br><br>
######接口定义：<br>
	- (void) record:(NSString *)openId userType:(NSString *)userType serverId:(NSNumber *)serverId userName:(NSString *)userName userId:(NSString *)userId roleLevel:(int)level roleName:(NSString *)roleName loginType:(NSString *)loginType custom:(NSString *)custom;
	<br>
######参数说明:
参数			 	| 		说明			|
------------ 	| ------------- 	|
openId 			| 渠道openId	  		|
userType		| 用户类型		  	|
serverId 		| 服务器Id	  		|
userId			| 用户Id 		 	|
roleLevel 		| 角色等级		  	|
roleName 		| 角色名称	  		|
loginType		| 登录类型（可传""） 	|
custom	 		| 自定义参数（可传""）	|

####3、判断是否开启切换用户功能
######接口定义：
-(BOOL) isShowChangeUser;
<br>返回true是可切换，false为不可切换

####4、显示切换账号
<br>当判断双账号为开启时调用
#######接口定义：
-(void) showChangeUser:(UIViewController *)controller;

####5、支付接口
######接口定义：
-(void) pay:(NSString *)serverId roleName:(NSString *)roleName amount:(NSNumber *)amount controller:(UIViewController *)controller;

######参数说明：
参数			 	| 		说明				|
------------ 	| ------------- 		|
serverId 		| 服务器Id	  			|
roleName		| 角色名称		  		|
amount	 		| 游戏币，支付金额兑换比例	|
controller		| UIViewController 	 	|


##其它配置按照9133接入

<http://www.i9133.com/topic/51f76391e138234b76959cfb.html>
