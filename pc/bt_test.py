from bluetooth import *

server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

#uuid = "00001101-0000-1000-8000-00805F9B34FB"
uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"

advertise_service( server_sock, "moms_spaghetti",
    service_id = uuid,
    service_classes = [ uuid, SERIAL_PORT_CLASS ],
    profiles = [ SERIAL_PORT_PROFILE ]
)

print "Waiting for connection on RFCOMM channel %d" % port

client_sock, client_info = server_sock.accept()
print "Accepted connection from ", client_info

try:
    while True:
        data = client_sock.recv(1024)
        if len(data) == 0: break
        print "received [%s]" % data
except IOError:
    pass

print "disconnected"

client_sock.close()
server_sock.close()
print "all done"