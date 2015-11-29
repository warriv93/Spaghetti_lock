from bluetooth import *
import hashlib

accepted_clients = ['C0:EE:FB:20:CC:51']

# Create the RFCOMM bluetooth socket using any port number.
server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"

advertise_service( server_sock, "spaghetti_lock",
    service_id = uuid,
    service_classes = [ uuid, SERIAL_PORT_CLASS ],
    profiles = [ SERIAL_PORT_PROFILE ]
)

print ("Waiting for connection on RFCOMM channel %d" % port)

try:
  # Enter the while loop and listen for connections
	while True:
		client_sock, client_info = server_sock.accept()
		
		# Check the client address. If we don't know it, we just close the connection
		if not any(client_info[0] in s for s in accepted_clients): 
			print ("Denied connection from ", client_info)
			client_sock.close()
			continue
		
		# Ok, we got a connection request. Accept it and send welcome message.
		print ("Accepted connection from ", client_info)
		client_sock.settimeout(10.0)
		
		try:
				client_sock.send('#Hello! What\'s the magic word?')
				while True:
						data = client_sock.recv(1024)
						if len(data) == 0: break
						print ("received [%s]" % data)
						#client_sock.send(data)
						dk = hashlib.pbkdf2_hmac('sha1', data, b'salt', 100000)
						hash = binascii.hexlify(dk)
						print (hash)
		except IOError:
				pass

		print ("disconnected", client_info)

		client_sock.close()
	
except KeyboardInterrupt:
	# Close socket and quit
	stop_advertising(server_sock)
	server_sock.close()

print ("all done")