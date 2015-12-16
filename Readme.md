#LiveXaml

The idea of this project is to allow you to easily make designs in **XAML** on Xamarin.Forms by editing your XAML’s directly on your preferred IDE (Xamarin Studio for example) and immediately preview those changes on simulators and/or devices, without needing to build your Xamarin.Foms project all over again!

The main idea to make this work was to use what is already provided on [DynamicForms](https://github.com/MobileEssentials/DynamicForms) together with an WebSocket server running on the computer side, that would notify the iOS/Android LiveXaml App each time that a specific XAML file is saved.
For that and, in order to support both Windows and Mac OS X, I used JAVA together with the library [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket).

##Requirements
* Xamarin Studio or Visual Studio
* Java Runtime Environment 7+
* Eclipse for Java (optional)

##Notes

* This project can only make live preview of XAML pages. If you need to change something on code that can’t be done on the XAML side, then you will need to make the normal rebuild of your project.
* It is recommended that you start your project over the LiveXaml App. This way, if you build custom views, you will be able to use them also on the Xaml side (but you will need first to build/rebuild the code until those custom views work well on their code implementation).
* When running on device, always ensure that your device is connected to the same network of your computer.

##Video instructions

[Coming soon]

## How to build

If you just want to use the project, you can go to the [downloads section](https://github.com/reyalpsirc/LiveXaml/releases) and download the latest ZIP file available. 
After extracting the ZIP, you will find 2 folders. One is the `Server`and contains a runnable JAR file that you will need to run on Terminal/Command Line. The other is `App` and contains the Xamarin template project that you will have to run on simulator/device in order to start previewing the XAML file that you're currently editing.
Anyway, if you want/need to modify something on the project, here are the steps to build it:

####Server
1. Download/clone the repository on your computer.
2. Download and install Eclipse for Java if you don’t have it yet.
3. Open Eclipse and go to “File” -> “Import” -> “General” -> “Existing projects into workspace”
4. Right click on the imported project and select “Export” -> “Java” -> “Runnable JAR file” and select “Next”
5. Select a folder to where you want to export the project under “Export destination” and click Finish.

####App

1. Just open the LiveXaml App project on Xamarin Studio or Visual Studio and run it to a simulator or device.

##How to run

1. Open the command line/terminal and write `ipconfig` (Windows) or `ifconfig` (Mac) and take note of your **ip address**.
2. Now write `cd path/to/LiveXaml.jar/folder`
3. Run the following command: `LiveXaml.jar /path/to/MyPage.xaml`
4. If you have issues running that command, please specify a port after the path to the Xaml page.
    * Ex: `LiveXaml.jar /path/to/MyPage.xaml 10030`
5. Open the LiveXaml App project (or your own project built over LiveXaml App project, which is recomended as said on the **Notes** section) on Xamarin Studio or Visual Studio and run it to the simulator/device.
6. Input the Ip and the port that you took note before on the simulator/device and press “Connect”
7. Now simply edit the Xaml file that you input on the LiveXaml.jar command in step **3.** and you will notice that each time that you save the file, the simulator/device will update the screen with the new contents without needing to rebuild anything.

##Troubleshoot

If you have trouble connecting your device/simulator to the LiveXaml Server, please check your **Firewall** settings.





