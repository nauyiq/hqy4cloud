var template ={
	content_body_init:  "<h1 id='h1_title'>Netty-Websocket 探测 :</h1>" +
						"<br/>" +
						'<div id="console" class="well"></div>' +
						'<form class="well form-inline" onsubmit="return false;">' +
							'<input id="msg" class="input-xlarge" type="text" placeholder="Type something..." />' +
							'<button type="button" onClick="template.sendMessage(\'clientPing\')" class="btn" id="send1">PUSH ‘clientPing’</button>' +
							'<button type="button" onClick="template.sendMessage(\'clientPoll\')" class="btn" id="send2">PUSH ‘clientPoll’</button>' +
							'<button type="button" onClick="sendDisconnect()" class="btn">Disconnect</button>' +
						'</form>',
	socket:null,
	userName : 'user' + Math.floor((Math.random()*1000)+1),
	domain_url : "https://test3.lovense.com",
	ch : '',
	ntoken : 'NjqOQ9w_kJP6x0mDH2fW0XxAdggbFUoHzTi-1Gw-2nZrwjyatjdLKytrgU7DeKF-7EmKWJT8X2hRb1FQgPJL3szd1NKKzinc9LcgFlFu1YKE57iSIdgGMy4qAKYBDvNdsShMiO53sFNYDfWeTktRTHMx8ZESm4Rwib6LKoGEjW4',
	aurl: 'https://test2.lovense.com/monitor-eye/appHealth/hb',
	path:'',
	lastFailTimeStr : "", // 最后一次断开时间
	lastSuccTimeStr: "", // 最后一次连接时间
	heart_interval:  5 * 1000,
	refresh_interval: 1 * 60 * 1000,
	onConnect:false,
	locationUrl:'',
	receive_count :0,
	disconn_count:0,
	socket_obj : {
		init: function(path){
			template.path = path;
			template.socket = io.connect(template.domain_url + '?ntoken=' + template.ntoken + "&ch="+template.ch , {path:path});
			template.socket.on('connect', function() {
				template.output('<span class="connect-msg">Client has connected to the server!</span>');
				template.lastSuccTimeStr = new Date().toString();
				template.onConnect = true;
			});

			template.socket.on('serverResponse', function(data) {
				template.output('<span class="username-msg">' + data.result + ':</span> ' + data.code + ", "  + data.message + ", "  + data.name);
				template.receive_count = template.receive_count+1;
				if(template.receive_count > ((template.refresh_interval / template.heart_interval ) + 10)){
					console.log("收发消息的次数达到了要上报的时限内的心跳次数,需要上报并刷新页面...");
					//如果收收发消息的次数达到了要上报的时限内的心跳次数，加20 是为了应对手动发送的场合....
					window.clearInterval(task1);
					template.socket.disconnect()
					template.report(path);//上报
					window.location.reload();//刷新....
				}
			});

			template.socket.on('ServerPushDemoObject', function(data) {
				template.output('<span class="server-msg">' + data.name + ':</span> ' + data.message +",IP="+data.serverIp  );
			});
			
			template.socket.on('MsgRecReceipt', function(data) {
				template.output('<span class="server-msg">' + data.receiveId + ':</span> ' + data.processed +",Time="+data.serverTime  );
			});

			template.socket.on('disconnect', function() {
				template.output('<span class="disconnect-msg">The client has disconnected!</span>');
				template.lastFailTimeStr =  new Date().toString();
				template.sleep(1);
				template.disconn_count = template.disconn_count+1;
			});

			let task1 = window.setInterval(function(){
				template.sendMessage('clientPoll');//上报
			},1000*5);


			window.setInterval(function(){
				template.socket.disconnect()
				window.clearInterval(task1);
				template.report(path);//上报
				window.location.reload();//刷新....
			},1 * 60 * 1000);


			//
			//console.log('write content_body_init to html.body 5>>');
//			$.ready(function(){
//				console.log('ready!');
//				$('body').html(template.content_body_init);
//			});
			window.document.write(template.content_body_init);
//			window.document.innerHTML  = template.content_body_init;
		}
	},
	// 上报数据
	report :function (){
		var okNow = false;
		if(template.socket != null){
			okNow = template.onConnect
		}
		if(template.disconn_count  >0){
			template.disconn_count = template.disconn_count-1;
		}
		var p = "?detectOn="+template.domain_url+template.path+"&env=test&type=wss&faildTimes="
			+ template.disconn_count+"&okNow="+okNow
			+"&lastFailTimeStr="+template.lastFailTimeStr +"&lastSuccTimeStr="+template.lastSuccTimeStr
			+"&succsTimes="+template.receive_count+"&t="+Math.random();
		console.log("aurl ->>  " + template.aurl);
		template.get(template.aurl + p, function(ret) {
			console.log(ret);
		});
		template.sleep(10);
	},

	get: function(url, fn) {
		//创建XMLHttpRequest对象
		var xhr = new XMLHttpRequest();
		//true表示异步
		xhr.open('GET', url, true);
		xhr.onreadystatechange = function() {
			// readyState == 4说明请求已完成
			if(xhr.readyState == 4 && xhr.status == 200 || xhr.status == 304) {
				//responseText：从服务器获得数据
				fn.call(this, xhr.responseText);
			}
		};
		xhr.send();
	},
	post: function(url, data, fn,reqContentType) { //datat应为'a=a1&b=b1'这种字符串格式
		var xhr = new XMLHttpRequest();
		xhr.open("POST", url, true);
		// 添加http头，发送信息至服务器时内容编码类型
		if(typeof(reqContentType) == 'undefined' ){
			//reqContentType = 'application/json;charset=utf-8';
			reqContentType = 'application/x-www-form-urlencoded';
		}
		xhr.setRequestHeader("Content-Type", reqContentType);
		xhr.onreadystatechange = function() {
			if(xhr.readyState == 4 && (xhr.status == 200 || xhr.status == 304)) {
				fn.call(this, xhr.responseText);
			}
		};
		xhr.send(data);
	},


	sleep :function(d){
		for(var t = Date.now();Date.now() - t <= (d*1000););
	},

	sendDisconnect: function () {
		template.socket.disconnect();
    },

    sendMessage: function (eventname) {
		//debugger;
		if(eventname == 'clientPing'){
			var message = $('#msg').val();
            $('#msg').val('');
			var jsonObject = {userName:template.userName,message:message,clientTime: new Date().getTime()};
			template.socket.emit('clientPing', jsonObject);
		}else if(eventname == 'clientPoll'){
			//向服务器发送查询请求
			var message = $('#msg').val();
            $('#msg').val('');
            var jsonObject = {userName: template.userName,
            		//requestId:'UUID'+template.guid(),
            		//requestReceipt:true,
            		message: message,clientTime: new Date().getTime()};//jsonObject 是附带的参数
            template.socket.emit('clientPoll', jsonObject);
		}
	},
	output: function (message) {
        var currentTime = "<span class='time'>" +  moment().format('HH:mm:ss') + "</span>";
        var element = $("<div>" + currentTime + " " + message + "</div>");
		$('#console').prepend(element);
	},
	guid:function() {
	    var s = [];
	    var hexDigits = "0123456789abcdef";
	    for (var i = 0; i < 36; i++) {
	        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
	    }
	    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
	    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
	    s[8] = s[13] = s[18] = s[23] = "-";
	 
	    var uuid = s.join("");
	    return uuid;
	}
}

