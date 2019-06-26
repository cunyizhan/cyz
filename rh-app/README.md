#### 一、小程序wx.request接口
```javascript

wx.request({
	url: 'http://localhost:9090/weappservice/api/v1/user/get/{id}',
	
    data: {appId: 'wx4a29790198c4777a', apiName: 'GET_USER'},
    
    method: 'GET',
    success: function(res){
		console.log(res.data);
    },
    fail: function(res){
    
    },
    complete: function(res){
    
    }
});
```

#### 二、测试小程序wx.uploadFile接口,单张上传
```javascript

wx.uploadFile({
    url: 'http://localhost:9090/weappservice/api/v1/upload/image',
    
    //文件临时路径
    filePath: tempFilePath,
    
    name: 'file',
    
    header: {},
    
    formData: {appId: 'wx4a29790198c4777a', apiName: 'UPLOAD_IMAGE'},
    
    success: function(res){
      console.log(res.data)
    },
    
    fail: function(res){
    
    },
    
    complete: function(res){
    
    }
})

#### 三、测试小程序websocket相关接口
```javascript

//发起websocket连接
wx.connectSocket({
	url: 'ws://localhost:9090/weappservice/websocket?name=zk',
  	data: {
  		'name1': 'zhaokui'
  	}
}),

//监听打开事件
wx.onSocketOpen(function(res) {
  	console.log('WebSocket连接已打开！');
}),

//接收消息，接收的消息是json字符串，需要JSON.parse转成JSON对象
wx.onSocketMessage(function(res){
	var data = JSON.parse(res.data);
	console.log(data);
}),

//发送消息,消息对象属性key(user和content)不能自定义。
wx.sendSocketMessage({
    data: JSON.stringify({
      user: 'zhaokui',
      content: 'Hi, My name is zk'
    }),
    success: function(res){
    	console.log('消息发送成功！')
    }
})