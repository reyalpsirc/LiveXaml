using System;
using WebSocket4Net;
using LiveXAML.iOS;
using System.Threading.Tasks;
using System.Threading;

[assembly: Xamarin.Forms.Dependency (typeof (WebSocketClient))]
namespace LiveXAML.iOS
{
	public class WebSocketClient:IWebSocketClient
	{
		WebSocket websocket;
		Task timer;
		bool connected = false;

		public WebSocketClient ()
		{
		}

		public override void Open (string wslocalhost){
			connected = false;
			websocket = new WebSocket(wslocalhost,"",(WebSocketVersion) (-1));
			websocket.Opened += delegate(object sender, EventArgs e) {
				connected = true;
				if (Opened!=null){
					Opened();
				}
			};
			websocket.MessageReceived += delegate(object sender, MessageReceivedEventArgs e) {
				if (MessageReceived!=null){
					MessageReceived(e.Message);
				}
			};
			websocket.Error += delegate(object sender, SuperSocket.ClientEngine.ErrorEventArgs e) {
				if (!connected && websocket!=null){
					websocket.Close();
					connected = true;
					if (Error!=null){
						Error(e.Exception);
					}	
				}
			};
			websocket.Closed += delegate(object sender, EventArgs e) {
				if (connected){
					if (Closed != null) {
						Closed ();
					}
				}
			};
			timer = new Task (async delegate() {
				await Task.Delay(2000);	
				if (!connected && websocket!=null){
					websocket.Close();
					connected = true;
					if (Error!=null){
						Error(new Exception("Connection failed"));
					}	
				}
			});
			timer.Start ();
			websocket.Open ();
		}

		public override void Send (string message){
			websocket.Send(message);
		}
	}
}

