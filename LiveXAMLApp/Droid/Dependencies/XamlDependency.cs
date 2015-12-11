using System;
using System.Linq;
using System.Reflection;
using Xamarin.Forms;
using LiveXAML.Droid;

[assembly: Xamarin.Forms.Dependency (typeof (XamlDependency))]
namespace LiveXAML.Droid
{
	public class XamlDependency:IXamlDependency
	{
		static Func<BindableObject, string, BindableObject> loadXaml;
		public XamlDependency ()
		{
			// This is the current situation, where the LoadFromXaml is the only non-public static method.
			var genericMethod = typeof (Xamarin.Forms.Xaml.Extensions)
				.GetMethods (BindingFlags.Static | BindingFlags.NonPublic).FirstOrDefault ();

			// If we didn't find it, it may be because the extension method may be public now :)
			if (genericMethod == null)
				genericMethod = typeof (Xamarin.Forms.Xaml.Extensions)
					.GetMethods (BindingFlags.Static | BindingFlags.Public)
					.FirstOrDefault (m => m.GetParameters().Last().ParameterType == typeof(string));

			if (genericMethod == null){
				loadXaml = (view, xaml) => { throw new NotSupportedException("Xamarin.Forms implementation of XAML loading not found. Please update the Dynamic nuget package."); };
			}
			else {
				genericMethod = genericMethod.MakeGenericMethod(typeof(BindableObject));
				loadXaml = (view, xaml) => (BindableObject)genericMethod.Invoke (null, new object[] { view, xaml });
			}
		}

		#region IXamlDependency implementation

		public void LoadFromXaml<TView> (TView view, string xaml) where TView : Xamarin.Forms.BindableObject
		{
			loadXaml (view, xaml);
		}

		#endregion
	}
}

