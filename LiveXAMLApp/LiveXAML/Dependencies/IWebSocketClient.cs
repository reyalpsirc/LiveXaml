using System;
using System.Threading.Tasks;

namespace LiveXAML
{
	public abstract class IWebSocketClient
	{
		public Action Opened {
			get;
			set;
		}

		public Action<Exception> Error {
			get;
			set;
		}

		public Action<string> MessageReceived {
			get;
			set;
		}

		public abstract void Open (string wslocalhost);

		public abstract void Send (string message);
	}
}

