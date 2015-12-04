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

describe smartphone app here...