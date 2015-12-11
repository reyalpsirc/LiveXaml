using System;

using Xamarin.Forms;
using System.Diagnostics;
using System.Threading.Tasks;
using System.Text;
using System.Net;
using System.Reflection;

namespace LiveXAML
{
	public class TestPage : ContentPage
	{
		IXamlDependency xamlDep;
		bool receivedMessage=false;

		public TestPage ()
		{
			xamlDep = DependencyService.Get<IXamlDependency> ();
			Content=StartLayout();
		}

		private async void ConnectServer(string address, int port){
			try{
				string realAddress=GetWSAddress(address,port);
				var client = DependencyService.Get<IWebSocketClient>(DependencyFetchTarget.NewInstance);	
				client.Opened = delegate() {
					Debug.WriteLine ("Opened");
					if (!receivedMessage){
						receivedMessage=true;
						Device.BeginInvokeOnMainThread(delegate {
							Content=null;
						});
					}
				};
				client.Error = delegate(Exception obj) {
					Device.BeginInvokeOnMainThread(async delegate() {							
						await DisplayAlert("Connection failed","Connection failed on \""+realAddress+"\".\n"+obj.Message,"Ok");
					});
					Debug.WriteLine(obj);
				};
				client.MessageReceived = delegate(string message) {
					if (!receivedMessage){
						receivedMessage=true;
						Device.BeginInvokeOnMainThread(delegate {
							Content=null;
						});
					}
					string xaml=message;
					Device.BeginInvokeOnMainThread(delegate() {
						try{
							xamlDep.LoadFromXaml(this,xaml);
						} catch (TargetInvocationException ex){
							ShowXamlError(client,ex.InnerException);
							Debug.WriteLine(ex);
						} catch (Exception ex){
							ShowXamlError(client,ex);
							Debug.WriteLine(ex);
						}
					});
				};
				client.Open(realAddress);
			} catch (WebException ex){
				Device.BeginInvokeOnMainThread (async delegate() {							
					await DisplayAlert ("Connection failed", "Please ensure that you have the server running.", "Ok");
				});
			} catch (Exception ex){
				Device.BeginInvokeOnMainThread (async delegate() {
					await DisplayAlert ("Exception", ex.ToString (), "Ok");
				});
				Debug.WriteLine (ex);
			}
		}

		string GetWSAddress (string address, int port)
		{
			string realAddress=address;
			if (!realAddress.StartsWith("ws://") && !realAddress.StartsWith("wss://")){
				realAddress="ws://"+address;
			}
			if (!realAddress.Substring(3).Contains(":")){
				if (realAddress.EndsWith("/")){
					realAddress=realAddress.Substring(0,realAddress.Length-1)+":"+port.ToString()+"/";
				} else {
					realAddress+=":"+port.ToString()+"/";
				}
			} else{
				if (!realAddress.EndsWith("/")){
					realAddress+="/";
				}
			}
			return realAddress;
		}

		void ShowXamlError (IWebSocketClient client, Exception ex)
		{
			Device.BeginInvokeOnMainThread(async delegate() {
				var result=await DisplayAlert("Exception on XAML",ex.Message,"Report to server","Ok");
				if (result){
					client.Send(ex.ToString());
				}
			});
		}

		StackLayout StartLayout ()
		{
			var menuLayout = new StackLayout{
				HeightRequest=150,
				VerticalOptions=LayoutOptions.CenterAndExpand,
				HorizontalOptions=LayoutOptions.FillAndExpand
			};
			var ipLbl = new Label {
				VerticalOptions=LayoutOptions.CenterAndExpand,
				Text = "Ip Address:",
				WidthRequest=100
			};
			var ipEntry = new Entry {
				HorizontalOptions=LayoutOptions.FillAndExpand,
				Text="127.0.0.1"
			};
			var layout= new StackLayout{
				HeightRequest=40,
				Orientation=StackOrientation.Horizontal,
				HorizontalOptions=LayoutOptions.FillAndExpand,
				Children={
					ipLbl,ipEntry
				}
			};
			menuLayout.Children.Add (layout);
			var porLbl = new Label {
				VerticalOptions=LayoutOptions.CenterAndExpand,
				Text = "Port:",
				WidthRequest=100,
			};
			var portEntry = new Entry {
				HorizontalOptions=LayoutOptions.FillAndExpand,
				Text="9934"
			};
			layout= new StackLayout{
				HeightRequest=40,
				Orientation=StackOrientation.Horizontal,
				HorizontalOptions=LayoutOptions.FillAndExpand,
				Children={
					porLbl,portEntry
				}
			};
			menuLayout.Children.Add (layout);
			var connectButton = new Button {
				Text="Connect",
				TextColor=Color.White,
				BackgroundColor=Color.Gray
			};
			connectButton.Clicked += delegate(object sender, EventArgs e) {
				ConnectServer(ipEntry.Text,int.Parse(portEntry.Text));
			};
			layout= new StackLayout{
				HeightRequest=40,
				Padding=new Thickness(50,0,50,0),
				HorizontalOptions=LayoutOptions.FillAndExpand,
				Children={
					connectButton
				}
			};
			menuLayout.Children.Add (layout);
			return new StackLayout {
				VerticalOptions=LayoutOptions.FillAndExpand,
				HorizontalOptions=LayoutOptions.FillAndExpand,
				Children={
					menuLayout
				}
			};
		}
	}
}


