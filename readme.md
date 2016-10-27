# Spaghetti Lock

The goal of this project is to develop a smartphone app as well as a PC tool that allows a user to unlock his/her PC with his/her phone. We are using Ubuntu 14.04 or Ubuntu 15.04 for this project. If you don't want to install it natively on your PC, please used Virtual Box to run Ubuntu in a virtual machine. 

[Virtual Box](https://www.virtualbox.org/wiki/Downloads)

[Ubuntu 14.04](http://www.ubuntu.com/download/desktop)


## PC app

The PC part of Spaghetti Lock is written in Python. It's a small program which runs in the background as a bluetooth server and waits for incoming connections. 

You can run the program only on Ubuntu. Go to the the directory "<your_path_to_this_repo>/pc" and enter

```
python3 ms_locker.py
```

It may be necessary to install a few additional Python libraries:
```
sudo apt-get install python3-pip
sudo pip3 install configobj
sudo pip3 install PyBluez
```


## Smartphone app
The Fingerprint part of our Android Application is built in Java. Its tested and works very well with the Emulator on Android 6. Unfortunately the current Android release on all our phones is still on version 5. 
We there for decided to keep this in a seperate repository, and implement it in a future update.

The Password part of our application is also built for Android and written in Java. The application contains basically one main controller which has all the logic for the applications UI Thread. 
It has 4 fragments which represents the different pages with layout and basic lagic. In the Password fragment there is some logic for encrypting the password with AES and CBC in a ASyncTask.
There is also 2 Thread classes. First one is for listening for a connection with the PC. The second one represents a Connected (Thread) Bluetooth client. When a connection to the PC is established this thread will handle all the READ and WRITE to and from the PC.

![lock](http://simon.brasse-pc.eu/portfolio/images/lock/lock.jpg)