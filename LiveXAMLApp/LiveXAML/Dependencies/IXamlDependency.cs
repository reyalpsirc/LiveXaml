using System;
using Xamarin.Forms;

namespace LiveXAML
{
	public interface IXamlDependency
	{
		void LoadFromXaml<TView> (TView view, string xaml) where TView : BindableObject;
	}
}

