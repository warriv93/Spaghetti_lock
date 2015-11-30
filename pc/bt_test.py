import hashlib
import os
import getpass

try:
		from configobj import ConfigObj
		from validate import Validator
except:
		print ("The program cannot import the module ConfigObj or Validator.")
		print ("Please make sure the ConfigObject package for python is installed.")
		print ("e.g. with Ubuntu Linux, type")
		print (" sudo apt-get install python-configobj")
		sys.exit(1)
try:
		from bluetooth import *
except:
		print ("The program cannot import the module bluetooth.")
		print ("Please make sure the bluetooth bindings for python as well as bluez are installed.")
		print ("e.g. with Ubuntu Linux, type")
		print (" sudo apt-get install python-bluez")
		sys.exit(1)

## Setup config file specs and defaults
# This is the ConfigObj's syntax
conf_specs = [
		'uuid=string(default=''fa87c0d0-afac-11de-8a39-0800200c9a66'')',
		'magic=string(default="")',
    'device_mac=string(max=17,default="")',
    'device_channel=integer(1,30,default=7)',
    'lock_distance=integer(0,127,default=7)',
    'lock_duration=integer(0,120,default=6)',
    'unlock_distance=integer(0,127,default=4)',
    'unlock_duration=integer(0,120,default=1)',
    'lock_command=string(default=''gnome-screensaver-command -l'')',
    'unlock_command=string(default=''gnome-screensaver-command -d'')',
    'proximity_command=string(default=''gnome-screensaver-command -p'')',
    'proximity_interval=integer(5,600,default=60)',
    'log_to_syslog=boolean(default=True)',
    'log_syslog_facility=string(default=''local7'')',
    'log_to_file=boolean(default=False)',
    'log_filelog_filename=string(default=''' + os.getenv('HOME') + '/blueproximity.log'')'
    ]
    


class Locker ():
		def __init__(self,config):
				self.config = config
				self.Dist = -255
				self.Simulate = False
				self.Stop = False
				self.dev_mac = self.config['device_mac']
				self.dev_channel = self.config['device_channel']
				self.gone_duration = self.config['lock_duration']
				self.gone_limit = -self.config['lock_distance']
				self.active_duration = self.config['unlock_duration']
				self.active_limit = -self.config['unlock_distance']
				self.sock = None
				self.ignoreFirstTransition = True
				#self.logger = Logger()
				#self.logger.configureFromConfig(self.config)
				self.accepted_clients = ['C0:EE:FB:20:CC:51']
				self.uuid = self.config['uuid']
				self.port = 0
    

		def create_connection(self):
				# Create the RFCOMM bluetooth socket using any port number.
				self.sock=BluetoothSocket( RFCOMM )
				self.sock.bind(("",PORT_ANY))
				self.sock.listen(1)

				self.port = self.sock.getsockname()[1]

				advertise_service( self.sock, "spaghetti_lock",
						service_id = self.uuid,
						service_classes = [ self.uuid, SERIAL_PORT_CLASS ],
						profiles = [ SERIAL_PORT_PROFILE ]
				)

		def run(self):
				self.create_connection()
				print ("Waiting for connection on RFCOMM channel %d" % self.port)

				while True:
					# Enter the while loop and listen for connections
					try:
						client_sock, client_info = self.sock.accept()
		
						# Check the client address. If we don't know it, we just close the connection
						if not any(client_info[0] in s for s in self.accepted_clients): 
							print ("Denied connection from ", client_info)
							client_sock.close()
							continue
		
						# Ok, we got a connection request. Accept it and send welcome message.
						print ("Accepted connection from ", client_info)
						client_sock.settimeout(20.0)
		
						try:
								client_sock.send('#msg#Hello! What\'s the magic word?')
								while True:
										data = client_sock.recv(1024)
										if len(data) == 0: break
										print ("received [%s]" % data)
										
										#client_sock.send(data)
						except IOError:
								pass

						print ("disconnected", client_info)

						client_sock.close()
	
					except KeyboardInterrupt:
						break
						
					# Close socket and quit
				stop_advertising(self.sock)
				self.sock.close()

				print ("cleaning up...")
				return 0

		def lock_pc(self):
				print ("Locking PC")
				ret_val = os.popen(self.config['lock_command']).readlines()

## Main part

# react on ^C
#signal.signal(signal.SIGINT, signal.SIG_DFL)

new_config = False
config_file = os.getenv('HOME') + '/.locker/standard.conf'
try:
		config = ConfigObj(config_file,{'create_empty':False,'file_error':True,'configspec':conf_specs})
except:
		new_config = True
		
if new_config:

		# Ok, we didn't find a configuration. Create a new configuration and ask user 
		# for password before continuing
		print ('Creating new config in ' + config_file)
		config = ConfigObj(config_file,{'create_empty':True,'file_error':False,'configspec':conf_specs})
		# next line fixes a problem with creating empty strings in default values for configobj
		config['device_mac'] = ''
		
		# Now get the password
		while True:
			print('Please enter a new password')
			pass1 = getpass.getpass()
			pass2 = getpass.getpass('Retype:')
			
			if pass1 == pass2: break
			else: print('Error: Passwords do not match')
			
		# Hash the new password and save it
		dk = hashlib.pbkdf2_hmac('sha1', pass1.encode(), b'momsspaghetti', 10000, 64)
		config['magic'] = dk
		print( binascii.hexlify(config['magic']))
else:
		print ('Found config in ' + config_file)
		
vdt = Validator()
config.validate(vdt, copy=True)
config.write()

l = Locker(config)
l.run()

