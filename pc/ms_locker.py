import hashlib
import os
import getpass
import time
import calendar
from Crypto.Cipher import AES


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
		'accepted_clients=list(default=list(''C0:EE:FB:20:CC:51''))',
		'uuid=string(default=''fa87c0d0-afac-11de-8a39-0800200c9a66'')',
		'magic=string(default="")',
    'device_mac=string(max=17,default="")',
    'lock_command=string(default=''gnome-screensaver-command -l'')',
    'unlock_command=string(default=''gnome-screensaver-command -d'')',
    'log_to_syslog=boolean(default=True)',
    'log_syslog_facility=string(default=''local7'')',
    'log_to_file=boolean(default=False)',
    'log_filelog_filename=string(default=''' + os.getenv('HOME') + '/blueproximity.log'')'
    ]
    


class Locker ():
	def __init__(self,config):
			self.config = config
			self.Stop = False
			self.dev_mac = self.config['device_mac']
			self.sock = None
			#self.logger = Logger()
			#self.logger.configureFromConfig(self.config)
			self.accepted_clients = self.config['accepted_clients']
			self.uuid = self.config['uuid']
			self.port = 0
	
	# create_connection(): Setup the bluetooth socket and advertise
	# the SDP service.
	def create_connection(self):
			# Create the RFCOMM bluetooth socket using any port number.
			# Bluetooth connection is always encrypted (security mechanism 1)
			self.sock=BluetoothSocket( RFCOMM )
			self.sock.bind(("",PORT_ANY))
			self.sock.listen(1)

			self.port = self.sock.getsockname()[1]

			advertise_service( self.sock, "spaghetti_lock",
					service_id = self.uuid,
					service_classes = [ self.uuid, SERIAL_PORT_CLASS ],
					profiles = [ SERIAL_PORT_PROFILE ]
			)

	# run(): THis is the main loop function which trys to receive data,
	# parses received messages and takes appropriate actions.
	def run(self):
		self.create_connection()
		print ("Waiting for connection on RFCOMM channel %d" % self.port)
		while True:
			# Enter the while loop and listen for connections
			try:
				client_sock, client_info = self.sock.accept()

				# Check the client address. If we don't know it, we just close the 
				# connection (security mechanism 3).
				if not any(client_info[0] in s for s in self.accepted_clients): 
					print ("Denied connection from ", client_info)
					client_sock.close()
					continue

				# Ok, we got a connection request. Accept it and send 
				# welcome message.
				print ("Accepted connection from ", client_info)
				client_sock.settimeout(20.0)

				try:
					client_sock.send('#msg#Hello! What\'s the magic word?')
					while True:
						# Try to receive data
						data = client_sock.recv(1024)
						if len(data) == 0: break
						#print ("received [%s]" % data)
						# Decode the message and try to convert it form bytes to 
						# a string.
						data = self.decode_msg(data)
						msg = ""
						try:
							msg = data.decode()
						except ValueError:
							print ("Invalid message")
							continue
							
						msg = self.remove_padding(msg)
						tokkens = msg.split('#')
						if (len(tokkens) <= 0): continue
						# Loop through message and look for known key words
						for i in range (1,len(tokkens), 2):
							key = tokkens[i]
							
							if (i + 1 >= len(tokkens)): 
								break;
							
							# Normal message, just ouput it
							if (key == 'msg'):
								print("Received message: " + tokkens[i+1])
							
							# Magic message, check key and if it matches, unlock 
							# the screen
							elif (key == 'magic'):
								if (tokkens[i+1] == config['magic']):
									self.unlock_pc()
								else: 
									print('received wrong password')
								
							# Loop message, just return the data to the sender
							elif (key == 'loop'):
								client_sock.send(data)
								
							else:
								print('unknown message')
							
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
			
	# remove_padding: Messages are padded with '#' to the next value of
	# AES.block_size. Remove all '#' at the end of the message.
	def remove_padding(self, msg):
		while (len(msg) > 0 and msg[len(msg)-1] == '#'):
			msg = msg[:-1]
		return msg

	# lock_pc(): Lock the the PC using the command from the configuration file
	def lock_pc(self):
		print ("Locking PC")
		ret_val = os.popen(self.config['lock_command']).readlines()
			
	# unlock_pc(): Unlock the PC using the command from the configuration
	# file. 
	def unlock_pc(self):
		print ("Unlocking PC")
		ret_val = os.popen(self.config['unlock_command']).readlines()
			
	# decode_msg(): Try to decode the received message using the current time
	# as AES key. 
	def decode_msg(self, msg):
		# First, generate the key based on current time. We use three 
		# tries (3 consecutive second values) to decode the message
		base = calendar.timegm(time.gmtime()) - 3
		for i in range(0,5):
			key = str(base)
			while (len(key) < 16): key = key + '0'
			iv = str(base)
			while (len(iv) < AES.block_size): iv = iv + '0'
			
			try:
				cipher = AES.new(key.encode(), AES.MODE_CBC, iv.encode())
				data = cipher.decrypt(msg)
			except ValueError:
				print('decryption failed')
				
			# Check if the first character is '#'. If yes, we assume that
			# decoding was successful
			if (data[0] == 35): break
			# Increase key by one (one second later) and try again
			base = base + 1
			
		return data

## Main part

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
			
		# Hash the new password and save it (https://docs.python.org/2/library/hashlib.html)
		dk = hashlib.pbkdf2_hmac('sha1', pass1.encode(), b'momsspaghetti', 10000, 32)
		config['magic'] = binascii.hexlify(dk).decode()
else:
		print ('Found config in ' + config_file)
		
vdt = Validator()
config.validate(vdt, copy=True)
config.write()

l = Locker(config)
l.run()

