# Spaghetti Lock

The goal of this project is to develop a smartphone app as well as a PC tool that allows a user to unlock his/her PC with his/her phone. We are using Ubuntu 14.04 or Ubuntu 15.04 for this project. If you don't want to install it natively on your PC, please used Virtual Box to run Ubuntu in a virtual machine. 

[Virtual Box](https://www.virtualbox.org/wiki/Downloads)

[Ubuntu 14.04](http://www.ubuntu.com/download/desktop)

## Setting up blueproximity

To install blue proximity on Ubuntu start a terminal and type

```
sudo apt-get install blueproximity

```

Then, pair your phone with your PC via Bluetooth. The configuration file (standard.conf) for blue proximity is located in 

```
/home/<username>/.blueproximity
```


Open the file with an editor of your choice and change device_mac to the Bluetooth MAC address of your phone. Since the unlock command does not work in the current Ubuntu version (may be an Ubuntu bug) we use the following work-around: Replace the unlock_command option with

```
gnome-screensaver-command -d && xdotool type <your_user_password> && xdtool key Return
```

Additionally, install xdotool

```
sudo apt-get install xdotool


```

Of course, it's not a good idea to save the password in clear text in a script... However, this is just a work around. 
You can also play around with the other settings like lock_distance and lock_duration.

## PC app

The PC part of Spaghetti Lock is written in C++. It's a small application which runs in the background as a bluetooth server and waits for incoming connections. 

You can compile the program only on Ubuntu. Go to the the directory "<your_path_to_this_repo>/pc/gcc" and enter

```
make
```

If you you compile it for the first time, you may need to install some additional libraries/programs:
```
sudo apt-get install libbluetooth-dev
sudo apt-get install g++
```

To run the program, enter
```
./ms_lock
```

## Smartphone app

describe smartphone app here...