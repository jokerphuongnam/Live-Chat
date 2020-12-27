//connect socket, send and receive messages from server
class SocketHelper {
	constructor(id, success, error) {
		this.stomp = Stomp.over(new SockJS('chat'))
		this.stomp.connect({}, (frame) => {
			this.stomp.subscribe(encodeURI(`/topic/message/${id}`), (msg) => {
				success(JSON.parse(JSON.parse(msg.body)))
			})
		}, (e) => {
			error(e)
		})
	}

	/*disconnectServer = () => {
		if (this.stomp != null) {
			this.stomp.disconnect()
		}
	}

	sendData = (data, idRoom) => {
		console.log(encodeURI(`/app/${mode}/${idRoom}`))
		this.stomp.send(encodeURI(`/app/${mode}/${idRoom}`), {}, JSON.stringify(data))
	}*/
}